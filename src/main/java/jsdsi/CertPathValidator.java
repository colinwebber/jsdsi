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
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertPathValidatorSpi;

/**
 * Checks whether a certification path satisfies certain parameters:
 * essentially a wrapper around Proof.verify().
 *
 * @see Proof
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertPathValidator extends CertPathValidatorSpi {
	/**
	 * @see java.security.cert.CertPathValidatorSpi#engineValidate(CertPath, CertPathParameters)
	 */
	public java.security.cert.CertPathValidatorResult engineValidate(
		java.security.cert.CertPath path,
		java.security.cert.CertPathParameters params)
		throws CertPathValidatorException, InvalidAlgorithmParameterException {
		try {
			return engineValidate(
				(jsdsi.CertPath) path,
				(jsdsi.CertPathParameters) params);
		} catch (ClassCastException e) {
			throw (InvalidAlgorithmParameterException) new InvalidAlgorithmParameterException()
				.initCause(
				e);
		}
	}

	/**
	 * @see java.security.cert.CertPathValidatorSpi#engineValidate(CertPath, CertPathParameters)
	 */
	public jsdsi.CertPathValidatorResult engineValidate(
		jsdsi.CertPath path,
		jsdsi.CertPathParameters params)
		throws CertPathValidatorException, InvalidAlgorithmParameterException {
		if (!path.getProof().getCert().implies(params.getCert())) {
			throw new CertPathValidatorException
                ("param cert does not match or imply proof cert");
		}

		try {
			path.getProof().verify();
			return new jsdsi.CertPathValidatorResult(null);
		} catch (GeneralSecurityException e) {
			return new jsdsi.CertPathValidatorResult(e);
		}
	}
}
