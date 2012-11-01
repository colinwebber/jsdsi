/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import jsdsi.Util;

/**
 * SPI for JCE KeyStore that handles JSDSI keys.
 * 
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2005/02/17 19:54:55 $
 *  
 */
public class KeyStoreSpi extends java.security.KeyStoreSpi {

    private static final int SALT_SIZE = 8;

    private final int MIN_ITERATIONS = 1024;

    private final String KEY_ALGO = "PBEWithMD5AndDES";

    private final String MAC_ALGO = "HmacSHA1";

    private static final int ENTRY_TYPE_KEY = 1;

    private static final int ENTRY_TYPE_CERT = 2;

    private Hashtable table = new Hashtable();

    private SecureRandom random = new SecureRandom();

    /**
     *  
     */
    public KeyStoreSpi() {
        super();
    }

    /**
     * @see java.security.KeyStoreSpi#engineSize()
     */
    public int engineSize() {
        return table.size();
    }

    /**
     * @see java.security.KeyStoreSpi#engineLoad(java.io.InputStream, char[])
     */
    public void engineLoad(InputStream stream, char[] password)
            throws IOException, NoSuchAlgorithmException, CertificateException {
        this.table.clear();
        if (stream == null) {
            // just initialising
            return;
        }
        DataInputStream dis = new DataInputStream(stream);
        byte[] salt = new byte[dis.readInt()];
        dis.readFully(salt);
        int iterationCount = dis.readInt();

        Mac mac = createMac(password, salt, iterationCount);

        MacInputStream mis = new MacInputStream(dis, mac);
        loadStore(mis);

        byte[] val = mac.doFinal();
        byte[] storeVal = new byte[val.length];
        try {
            dis.readFully(storeVal);
        } catch (EOFException e) {
            throw new IOException("MAC failure");
        }

        mis.close();
        if (!Util.equals(val, storeVal)) {
            throw new IOException("MAC failure");
        }
    }

    /**
     * @see java.security.KeyStoreSpi#engineStore(java.io.OutputStream, char[])
     */
    public void engineStore(OutputStream stream, char[] password)
            throws IOException, NoSuchAlgorithmException, CertificateException {
        DataOutputStream dos = new DataOutputStream(stream);
        byte[] salt = new byte[SALT_SIZE];
        int iterationCount = MIN_ITERATIONS + (random.nextInt() & 0x3ff);

        random.nextBytes(salt);
        dos.writeInt(salt.length);
        dos.write(salt);
        dos.writeInt(iterationCount);

        Mac mac = createMac(password, salt, iterationCount);

        MacOutputStream mos = new MacOutputStream(stream, mac);
        saveStore(mos);

        byte[] val = mac.doFinal();

        dos.write(val);
        dos.close();
    }

    private Mac createMac(char[] password, byte[] salt, int iterationCount)
            throws NoSuchAlgorithmException, IOException {
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance(KEY_ALGO);
        Key passKey;
        try {
            passKey = keyFact.generateSecret(pbeSpec);
        } catch (InvalidKeySpecException e) {
            throw new IOException(e.getMessage());
        }
        PBEParameterSpec defParams = new PBEParameterSpec(salt, iterationCount);

        Mac mac = Mac.getInstance(MAC_ALGO);
        try {
            //mac.init(passKey, defParams);
            mac.init(passKey, null);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new IOException(e.getMessage());
        }
        return mac;
    }

    /**
     * @see java.security.KeyStoreSpi#engineDeleteEntry(java.lang.String)
     */
    public void engineDeleteEntry(String alias) throws KeyStoreException {
        this.table.remove(alias);
    }

    /**
     * @see java.security.KeyStoreSpi#engineContainsAlias(java.lang.String)
     */
    public boolean engineContainsAlias(String alias) {
        return this.table.contains(alias);
    }

