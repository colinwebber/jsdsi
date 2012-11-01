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
 * Selects all certificates issued by the given principal.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/03/12 00:32:41 $
 */
public class IssuerCertSelector extends CertSelector {
	private final Principal issuer;
	
	/**
	 * Creates a new <code>IssuerCertSelector</code> that matches
	 * certificates issued by <code>i</code>.
	 */
	public IssuerCertSelector(Principal i) {
		issuer = i;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new IssuerCertSelector(issuer);
	}

	/**
	 * @return true if cert's issuer is the same principal as this.issuer.
	 * 
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public boolean match(jsdsi.Certificate cert) {
		return cert.getCert().getIssuer().samePrincipalAs(issuer);
	}

	/**
	 * @return this.issuer
	 */
	public Principal getIssuer() {
		return issuer;
	}
}