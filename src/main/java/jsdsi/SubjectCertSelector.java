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
 * Selects all certificates whose subject is the given subject.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/12 00:32:41 $
 */
public class SubjectCertSelector extends CertSelector {
	Subject subject;

	/**
	 * Creates a new <code>SubjectCertSelector</code> that matches <code>s</code>.
	 */
	public SubjectCertSelector(Subject s) {
		subject = s;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new SubjectCertSelector(subject);
	}

	/**
	 * @see java.security.cert.CertSelector#match(Certificate)
	 */
	public boolean match(jsdsi.Certificate cert) {
		return cert.getCert().getSubject().equals(subject);
	}
    
	/**
	 * @return this.subject
	 */
	public Subject getSubject() {
		return subject;
	}
}