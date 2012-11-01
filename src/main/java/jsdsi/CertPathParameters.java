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

import java.security.InvalidAlgorithmParameterException;

/**
 * Parameters to the cert path builder and validator.  Specifies the
 * statement (a SPKI/SDSI cert) that needs to be proved or validated and
 * the certificate store from which to fetch certificates.
 *
 * @see CertStore
 * @see CertPathBuilder
 * @see CertPathValidator
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertPathParameters
	implements java.security.cert.CertPathParameters {
		/**
		 * The certs to prove.
		 */
	private Cert cert;
	
	/**
	 * The cert store to use.
	 */
	private java.security.cert.CertStore store;
	
	/**
	 * Creates a new <code>CertPathParameters</code> object from a given
	 * <code>Cert</code> and a given <code>CertStore</code>.
	 * 
	 * @see Name
	 * 
	 * @param  c <code>Cert</code> to create the object from.
	 * @param  s <code>CertStore</code> to create the object from.
	 * @throws InvalidAlgorithmParameterException if the subject of 
	 *         <code>c</code> is not a <code>Name</code>.
	 */
	public CertPathParameters(Cert c, java.security.cert.CertStore s)
		throws InvalidAlgorithmParameterException {
		if (c.getSubject() instanceof Name) {
			throw new InvalidAlgorithmParameterException("cert subject must not be a name");
		}
		cert = c;
		store = s;
	}

	/**
	 * Returns the <code>Cert</code> of this <code>CertPathParameters</code>.
	 * 
	 * @return the <code>Cert</code> of this <code>CertPathParameters</code>.
	 */
	public Cert getCert() {
		return cert;
	}

	/**
	 * Returns the <code>CertStore</code> of this 
	 * <code>CertPathParameters</code>.
	 * 
	 * @return the <code>CertStore</code> of this 
	 *         <code>CertPathParameters</code>.
	 */
	public java.security.cert.CertStore getStore() {
		return store;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		try {
			return new CertPathParameters(cert, store);
		} catch (InvalidAlgorithmParameterException e) {
			throw (IllegalStateException) new IllegalStateException()
				.initCause(
				e);
		}
	}
}