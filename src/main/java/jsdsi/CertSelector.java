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

/**
 * Selects a set of SPKI/SDSI certificates from a CertStore.
 *
 * @see CertStore
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public abstract class CertSelector implements java.security.cert.CertSelector {
	/**
	 * @see java.lang.Object#clone()
	 */
	public abstract Object clone();
	
	/**
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public final boolean match(java.security.cert.Certificate cert) {
		if (cert instanceof jsdsi.Certificate) {
			return match((jsdsi.Certificate) cert);
		}
		return false;
	}
	
	/**
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public abstract boolean match(jsdsi.Certificate cert);
}