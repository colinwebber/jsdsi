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
 * Selects all name certificates issued by a specific principal to define
 * a specific local name.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/12 00:32:41 $
 */
public class NameCertSelector extends CertSelector {
	Principal issuer;
	String name;

	/**
	 * Creates a new <code>NameCertSelector</code> that matches
	 * certificates issued by <code>i</code> for the local name n.
	 */
	public NameCertSelector(Principal i, String n) {
		issuer = i;
		name = n;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new NameCertSelector(issuer, name);
	}

	/**
	 * @return true if cert.issuer is the same principal as this.issuer
	 * and if cert defined the local name this.name.
	 * 
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public boolean match(jsdsi.Certificate cert) {
		return (cert.getCert() instanceof NameCert)
			&& cert.getCert().getIssuer().samePrincipalAs(issuer)
			&& ((NameCert) cert.getCert()).getName().equals(name);
	}

	/**
	 * @return the issuer matched by this selector
	 */
	public Principal getIssuer() {
		return issuer;
	}

	/**
	 * @return the local name matched by this selector
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fully-qualified local name matched by this selector
	 */
	public Name getFullName() {
		return new Name(issuer, name);
	}
}