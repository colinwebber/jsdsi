/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;

import jsdsi.AuthCert;
import jsdsi.Principal;
import jsdsi.Provider;
import jsdsi.Signature;
import jsdsi.StringTag;
import jsdsi.Subject;
import jsdsi.Tag;
import jsdsi.Validity;
import jsdsi.util.DateUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;
import junit.framework.TestCase;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2005/02/17 16:47:14 $
 *
 */
public class KeyStoreTest extends TestCase {

 

	static {
        Provider.install();
    }

    private static final int NUM = 2;
    
    private KeyPair[] kp = new KeyPair[NUM];
    
    private File file = new File("target", "test.sks");
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        KeyStore store = KeyStore.getInstance("SPKI");
        store.load(null, null);
        
        for (int n=0; n<NUM; n++) {
            kp[n] = KeyPairFactory.create(KeyEnum.RSA, 512);
            createPrivateKeyEntry(n, kp[n], store);
        }
        
        for (int n=0; n<NUM; n++) {
            createPublicKeyEntry(n, kp[n], store);
        }
        
        for (int n=0; n<NUM; n++) {
            createCertificateEntry(n, kp[n], store);
        }
        
        FileOutputStream fos = new FileOutputStream(file);
        store.store(fos, "store".toCharArray());
        fos.close();
    }
    
    private void createPrivateKeyEntry(int num, KeyPair kp, KeyStore ks) throws Exception {
        Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
        Tag t = new StringTag("autocert");
        boolean p = false;
        AuthCert cert = new AuthCert((Principal)kp.getPublic(), (Subject)kp.getPublic(),v, "test/plan", "private", t, p);
        Signature sig = Signature.create(kp, cert, DigestAlgoEnum.MD5);
        Certificate auto = new jsdsi.Certificate(cert, sig);
        ks.setKeyEntry("priv"+num, kp.getPrivate(), "key".toCharArray(), new Certificate[] {auto});
    }
    
    private void createPublicKeyEntry(int num, KeyPair kp, KeyStore ks) throws Exception {
        Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
        Tag t = new StringTag("autocert");
        boolean p = false;
        AuthCert cert = new AuthCert((Principal)kp.getPublic(), (Subject)kp.getPublic(),v, "test/plan", "public", t, p);
        Signature sig = Signature.create(kp, cert, DigestAlgoEnum.MD5);
        Certificate auto = new jsdsi.Certificate(cert, sig);
        ks.setKeyEntry("pub"+num, kp.getPublic(), "key".toCharArray(), new Certificate[] {auto});
    }
    
    private void createCertificateEntry(int num, KeyPair kp, KeyStore ks) throws Exception {
        Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
        Tag t = new StringTag("autocert");
        boolean p = false;
        AuthCert cert = new AuthCert((Principal)kp.getPublic(), (Subject)kp.getPublic(),v, "test/plan", "certificate", t, p);
        Signature sig = Signature.create(kp, cert, DigestAlgoEnum.MD5);
        Certificate auto = new jsdsi.Certificate(cert, sig);
        ks.setCertificateEntry("cert"+num, auto);
    }
    
    public void testPrivateKey() throws Exception {
        KeyStore store = KeyStore.getInstance("SPKI");
        store.load(new FileInputStream(file), "store".toCharArray());
        
        for (int n=0; n<NUM; n++) {
            String alias = "priv"+n;
            Key key = store.getKey(alias, "key".toCharArray());
            assertEquals(alias, kp[n].getPrivate(), key);
            Certificate[] chain = store.getCertificateChain(alias);
            assertEquals(alias, 1, chain.length);
        }
        
        for (int n=0; n<NUM; n++) {
            String alias = "priv"+n;
            try {
                Key privKey = store.getKey(alias, "dud".toCharArray());
                fail("PrivateKey retrieved with incorrect password, "+alias);
            } catch(UnrecoverableKeyException e) {
                // expected as incorrect password
            }
        }
        
    }
    
    public void testPublicKey() throws Exception {
        KeyStore store = KeyStore.getInstance("SPKI");
        store.load(new FileInputStream(file), "store".toCharArray());
        
        for (int n=0; n<NUM; n++) {
            String alias = "pub"+n;
            Key key = store.getKey(alias, "key".toCharArray());
            assertEquals(alias, kp[n].getPublic(), key);
            Certificate[] chain = store.getCertificateChain(alias);
            assertEquals(alias, 1, chain.length);
        }
    }
    
    public void testCertificate() throws Exception {
        KeyStore store = KeyStore.getInstance("SPKI");
        store.load(new FileInputStream(file), "store".toCharArray());
        
        for (int n=0; n<NUM; n++) {
            String alias = "cert"+n;
            Certificate out = store.getCertificate(alias);
            assertEquals(alias, kp[n].getPublic(), out.getPublicKey());
        }
    }
    
    public void testLoadStoreWithInvalidPassword() throws Exception {
        KeyStore store = KeyStore.getInstance("SPKI");
        try {
            store.load(new FileInputStream(file), "dud".toCharArray());
            fail("KeyStore loaded with invalid password");
        } catch (IOException e) {
            // expected as invalid password
        }
    }
    
}
