/*
 * Copyright 2002 Massachusetts Institute of Technology
 *   
 * Permission to use, copy, modify, and distribute this program for any
 * purpose and without fee is hereby granted, provided that this
 * copyright and permission notice appear on all copies and supporting
 * documentation, the name of M.I.T. not be used in advertising or
 * publicity pertaining to distribution of the program without specific
 * prior permission, and notice be given in supporting documentation that
 * copying and distribution is by permission of M.I.T.  M.I.T. makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided "as is" without express or implied warranty.
 */
package jsdsi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.DigestUtils;
import jsdsi.util.KeyEnum;
import jsdsi.util.SignatureAlgoEnum;
import jsdsi.util.SignatureUtils;

/**
 * A cryptographic signature.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.8 $ $Date: 2004/11/08 12:08:08 $
 */
public class Signature extends Obj implements Element {

  private static final long serialVersionUID = -7728805907022170149L;
  
	/**
	 * The principal that represents the signer.
	 */
	private transient final Principal signer;

	/**
	 * The hash of the signed data.
	 */
	private transient final Hash signee;

	/**
	 * The signature algorithm.
	 */
	private transient final SignatureAlgoEnum algo;

	/**
	 * The value of the signature.
	 */
	private transient final byte[] data;

	/**
	 * Creates a new Signature.
	 * 
	 * @param  i the signer
	 * @param  h hash of the signed data
	 * @param  a signature algorithm
	 * @param  d signature value (<i>not</i> the data to sign)
	 * 
	 */
	public Signature(Principal i, Hash h, SignatureAlgoEnum a, byte[] d) {
		assert(i != null) : "null signer";
		assert(h != null) : "null signee";
		assert(a != null) : "null algo";
		assert(d != null) : "null data";
		signer = i;
		signee = h;
		this.algo = a;
		data = d;
	}
	/**
	 * Creates a new Signature.
	 * 
	 * @param  i the signer
	 * @param  h hash of the signed data
	 * @param  a signature algorithm
	 * @param  d signature value (<i>not</i> the data to sign)
	 * 
	 * @deprecated use {@link Signature(Principal, Hash, jsdsi.util.SignatureAlgoEnum, byte[]}
	 */
	public Signature(Principal i, Hash h, String a, byte[] d) {
		this(i, h, SignatureAlgoEnum.fromSpki(a), d);
	}

