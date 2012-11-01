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

import java.security.GeneralSecurityException;

/**
 * The result of a call to the cert path validator: returns true to
 * isOk() if successful; otherwise getCause() returns the cause of the
 * failure.
 *
 * @see CertPathValidator
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertPathValidatorResult
	implements java.security.cert.CertPathValidatorResult {
	/**
	 * The exception that has been thrown at the call to the cert path
	 * validator.
	 */
	GeneralSecurityException ex;

	/**
	 * Creates a new <code>CertPathValidatorResult</code> from a given
	 * <code>GeneralSecurityException</code>.
	 * 
	 * @param  e <code>GeneralSecurityException</code> to create the object 
	 *         from.
	 */
	public CertPathValidatorResult(GeneralSecurityException e) {
		ex = e;
	}

	/**
	 * Checks if the no exception has been used creating this
	 * <code>CertPathValidatorResult</code>.
	 * 
	 * @return <code>true</code> if the exception that has been used for 
	 *         creating this <code>CertPathValidatorResult</code> was
	 *         <code>null</code>, returns <code>false</code> otherwise.
	 */
	public boolean isOk() {
		return (ex == null);
	}

	/**
	 * Returns the exception that has been used creating this
	 * <code>CertPathValidatorResult</code>.
	 * 
	 * @return the <code>GeneralSecurityException</code> that has been used for
	 *         creating this <code>CertPathValidatorResult</code>.
	 */
	public GeneralSecurityException getCause() {
		return ex;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new CertPathValidatorResult(ex);
	}
}