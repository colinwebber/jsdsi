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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A Cert along with its validators (verification path, signature, and
 * online test results).  Whereas a Cert is simply an unauthenticated
 * statement, a Certificate is self-validating and thus can be
 * considered authentic if verify() succeeds.  A Certificate is
 * serialized as a SPKI/SDSI Sequence.
 * 
 * @see Cert
 * @see Signature
 * @see Sequence
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Certificate extends java.security.cert.Certificate {
    
    private static final long serialVersionUID = -2449865619310582192L;
    
	/**
	 * Issuer of this <code>Certificate</code>.
	 */
	private transient PublicKey issuer; // if null, cert.getIssuer() is a key

	/**
	 * <code>Cert</code> of this certificate.
	 */
	private transient Cert cert;

	// TODO: VerificationPath ...

	/**
	 * <code>Signature</code> of this <code>Certificate</code>.
	 */
	private transient Signature sig;

	// TODO: OnlineTestResult[] ...

	/**
	 * Creates a new <code>Certificate</code> from a given public key, 
	 * <code>Cert</code>, and signature.
	 * 
	 * @param  k public key of the issuer (principal).
	 * @param  c <code>Cert</code> of this <code>Certificate</code>.
	 * @param  s <code>Signature</code> of this <code>Certificate</code>.
	 * @throws CertificateException if <code>k</code> is <code>null</code> or
	 *         not a <code>PublicKey</code>.
	 */
	public Certificate(PublicKey k, Cert c, Signature s)
		throws CertificateException {
		super("SPKI");
		assert(c != null) : "null cert";
		assert(s != null) : "null signature";
		issuer = k;
		cert = c;
		sig = s;
		if ((issuer == null) && !(c.getIssuer() instanceof PublicKey)) {
			throw new CertificateException("issuer must be a PublicKey");
		}
	}

	/**
	 * Creates a new <code>Certificate</code> from a given <code>Cert</code>
	 * and <code>Signature</code>.
	 * 
	 * @param  c <code>Cert</code> to create a new <code>Certificate</code> 
	 *         from.
	 * @param  s <code>Signature</code> to create a new 
	 *         <code>Certificate</code> from.
	 * @throws CertificateException
	 */
	public Certificate(Cert c, Signature s) throws CertificateException {
		this(null, c, s);
	}

	/**
	 * @see java.security.cert.Certificate#getPublicKey()
	 */
	public java.security.PublicKey getPublicKey() {
		if (issuer == null) {
			return (PublicKey) cert.getIssuer();
		} else {
			return issuer;
		}
	}

	/**
	 * @see java.security.cert.Certificate#verify(PublicKey)
	 */
	public void verify(java.security.PublicKey key)
		throws
			CertificateException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			NoSuchProviderException,
			SignatureException {
		verify(key, null);
	}

	/**
	 * @see java.security.cert.Certificate#verify(PublicKey, String)
	 */
	public void verify(java.security.PublicKey key, String sigProvider)
		throws
			CertificateException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			NoSuchProviderException,
			SignatureException {
		assert(key != null) : "null key";
		if (!cert.getIssuer().samePrincipalAs(sig.getSigner())) {
			throw new CertificateException("issuer does not match signer");
		}
		if (!(key instanceof Principal)
			|| !cert.getIssuer().samePrincipalAs((Principal) key)) {
			throw new CertificateException("verification key does not match issuer");
		}
		if (!sig.verify(key, cert, sigProvider)) {
			throw new SignatureException("signature verification failed");
		}
		// TODO: check verification path, online test results, etc.
	}

	/**
	 * Returns the <code>Cert</code> of this <code>Certificate</code>.
	 * 
	 * @return the <code>Cert</code> of this <code>Certificate</code>.
	 */
	public Cert getCert() {
		return cert;
	}

	/**
	 * Checks if a given <code>Iterator</code> has more elements.
	 * 
	 * @param  elems <code>Iterator</code> to check for if it has more
	 *         elements.
	 * @throws CertificateException if <code>elems</code> does not have
	 *         any more elements.
	 */
	private static void check(Iterator elems) throws CertificateException {
		if (!elems.hasNext()) {
			throw new CertificateException("Not enough elements in sequence");
		}
	}

	/**
	 * Factory method for creating a new <code>Certificate</code> from an 
	 * iterator that holds a <code>Cert</code> and a <code>Signature</code>.
	 * 
	 * @param  elems <code>Iterator</code> holding a <code>Cert</code> and a
	 *         <code>Signature</code> (in this order).
	 * @return the certificate created from the <code>Cert</code> and the
	 *         <code>Signature</code> in <code>elems</code>.
	 * @throws CertificateException if <code>elems</code> does not contain
	 *         the expected values.
	 */
	public static Certificate fromElements(Iterator elems)
		throws CertificateException {
		PublicKey issuer = null;
		check(elems);
		Element e = (Element) elems.next();
		if (e instanceof PublicKey) {
			issuer = (PublicKey) e;
			check(elems);
			e = (Element) elems.next();
		}
		if (!(e instanceof Cert)) {
			throw new CertificateException("Expected cert");
		}
		Cert cert = (Cert) e;
		check(elems);
		e = (Element) elems.next();
		if (!(e instanceof Signature)) {
			throw new CertificateException("Expected signature");
		}
		Signature sig = (Signature) e;
		// TODO: include verification path, online test results, etc.
		return new Certificate(issuer, cert, sig);
	}

	/**
	 * Adds the issuer (if not <code>null</code>), the <code>Cert</code>,
	 * and the <code>Signture</code> of this <code>Certificate</code> to
	 * the given List.
	 * 
	 * @param  elems <code>List</code> to add the issuer (if not 
	 *         <code>null</code>), the <code>Cert</code>, and the 
	 *         <code>Signture</code> of this <code>Certificate</code> to.
	 */
	public void toElements(List elems) {
		if (issuer != null) {
			elems.add(issuer);
			// TODO: if key is hashed in cert or sig, include hash ops
		}
		elems.add(cert);
		elems.add(sig);
		// TODO: include verification path, online test results, etc.
	}

	/**
	 * Factory method that creates a new <code>Certificate</code> from a given
	 * <code>Sequence</code> that contains a <code>Cert</code> and a
	 * <code>Signature</code> (in this order).
	 * 
	 * @param  seq <code>Sequence</code> holding the <code>Cert</code> and the
	 *         <code>Signature</code> to create the <code>Certificate</code> 
	 *         from.
	 * @return the new <code>Certificate</code>.
	 * @throws CertificateException if the creation of the 
	 *         <code>Certificate</code> failed.
	 */
	public static Certificate fromSequence(Sequence seq)
		throws CertificateException {
		return fromElements(Arrays.asList(seq.getElements()).iterator());
	}

	/**
	 * Returns a <code>Sequence</code> containing the issuer (if not 
	 * <code>null</code>), the <code>Cert</code>, and the
	 * <code>Signature</code> of this <code>Certificate</code>.
	 * @return Sequence
	 */
	public Sequence toSequence() {
		ArrayList elems = new ArrayList();
		toElements(elems);
		return new Sequence((Element[]) elems.toArray(new Element[0]));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toSequence().toString();
	}

	/**
	 * @see java.security.cert.Certificate#getEncoded()
	 */
	public byte[] getEncoded() {
		return toSequence().toByteArray();
	}

	/**
	 * Returns the format of this <code>Certificate</code>, namely
	 * <code>&quot;SEXP&quot;</code>.
	 * 
	 * @return the format of this <code>Certificate</code>
	 *         (<code>&quot;SEXP&quot;</code>).
	 */
	public String getFormat() {
		return "SEXP";
	}
}