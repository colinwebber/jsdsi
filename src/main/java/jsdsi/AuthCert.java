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

import java.util.ArrayList;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpUtil;

/**
 * A SPKI authorization certificate: conveys a permission (the Tag) from
 * the issuer to the subject.  If the propagate flag is set, the subject
 * can further delegate the permission.
 *
 * @see Cert
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date
 */
public class AuthCert extends Cert {
    
    private static final long serialVersionUID = -5588350360041330506L;
    
	/**
	 * The authorization of this <code>AuthCert</code>.
	 */
	private transient final Auth auth;

	/**
	 * Creates a new <code>AuthCert</code> from a given principal, subject,
	 * validity, display-string, comment, tag, and delegation bit.
	 * 
	 * @param  i principal of this <code>AuthCert</code>.
	 * @param  s subject of this <code>AuthCert</code>.
	 * @param  v validity of this <code>AuthCert</code>.
	 * @param  d display-string of this <code>AuthCert</code>.
	 * @param  c comment of this <code>AuthCert</code>.
	 * @param  t tag of this <code>AuthCert</code>.
	 * @param  p delegation bit of this <code>AuthCert</code>.
	 */
	public AuthCert(
		Principal i,
		Subject s,
		Validity v,
		String d,
		String c,
		Tag t,
		boolean p) {
		super(i, s, v, d, c);
		assert(t != null) : "null tag";
		auth = new Auth(t, p);
	}

	/**
	 * Returns the tag of this <code>AuthCert</code>.
	 * 
	 * @return the tag of this <code>AuthCert</code>.
	 */
	public Tag getTag() {
		return auth.getTag();
	}

	/**
	 * Returns the delegation bit of this <code>AuthCert</code>.
	 * 
	 * @return the delegation bit of this <code>AuthCert</code>.
	 */
	public boolean getPropagate() {
		return auth.getPropagate();
	}

	Auth getAuth() {
		return auth;
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof AuthCert) {
			AuthCert a = (AuthCert) o;
			return auth.equals(a.auth) && super.equals(o);
		}
		return false;
	}

	/**
     * @return true iff this is at least as strong as c
	 */
	public boolean implies(Cert c) {
        return (c instanceof AuthCert)
            && auth.implies(((AuthCert)c).auth)
            && super.implies(c);
	}
    
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return auth.hashCode() ^ super.hashCode();
	}

	/**
	 * Returns an <code>SexpList</code> that represents this
	 * <code>AuthCert</code>.
	 * 
	 * @return an <code>SexpList</code> that represents this
	 * <code>AuthCert</code>.
	 */
	public SexpList toSexp() {
		List l = new ArrayList(7);

		// display-hint
		String display = this.getDisplay();
		if (display!=null && display.equals("")==false) {
		    l.add( SexpUtil.toSexpDisplayHint(display) );
		}
		
		// issuer block
		Sexp[] is = new Sexp[1];
		is[0] = getIssuer().toSexp();
		l.add(SexpUtil.toSexp("issuer", is));

		// subject block
		Sexp[] ss = new Sexp[1];
		// FIXME: can we eliminate these downcasts?
		if (getSubject() instanceof Name) {
			// tell name about issuer
			ss[0] = ((Name) getSubject()).toSexp(getIssuer());
		} else if (getSubject() instanceof Threshold) {
			// tell nested names about issuer
			ss[0] = ((Threshold) getSubject()).toSexp(getIssuer());
		} else {
			ss[0] = getSubject().toSexp();
		}
		l.add(SexpUtil.toSexp("subject", ss));

		// auth block
		if (getPropagate()) {
			l.add(SexpUtil.toSexpList("propagate"));
		}
		l.add(getTag().toSexp());

		if (getValidity() != null) {
			l.add(getValidity().toSexp());
		}
		if (getComment() != null) {
			l.add(SexpUtil.toSexpComment(getComment()));
		}
		return SexpUtil.toSexp("cert", l);
	}
}
