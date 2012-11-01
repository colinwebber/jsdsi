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
 * A SDSI name certificate: associates a string (a local name) with a
 * Subject in the issuer's local namespace.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $
 */
public class NameCert extends Cert {
    
    private static final long serialVersionUID = -8560074033459222359L;
    
	/**
	 * Name-string of this name certificate.
	 */
	private transient final String name;

	/**
	 * Creates a new name certificate from a given principal, subject,
	 * validity, display hint, comment, and (local) name.
	 * 
	 * @param  i issuer (principal).
	 * @param  s subject.
	 * @param  v validity
	 * @param  d display hint.
	 * @param  c comment.
	 * @param  n name-string.
	 */
	public NameCert(
		Principal i,
		Subject s,
		Validity v,
		String d,
		String c,
		String n) {
		super(i, s, v, d, c);
		assert(n != null) : "null name";
		name = n;
	}

	/**
	 * Returns the name-string of this name certificate.
	 * 
	 * @return the name-string of this name certificate.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the full name, that is the <code>Name</code> that is created 
	 * with this issuer and the name-string.
	 * 
	 * @return a new <code>Name</code> created from this issuer and 
	 *         name-string.
	 */
	public Name getFullName() {
		return new Name(getIssuer(), new String[] { getName()});
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof NameCert)
			&& name.equals(((NameCert) o).name)
			&& super.equals(o);
	}

	/**
     * @return true iff this is at least as strong as c
	 */
	public boolean implies(Cert c) {
        return (c instanceof NameCert)
            && name.equals(((NameCert)c).name)
            && super.implies(c);
	}
    
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return name.hashCode() ^ super.hashCode();
	}

	// TODO: move code common with AuthCert into Cert
	public SexpList toSexp() {
		List l = new ArrayList(5);

		// display-hint
		String display = this.getDisplay();
		if (display!=null && display.equals("")==false) {
		    l.add( SexpUtil.toSexpDisplayHint(display) );
		}
		
		// issuer block
		Sexp[] is = new Sexp[1];
		Sexp[] ns = new Sexp[2];
		ns[0] = getIssuer().toSexp();
		ns[1] = SexpUtil.toSexp(getName());
		is[0] = SexpUtil.toSexp("name", ns);
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

		if (getValidity() != null) {
			l.add(getValidity().toSexp());
		}
		if (getComment() != null) {
			l.add(SexpUtil.toSexpComment(getComment()));
		}
		return SexpUtil.toSexp("cert", l);
	}
}
