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
 * Specifies that the cert path builder should search for a cert path
 * from issuer to subject.  Essentially, tells the builder to use
 * FProver.
 *
 * @see FProver
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class IssuerCertPathParameters extends jsdsi.CertPathParameters {
	/**
	 * @see jsdsi.CertPathParameters#CertPathParameters(Cert, java.security.cert.CertStore)
	 */
	public IssuerCertPathParameters(Cert c, java.security.cert.CertStore s)
		throws InvalidAlgorithmParameterException {
		super(c, s);
	}
}