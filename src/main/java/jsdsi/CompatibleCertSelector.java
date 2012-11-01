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
 * Selects all certificates whose subject is a name that starts with the
 * specified issuer and local name.  That is, if the issuer is <code>K</code> and 
 * the local name is <code>S</code>, this selects all certificates of the form 
 * <code>(LHS -> &quot;K S ...&quot;)</code>, where <code>LHS</code> is the 
 * left-hand-side of the certificate and <code>&quot;K S ...&quot;</code> is a 
 * name.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/12 00:32:41 $
 */
public class CompatibleCertSelector extends CertSelector {
	Principal issuer;
	String name;

	/**
	 * Creates a new <code>CompatibleCertSelector</code> that matches
	 * certificates whose subject is a name issued by <code>i</code>
	 * whose first local name is <code>n</code>.
	 */
	public CompatibleCertSelector(Principal i, String n) {
		issuer = i;
		name = n;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new CompatibleCertSelector(issuer, name);
	}

	/**
	 * @return true if cert.subject is a Name whose issuer
	 * is the same principal as this.issuer and whose first
	 * local name is this.name.
	 * 
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public boolean match(jsdsi.Certificate cert) {
		return (cert.getCert().getSubject() instanceof Name)
			&& ((Name) cert.getCert().getSubject()).getIssuer().samePrincipalAs(
				issuer)
			&& ((Name) cert.getCert().getSubject()).getNames()[0].equals(name);
	}

	/**
	 * @return the issuer matched by this selector.
	 */
	public Principal getIssuer() {
		return issuer;
	}

	/**
	 * @return the local name matched by this selector.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fully-qualified name matched by this selector.
	 */
	public Name getFullName() {
		return new Name(issuer, name);
	}
}