    /**
     * @see java.security.KeyStoreSpi#engineIsCertificateEntry(java.lang.String)
     */
    public boolean engineIsCertificateEntry(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null) {
            return false;
        }
        return entry.type == ENTRY_TYPE_CERT;
    }

    /**
     * @see java.security.KeyStoreSpi#engineIsKeyEntry(java.lang.String)
     */
    public boolean engineIsKeyEntry(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null) {
            return false;
        }
        return entry.type == ENTRY_TYPE_KEY;
    }

    /**
     * @see java.security.KeyStoreSpi#engineAliases()
     */
    public Enumeration engineAliases() {
        return this.table.keys();
    }

    /**
     * @see java.security.KeyStoreSpi#engineGetCertificateAlias(java.security.cert.Certificate)
     */
    public String engineGetCertificateAlias(Certificate cert) {
        if (true)
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
        return null;
    }

    /**
     * @see java.security.KeyStoreSpi#engineGetKey(java.lang.String, char[])
     */
    public Key engineGetKey(String alias, char[] password)
            throws NoSuchAlgorithmException, UnrecoverableKeyException {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null) {
            return null;
        }
        if (entry.type == ENTRY_TYPE_KEY) {
            return entry.getKey(password);
        } else {
            throw new UnrecoverableKeyException("Not a key: " + alias);
        }
    }

    /**
     * @see java.security.KeyStoreSpi#engineGetCertificate(java.lang.String)
     */
    public Certificate engineGetCertificate(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null) {
            return null;
        }
        return entry.chain[0];
    }

    /**
     * @see java.security.KeyStoreSpi#engineGetCertificateChain(java.lang.String)
     */
    public Certificate[] engineGetCertificateChain(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null) {
            return entry.chain;
        }
        return null;
    }

    /**
     * @see java.security.KeyStoreSpi#engineSetCertificateEntry(java.lang.String,
     *          java.security.cert.Certificate)
     */
    public void engineSetCertificateEntry(String alias, Certificate cert)
            throws KeyStoreException {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry != null) {
            throw new KeyStoreException("Entry already exists with alias, "
                    + alias);
        }
        entry = new StoreEntry(alias, cert);
        this.table.put(alias, entry);
    }

    /**
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String,
     *          byte[], java.security.cert.Certificate[])
     */
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain)
            throws KeyStoreException {
        if (true)
            throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    /**
     * @see java.security.KeyStoreSpi#engineGetCreationDate(java.lang.String)
     */
    public Date engineGetCreationDate(String alias) {
        StoreEntry entry = (StoreEntry) this.table.get(alias);
        if (entry == null) {
            return null; 
        }
        return entry.created;
    }

    /**
     * @see java.security.KeyStoreSpi#engineSetKeyEntry(java.lang.String,
     *          java.security.Key, char[], java.security.cert.Certificate[])
     */
    public void engineSetKeyEntry(String alias, Key key, char[] password,
            Certificate[] chain) throws KeyStoreException {
        if ((key instanceof PrivateKey) && (chain == null)) {
            throw new KeyStoreException("no certificate chain for private key");
        }

        StoreEntry entry = (StoreEntry) table.get(alias);

        if (entry != null) {
            throw new KeyStoreException(
                    "Alias already exists, " + alias);
        }

        try {
            if (key instanceof PrivateKey) {
                if (password == null) {
                    throw new KeyStoreException(
                            "Must specify a password to encrypt PrivateKey.");
                }
                entry = new StoreEntry(alias, (PrivateKey) key, password, chain);
            } else {
                entry = new StoreEntry(alias, (PublicKey) key, chain);

            }
        } catch (IOException e) {
            throw new KeyStoreException(e.getMessage());
        }

        table.put(alias, entry);
    }

    private void saveStore(OutputStream stream)
            throws CertificateEncodingException, IOException {
        DataOutputStream out = new DataOutputStream(stream);
        out.writeInt(this.table.size());
        Iterator it = this.table.values().iterator();
        while (it.hasNext()) {
            StoreEntry entry = (StoreEntry) it.next();
            entry.write(out);
        }
    }

    private void loadStore(InputStream stream) throws CertificateException,
            IOException {
        DataInputStream in = new DataInputStream(stream);
        int num = in.readInt();
        int n = 0;
        while (n < num) {
            try {
                StoreEntry entry = new StoreEntry(in);
                this.table.put(entry.alias, entry);
                n++;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private class StoreEntry {
        private final int KEY_PRIVATE = 1;

        private final int KEY_PUBLIC = 2;

        String alias;

        Date created;

        int type;

        boolean encrypted;

        byte[] data;

        Certificate[] chain;

        StoreEntry(String a, PublicKey k, Certificate[] c) throws IOException {
            this.alias = a;
            this.created = new Date();
            this.type = ENTRY_TYPE_KEY;
            this.encrypted = false;
            this.data = encodeKey(k);
            this.chain = c;
        }

        StoreEntry(String a, PrivateKey k, char[] password, Certificate[] c)
                throws IOException {
            this.alias = a;
            this.created = new Date();
            this.type = ENTRY_TYPE_KEY;
            this.encrypted = true;
            this.data = encrypt(encodeKey(k), password);
            this.chain = c;
        }

        StoreEntry(String a, Certificate c) {
            this.alias = a;
            this.created = new Date();
            this.type = ENTRY_TYPE_CERT;
            this.encrypted = false;
            this.data = new byte[0];
            this.chain = new Certificate[] { c };
        }

        StoreEntry(DataInputStream in) throws CertificateException,
                ClassNotFoundException, IOException {
            read(in);
        }

        private byte[] encrypt(byte[] plainTxt, char[] password)
                throws IOException {
            byte[] salt = new byte[SALT_SIZE];
            random.setSeed(System.currentTimeMillis());
            random.nextBytes(salt);
            int iterationCount = MIN_ITERATIONS + (random.nextInt() & 0x3ff);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(salt.length);
            dos.write(salt);
            dos.writeInt(iterationCount);

            Cipher cipher = makePBECipher(KEY_ALGO, Cipher.ENCRYPT_MODE,
                    password, salt, iterationCount);
            CipherOutputStream cos = new CipherOutputStream(dos, cipher);

            dos = new DataOutputStream(cos);
            dos.writeInt(plainTxt.length);
            dos.write(plainTxt);
            dos.close();
            return baos.toByteArray();
        }

        private byte[] decrypt(byte[] cipherTxt, char[] password)
                throws IOException {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
                    cipherTxt));
            byte[] salt = new byte[dis.readInt()];
            dis.readFully(salt);
            int iterationCount = dis.readInt();
            Cipher cipher = makePBECipher(KEY_ALGO, Cipher.DECRYPT_MODE,
                    password, salt, iterationCount);
            CipherInputStream cis = new CipherInputStream(dis, cipher);
            dis = new DataInputStream(cis);
            int len = dis.readInt();
            if (len < 1 || len > 1024) {
                throw new IOException("invalid");
            }
            byte[] plainTxt = new byte[len];
            dis.readFully(plainTxt);
            dis.close();
            return plainTxt;
        }

        private Cipher makePBECipher(String algorithm, int mode,
                char[] password, byte[] salt, int iterationCount)
                throws IOException {
            try {
                PBEKeySpec pbeSpec = new PBEKeySpec(password);
                SecretKeyFactory keyFact = SecretKeyFactory
                        .getInstance(algorithm);
                PBEParameterSpec defParams = new PBEParameterSpec(salt,
                        iterationCount);

                Cipher cipher = Cipher.getInstance(algorithm);

                cipher.init(mode, keyFact.generateSecret(pbeSpec), defParams);

                return cipher;
            } catch (Exception e) {
                throw new IOException("Error initialising Cipher for KeyStore.");
            }
        }

        private void write(DataOutputStream out)
                throws CertificateEncodingException, IOException {
            out.writeUTF(this.alias);
            out.writeLong(this.created.getTime());
            out.writeInt(this.type);
            out.writeBoolean(this.encrypted);
            out.writeInt(this.data.length);
            if (this.data.length > 0) {
                out.write(this.data);
            }
            out.writeInt(this.chain.length);
            for (int num = 0; num < this.chain.length; num++) {
                byte[] bytes = encodeCertificate(this.chain[num]);
                out.writeInt(bytes.length);
                out.write(bytes);
            }
            out.flush();
        }

        private void read(DataInputStream in) throws ClassNotFoundException,
                CertificateException, IOException {
            this.alias = in.readUTF();
            this.created = new Date(in.readLong());
            this.type = in.readInt();
            this.encrypted = in.readBoolean();
            int len = in.readInt();
            this.data = new byte[len];
            if (len > 0) {
                in.read(this.data);
            }
            this.chain = new Certificate[in.readInt()];
            for (int num = 0; num < this.chain.length; num++) {
                byte[] bytes = new byte[in.readInt()];
                in.read(bytes);
                this.chain[num] = decodeCertificate(bytes);
            }
        }

        private byte[] encodeKey(Key k) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            if (k instanceof PrivateKey) {
                out.writeInt(KEY_PRIVATE);
            } else if (k instanceof PublicKey) {
                out.writeInt(KEY_PUBLIC);
            }
            out.writeUTF(k.getFormat());
            out.writeUTF(k.getAlgorithm());
            byte[] enc = k.getEncoded();
            out.writeInt(enc.length);
            out.write(enc);
            out.close();
            return baos.toByteArray();
        }

        private Key decodeKey(byte[] bytes) throws IOException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                    bytes));
            int type = in.readInt();
            String format = in.readUTF();
            String algo = in.readUTF();
            byte[] enc = new byte[in.readInt()];
            in.readFully(enc);
            KeySpec spec = null;
            if (format.equals("PKCS#8") || format.equals("PKCS8")) {
                spec = new PKCS8EncodedKeySpec(enc);
            } else if (format.equals("SEXP")) {
                spec = new jsdsi.sexp.KeySpec(enc);
                algo = "SPKI/SEXP";
            } else if (format.equals("X.509") || format.equals("X509")) {
                spec = new X509EncodedKeySpec(enc);
            } else {
                throw new IOException("Key format not recognised: " + format);
            }
            try {
                switch (type) {
                case KEY_PRIVATE:
                    return KeyFactory.getInstance(algo).generatePrivate(spec);
                case KEY_PUBLIC:
                    return KeyFactory.getInstance(algo).generatePublic(spec);
                default:
                    throw new IOException("Unknown Key type: " + type);
                }
            } catch (Exception e) {
                throw new IOException("Exception creating key: " + e.toString());
            }
        }

        private byte[] encodeCertificate(Certificate c) throws IOException,
                CertificateEncodingException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeUTF(c.getType());
            byte[] enc = c.getEncoded();
            out.writeInt(enc.length);
            out.write(enc);
            out.close();
            return baos.toByteArray();
        }

        private Certificate decodeCertificate(byte[] bytes) throws IOException,
                CertificateException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(
                    bytes));
            String type = in.readUTF();
            byte[] enc = new byte[in.readInt()];
            in.readFully(enc);
            CertificateFactory f = CertificateFactory.getInstance(type);
            return f.generateCertificate(new ByteArrayInputStream(enc));
        }

        public Key getKey(char[] password) throws UnrecoverableKeyException {
            if (this.type == ENTRY_TYPE_CERT) {
                throw new UnrecoverableKeyException("Not a key");
            }
            byte[] bytes = null;
            if (this.encrypted) {
                try {
                    bytes = decrypt(this.data, password);
                } catch (IOException e) {
                    throw new UnrecoverableKeyException(e.getMessage());
                }
            } else {
                bytes = this.data;
            }
            try {
                return decodeKey(bytes);
            } catch (IOException e) {
                throw new UnrecoverableKeyException(e.getMessage());
            }
        }
    }

}