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
 * Selects all authorization certificates 
 * issued by a specific principal that
 * grant an authorization at least as strong
 * as a specified authorization.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/12 00:32:41 $
 */
public class AuthCertSelector extends CertSelector {
	private final Principal issuer;
	private final Auth auth;

	/**
	 * Creates an <code>AuthCertSelector</code> that matches
	 * all auth certs issued by <code>i</code>.
	 * 
	 * @param i the issuer to match
	 */
	public AuthCertSelector(Principal i) {
		this(i, Auth.NULL_AUTH);
	}

	/**
	 * Creates an <code>AuthCertSelector</code> that matches
	 * these auth certs issued by <code>i</code> that grant
	 * an authorization no weaker than <code>a</code>
	 * 
	 * @param i the issuer to match
	 * @param a the weakest auth to match
	 */
	public AuthCertSelector(Principal i, Auth a) {
		issuer = i;
		auth = a;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new AuthCertSelector(issuer, auth);
	}

	/**
	 * @return true if cert's issuer is the same principal as
	 * this.issuer, and if cert's auth implies this.auth. 
	 * 
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public boolean match(jsdsi.Certificate cert) {
		return (cert.getCert() instanceof AuthCert)
			&& cert.getCert().getIssuer().samePrincipalAs(issuer)
			&& ((AuthCert)cert.getCert()).getAuth().implies(auth);
	}

	/**
	 * @return issuer
	 */
	public Principal getIssuer() {
		return issuer;
	}
	
	/**
	 * @return auth
	 */
	public Auth getAuth() {
		return auth;
	}
}