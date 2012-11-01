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

import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * An RSA public key.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 */
public class RSAPublicKey
	extends PublicKey
	implements java.security.interfaces.RSAPublicKey {
    
    private static final long serialVersionUID = 115101582431469482L;
    
	/**
	 * The modulus of this <code>RSAPublicKey</code>.
	 */
	private transient final BigInteger modulus;

	/**
	 * The exponent of this <code>RSAPublicKey</code>.
	 */
	private transient final BigInteger exponent;

	/**
	 * Creates a new <code>RSAPublicKey</code> from a given modulus, exponent, 
	 * algorithm-name, and URLs.
	 * 
	 * @param  m modulus.
	 * @param  e exponent.
	 * @param  a algorithm-name.
	 * @param  u list of URLs.
	 * @deprecated use {@link #RSAPublicKey(BigInteger, BigInteger, String, URI[])}
	 */
	public RSAPublicKey(BigInteger m, BigInteger e, String a, URL[] u) {
		super(a, u);
		assert(m != null) : "null modulus";
		assert(e != null) : "null exponent";
		modulus = m;
		exponent = e;
	}

	public RSAPublicKey(BigInteger m, BigInteger e, String a, URI[] u) {
		super(a, u);
		assert(m != null) : "null modulus";
		assert(e != null) : "null exponent";
		modulus = m;
		exponent = e;
	}
	
	/**
	 * Creates a new <code>RSAPublicKey</code> from a given modulus, exponent 
	 * and algorithm name.
	 * 
	 * @param  m modulus.
	 * @param  e exponent.
	 * @param  a algorithm name.
	 */
	public RSAPublicKey(BigInteger m, BigInteger e, String a) {
		this(m, e, a, (URI[])null );
	}

	public RSAPublicKey(java.security.interfaces.RSAPublicKey k, URL[] u) {
		this(k.getModulus(), k.getPublicExponent(), k.getAlgorithm(), u);
	}
	
	public RSAPublicKey(java.security.interfaces.RSAPublicKey k, URI[] u) {
		this(k.getModulus(), k.getPublicExponent(), k.getAlgorithm(), u);
	}

	public RSAPublicKey(java.security.interfaces.RSAPublicKey k) {
		this(k, (URI[])null );
	}

	/**
	  * Creates a new RSA key pair whose public key is a Principal.
	  * @param a the specific RSA algorithm to use
	  * @param provider the provider to use
	  */
	public static KeyPair create(String a, String provider)
		throws NoSuchAlgorithmException, NoSuchProviderException {
		assert(a != null) : "null algo";
		KeyPairGenerator kpg =
			(provider == null)
				? KeyPairGenerator.getInstance(a)
				: KeyPairGenerator.getInstance(a, provider);
		KeyPair kp = kpg.genKeyPair();
		assert(
			kp.getPublic()
				instanceof java
					.security
					.interfaces
					.RSAPublicKey) : "did not generate RSA key";
		return new KeyPair(
			new RSAPublicKey(
				(java.security.interfaces.RSAPublicKey) kp.getPublic()),
			kp.getPrivate());
	}

	/**
	  * Creates a new RSA key pair whose public key is a Principal.
	  * 
	  * @param a the specific RSA algorithm to use
	  */
	public static KeyPair create(String a)
		throws NoSuchAlgorithmException, NoSuchProviderException {
		return create(a, null);
	}

	/**
	  * Creates a new RSA key pair whose public key is a Principal.
	  */
	public static KeyPair create()
		throws NoSuchAlgorithmException, NoSuchProviderException {
		return create("RSA");
	}

	/**
	 * @see java.security.interfaces.RSAKey#getModulus()
	 */
	public BigInteger getModulus() {
		return modulus;
	}

	/**
	 * Returns the exponent of this <code>RSAPublicKey</code>.
	 * 
	 * @return the exponent of this <code>RSAPublicKey</code>.
	 */
	public BigInteger getExponent() {
		return exponent;
	}

	/**
	 * @see java.security.interfaces.RSAPublicKey#getPublicExponent()
	 */
	public BigInteger getPublicExponent() {
		return exponent;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof RSAPublicKey) {
			RSAPublicKey r = (RSAPublicKey) o;
			return modulus.equals(r.modulus)
				&& exponent.equals(r.exponent)
				&& super.equals(o);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return modulus.hashCode() ^ exponent.hashCode() ^ super.hashCode();
	}

	public SexpList toPublicKeySexp() {
		String algo = "rsa";
		Sexp[] ss = new Sexp[2];
		Sexp[] es = new Sexp[1];
		es[0] = SexpUtil.toSexp(getExponent().toByteArray());
		ss[0] = SexpUtil.toSexp("e", es);
		Sexp[] ns = new Sexp[1];
		ns[0] = SexpUtil.toSexp(getModulus().toByteArray());
		ss[1] = SexpUtil.toSexp("n", ns);
		return SexpUtil.toSexp(algo, ss);
	}

	/**
	 * Parses an Sexp of an rsa-publickey returning the <code>RSAPublicKey</code> with the given urls.
	 * @param l Sexpression defining the public-key
	 * @param algo algorythm
	 * @param urls array of uris (may be <code>null</code>)
	 * @return
	 * @throws SexpParseException
	 * 
	 * @deprecated use {@link #parseRSAPublicKey(SexpList, String, URI[])}
	 */
	static RSAPublicKey parseRSAPublicKey(SexpList l, String algo, URL[] urls)
		throws SexpParseException {
		Iterator rbody = SexpUtil.getBody(l);
		Iterator ebody =
			SexpUtil.getBody(SexpUtil.getNextList(rbody, "e", "exponent"));
		BigInteger e =
			new BigInteger(SexpUtil.getNextByteArray(ebody, "e value"));
		SexpUtil.checkDone(ebody, "exponent");
		Iterator nbody =
			SexpUtil.getBody(SexpUtil.getNextList(rbody, "n", "modulus"));
		BigInteger n =
			new BigInteger(SexpUtil.getNextByteArray(nbody, "n value"));
		SexpUtil.checkDone(nbody, "modulus");
		SexpUtil.checkDone(rbody, "rsa public-key");
		return new RSAPublicKey(n, e, algo, urls);
	}
	
	/**
	 * Parses an Sexp of an rsa-publickey returning the <code>RSAPublicKey</code> with the given uris.
	 * @param l Sexpression defining the public-key
	 * @param algo algorythm
	 * @param uris array of uris (may be <code>null</code>)
	 * @return
	 * @throws SexpParseException
	 * 
	 */
	static RSAPublicKey parseRSAPublicKey(SexpList l, String algo, URI[] uris)
	throws SexpParseException {
		Iterator rbody = SexpUtil.getBody(l);
		Iterator ebody =
			SexpUtil.getBody(SexpUtil.getNextList(rbody, "e", "exponent"));
		BigInteger e =
			new BigInteger(SexpUtil.getNextByteArray(ebody, "e value"));
		SexpUtil.checkDone(ebody, "exponent");
		Iterator nbody =
			SexpUtil.getBody(SexpUtil.getNextList(rbody, "n", "modulus"));
		BigInteger n =
			new BigInteger(SexpUtil.getNextByteArray(nbody, "n value"));
		SexpUtil.checkDone(nbody, "modulus");
		SexpUtil.checkDone(rbody, "rsa public-key");
		return new RSAPublicKey(n, e, algo, uris);
	}
}