	/**
	 * Signs the given JSDSI Obj using the 'default' java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param o JSDSI Obj to sign.
	 * @param a hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, Obj o, DigestAlgoEnum a) {
		return create(kp, o.toByteArray(), a, null);
	}	
	
	/**
	 * Signs the given byte array using the 'default' java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param bytes data to sign.
	 * @param a hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, byte[] bytes, DigestAlgoEnum a) {
		return create(kp, new ByteArrayInputStream(bytes), a, null);
	}		
	
	/**
	 * Signs the given byte array using the 'default' java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param is data to sign.
	 * @param a hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, InputStream is, DigestAlgoEnum a) {
		return create(kp, is, a, null);
	}		
	
	/**
	 * Signs the given JSDSI Obj using the specified java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param o JSDSI Obj to sign.
	 * @param a hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, Obj o, DigestAlgoEnum a, String provider) {
		return create(kp, o.toByteArray(), a, provider);
	}	
	
	/**
	 * Signs the given byte array using the specified java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param bytes data to sign.
	 * @param a hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, byte[] bytes, DigestAlgoEnum a, String provider) {
		return create(kp, new ByteArrayInputStream(bytes), a, provider);
	}		
	
	/**
	 * Signs the given byte array using the specified java.security.Provider.
	 * @param kp KeyPair containing the PrivateKey to sign with.
	 * @param is data to sign.
	 * @param digestEnum hash algorythm to use. 
	 * @return the signature.
	 * @throws JsdsiRuntimeException on error
	 */
	public static Signature create(java.security.KeyPair kp, InputStream is, DigestAlgoEnum digestEnum, String provider) {
	    assert(kp != null) : "null key pair";
		assert(is != null) : "null data to sign";
		assert(digestEnum != null) : "null hash algo";
		assert(kp.getPublic() instanceof Principal) :
            "public key must be a principal";
		KeyEnum keyEnum = KeyEnum.fromJdk(kp.getPublic().getAlgorithm());
		SignatureAlgoEnum sigEnum = SignatureAlgoEnum.create(digestEnum, keyEnum );
		java.security.Signature sig = SignatureUtils.getJdkSignature(sigEnum.jdkName(), provider);
        try {
            sig.initSign(kp.getPrivate());
        } catch (InvalidKeyException e) {
            throw new JsdsiRuntimeException("Error with signing PrivateKey", e);
        }
        MessageDigest digester = DigestUtils.getDigest(digestEnum);
        Iterator it = new jsdsi.util.InputStreamIterator(is);
        while (it.hasNext()) {
            byte[] bytes = (byte[]) it.next();
            try {
                sig.update(bytes);
            } catch (SignatureException e) {
                throw new JsdsiRuntimeException("Error updating signature engine with data to sign", e);
            }
            digester.update(bytes);
        }
        byte[] d;
        try {
            d = sig.sign();
        } catch (SignatureException e) {
            throw new JsdsiRuntimeException("Error performing sign operation", e);
        }
        return new Signature((Principal) kp.getPublic(),
                new Hash(digestEnum, digester.digest(), null), sigEnum, d);
	}			
	
	
	
	
	/**
	 * Signs the given JSDSI Obj using the 'default' java.security.Provider.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param o the JSDSI Obj to sign.
	 * @param a the name of the signing algorythm to use. 
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * 
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, Obj o, String a) throws NoSuchAlgorithmException,
	InvalidKeyException, NoSuchProviderException, SignatureException {
		return create(kp, o, a, null);
	}

	/**
	 * Signs the given JSDSI Obj.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param o the JSDSI Obj to sign.
	 * @param a the name of the signing algorythm to use. 
	 * @param provider the name of the security Provider to use.
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, Obj o, String a, String provider) throws 
	NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
	    return create(kp, o.toByteArray(), a, provider);
	}

	/**
	 * Signs some data given as a <code>byte[]</code> using the 'default' java.security.Provider.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param b the data to sign.
	 * @param a the name of the signing algorythm to use. 
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, byte[] b, String a) throws NoSuchAlgorithmException,
	InvalidKeyException, NoSuchProviderException, SignatureException {
        return create(kp, b, a, null);
    }
    
	/**
	 * Signs some data given as a <code>byte[]</code>.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param b the data to sign.
	 * @param a the name of the signing algorythm to use. 
	 * @param provider the name of the security Provider to use.
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, byte[] b, String a, String provider) throws
    NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
		return create(kp, new ByteArrayInputStream(b), a, provider);
	}
    
	/**
	 * Signs some data given as an <code>java.io.InputStream</code> using the 'default' java.security.Provider.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param toSign the InputStream with the data to sign.
	 * @param a the name of the signing algorythm to use. 
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, InputStream toSign, String a) throws
	InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
	    return create(kp, toSign, a, null);	
	}
	        
	/**
	 * Signs some data given as a <code>java.io.InputStream</code>.
	 * @param kp the KeyPair containing the PrivateKey to sign with.
	 * @param toSign the InputStream with the data to sign.
	 * @param a the name of the signing algorythm to use
	 * @param provider the name of the security Provider to use.
	 * @return the signature.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchProviderException
	 * @throws SignatureException
	 * @deprecated
	 */
	public static Signature create(java.security.KeyPair kp, InputStream toSign, String a, String provider) throws
	NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
	    SignatureAlgoEnum sigEnum = SignatureAlgoEnum.fromJdk(a);
	    DigestAlgoEnum digestEnum = sigEnum.getDigestEnum();
	    return create(kp, toSign, digestEnum, provider);
	}
	
	/**
	 * Using the default security Provider, verifies that the corresponding PrivateKey for <code>key</code>
	 * generated <code>this</code> Signature for <code>o</code>
	 * @param key the PublicKey 
	 * @param o the jsdsi object that was (alledgedly) signed
	 * @return <code>true</code> if the signature verifies
	 * @throws JsdsiRuntimeException on error
	 */
	public boolean verify(java.security.PublicKey key, Obj o) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		return verify(key, o, null);
	}

	public boolean verify(java.security.PublicKey key, Obj o, String provider) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        return verify(key, o.toByteArray(), provider);
	}

	public boolean verify(java.security.PublicKey key, byte[] b) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        return verify(key, b, null);
    }
    
	public boolean verify(java.security.PublicKey key, byte[] b, String provider) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
		return verify(key, new ByteArrayInputStream(b), provider);
	}
   
	public boolean verify(java.security.PublicKey key, InputStream toCheck) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException {
        return verify(key, toCheck, null);
    }
    
	/**
	 * Using the <code>provider</code>, verifies that the corresponding PrivateKey for <code>key</code>
	 * generated <code>this</code> Signature for the data given by <code>toCheck</code>
	 * @param key the PublicKey 
	 * @param toCheck an InputStream of data
	 * @return <code>true</code> if the signature verifies
	 * @throws JsdsiRuntimeException on error
	 */
	public boolean verify(java.security.PublicKey key, InputStream toCheck, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
	    String algo = this.algo.jdkName();
	    java.security.Signature sig;
//        try {
            sig = (provider == null)
            ? java.security.Signature.getInstance(algo)
            : java.security.Signature.getInstance(algo, provider);
            sig.initVerify(key);
//        } catch (NoSuchAlgorithmException e) {
//            throw new JsdsiRuntimeException("Error creating signature engine for verification", e);
//        } catch (NoSuchProviderException e) {
//            throw new JsdsiRuntimeException("Error creating signature engine for verification", e);
//        } catch (InvalidKeyException e) {
//            throw new JsdsiRuntimeException("Error initialising signature engine for verification", e);
//        }
        
		Iterator it = new jsdsi.util.InputStreamIterator(toCheck);
        while (it.hasNext()) {
            byte[] bytes = (byte[]) it.next();
//            try {
                sig.update(bytes);
//            } catch (SignatureException e) {
//                throw new JsdsiRuntimeException("Error updating signature engine for verification", e);
//            }
        }
//		try {
            return sig.verify(data);
//        } catch (SignatureException e) {
//            throw new JsdsiRuntimeException("Error verifying signature", e);
//        }
	}
	
	/**
	 * Returns the principal representing the signer.
	 * 
	 * @return the signer.
	 */
	public Principal getSigner() {
		return signer;
	}

	/**
	 * Returns the hash of the signed data.
	 * 
	 * @return the hash of the signed data.
	 */
	public Hash getSignee() {
		return signee;
	}

	/**
	 * @return the JDK name for the signature algorithm.
	 */
	public String getAlgorithm() {
		return this.algo.jdkName();
	}

	/**
	 * @return the signature algorythm
	 */
	public SignatureAlgoEnum getSignatureEnum() {
	    return this.algo;
	}
	
	/**
	 * @return the value of the signature.
	 */
	public byte[] getData() {
		return data;
	}

	public boolean equals(Object o) {
		if (o instanceof Signature) {
			Signature s = (Signature) o;
			return signer.equals(s.signer)
				&& signee.equals(s.signee)
				&& algo.equals(s.algo)
				&& Util.equals(data, s.data);
		}
		return false;
	}

	public int hashCode() {
		return signer.hashCode()
			^ signee.hashCode()
			^ algo.hashCode()
			^ Util.hashCode(data);
	}

	public SexpList toSexp() {
		Sexp[] ss = new Sexp[3];
		ss[0] = getSignee().toSexp();
		ss[1] = getSigner().toSexp();
		Sexp[] sig = new Sexp[1];
		sig[0] = SexpUtil.toSexp(getData());
		ss[2] = SexpUtil.toSexp(this.algo.spkiName(), sig);
		return SexpUtil.toSexp("signature", ss);
	}

	static Signature parseSignature(SexpList l) throws SexpParseException {
		Iterator sbody = SexpUtil.getBody(l);
		// (signature <hash> <principal> (<algo> <data))
		Hash signee = Hash.parseHash(SexpUtil.getNextList(sbody, "sig hash"));
		Principal signer =
			Principal.parsePrincipal(
				SexpUtil.getNextList(sbody, "sig principal"));
		SexpList algoblock = SexpUtil.getNextList(sbody, "sig algo block");
		SexpUtil.checkDone(sbody, "signature");
		String algo = algoblock.getType();
		Iterator algobody = SexpUtil.getBody(algoblock);
		byte[] data = SexpUtil.getNextByteArray(algobody, "sig data");
		SexpUtil.checkDone(algobody, "signature");
		SignatureAlgoEnum sigEnum = calculateSignatureEnum(algo);
		return new Signature(signer, signee, sigEnum, data);
	}
	
	/**
	 * Temporary method to cope with legacy certificates/signatures which incorrectly have
	 * JDK algorythm names in their SExpression.
	 * @param algo
	 * @return
	 * @todo Method should be removed in the future when legacy data is no longer an issue 
	 */
	static SignatureAlgoEnum calculateSignatureEnum(String algo) {
	    try {
            return SignatureAlgoEnum.fromSpki(algo);
        } catch (JsdsiRuntimeException e) {
            //Logger logger = Logger.getLogger("jsdsi.Signature");
            //logger.warning("Non SPKI signature algorythm found, seeing if it is a legacy data issue...");
            // was not a SPKI algo name, so was it a legacy JDK one...
            return SignatureAlgoEnum.fromJdk(algo);
        }
	}

    public static void main(String[] args) throws Exception
    {
        KeyPair kp = jsdsi.util.KeyPairFactory.create(KeyEnum.RSA);

        jsdsi.Signature sig1 = jsdsi.Signature.create
            (kp, new jsdsi.StringTag("member"),"MD5withRSA");
        jsdsi.Signature sig2 = jsdsi.Signature.create
            (kp, new jsdsi.StringTag("member"),"MD5withRSA");
        
        System.out.println(sig1.equals(sig2));
        System.out.println(sig1 == sig2);
        
        System.out.println("sig.hashCode()");
        System.out.println(sig1.hashCode());
        System.out.println(sig2.hashCode());
        
        System.out.println("sig.getSigner().hashCode()");
        System.out.println(sig1.getSigner().hashCode());
        System.out.println(sig2.getSigner().hashCode());

        System.out.println("sig.getSignee().hashCode()");
        System.out.println(sig1.getSignee().hashCode());
        System.out.println(sig2.getSignee().hashCode());
        
        System.out.println("sig.getAlgorithm().hashCode()");
        System.out.println(sig1.getAlgorithm().hashCode());
        System.out.println(sig2.getAlgorithm().hashCode());

        System.out.println("Util.hashCode(sig.getData())");
        System.out.println(Util.hashCode(sig1.getData()));
        System.out.println(Util.hashCode(sig2.getData()));
        
        System.out.println("sig.getData().hashCode()");
        System.out.println(sig1.getData().hashCode());
        System.out.println(sig2.getData().hashCode());
    }
}
