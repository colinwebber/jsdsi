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

import java.util.Iterator;

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A single SPKI/SDSI certificate.
 * 
 * @see AuthCert
 * @see NameCert
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class Cert extends Obj implements Element {
    
    private static final long serialVersionUID = 2128550525621089508L;
    
	/**
	 * Issuer of this certificate.
	 */
	private transient final Principal issuer;

	/**
	 * Subject of this certificate.
	 */
	private transient final Subject subject;

	/**
	 * Validity of this certificate.
	 */
	private transient final Validity validity;

	/**
	 * A presentation hint for this certificate.
	 */
	private transient final String display;

	/**
	 * Comment of this certificate.
	 */
	private transient final String comment;

	/**
	 * Creates a new <code>Cert</code> using a given issuer, subject, validity,
	 * display string, and comment.
	 * 
	 * @param  i issuer of this <code>Cert</code>.
	 * @param  s subject of this <code>Cert</code>.
	 * @param  v validity of this <code>Cert</code>.
	 * @param  d display-string of this <code>Cert</code>.
	 * @param  c comment of this <code>Cert</code>.
	 */
	public Cert(Principal i, Subject s, Validity v, String d, String c) {
		assert(i != null) : "null issuer";
		assert(s != null) : "null subject";
		issuer = i;
		subject = s;
		validity = v; // may be null
		display = d; // may be null
		comment = c; // may be null
	}

	/**
	 * Returns the issuer of this <code>Cert</code>.
	 * 
	 * @return the issuer of this <code>Cert</code>.
	 */
	public Principal getIssuer() {
		return issuer;
	}

	/**
	 * Returns the subject of this <code>Cert</code>.
	 * 
	 * @return the subject of this <code>Cert</code>.
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Returns the validity of this <code>Cert</code>.
	 * 
	 * @return the validity of this <code>Cert</code>.
	 */
	public Validity getValidity() {
		return validity;
	}

	/**
	 * Returns the comment of this <code>Cert</code>.
	 * 
	 * @return the comment of this <code>Cert</code>.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Returns the display string of this <code>Cert</code>.
	 * 
	 * @return the display string of this <code>Cert</code>.
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Cert) {
			Cert c = (Cert) o;
			return issuer.equals(c.issuer)
				&& subject.equals(c.subject)
				&& Util.equals(validity, c.validity)
				&& Util.equals(display, c.display)
				&& Util.equals(comment, c.comment);
		}
		return false;
	}

	/**
     * @return true iff this is at least as strong as c
	 */
	public boolean implies(Cert c) {
        return issuer.equals(c.issuer)
            && subject.equals(c.subject)
            && Validity.implies(validity, c.validity)
            && Util.equals(display, c.display);
        // ignore comment
	}
    
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return issuer.hashCode()
			^ subject.hashCode()
			^ Util.hashCode(validity)
			^ Util.hashCode(display)
			^ Util.hashCode(comment);
	}

	/**
	 * Parses an <code>SexpList</code> that holds a <code>Cert</code>
	 * and return a new <code>Cert</code>.
	 *  
	 * @param l the <code>SexpList</code> that holds a <code>Cert</code>.
	 * @return a new <code>Cert</code> stored in <code>l</code>.
	 * @throws SexpParseException
	 */
	static Cert parseCert(SexpList l) throws SexpParseException {
		Iterator cbody = SexpUtil.getBody(l);
		
		SexpList displayOrIssuer =
			SexpUtil.getNextList(cbody, "cert display or issuer");
		String type = displayOrIssuer.getType();
		String display = null;
		Iterator ibody = null;
		if (type.equals("display")) {
		    Iterator displaybody = SexpUtil.getBody(displayOrIssuer);
			display = SexpUtil.getNextString(displaybody, "display body");
			ibody =
				SexpUtil.getBody(
					SexpUtil.getNextList(cbody, "issuer", "cert issuer"));
		} else {
		    ibody = SexpUtil.getBody(displayOrIssuer);
		}
		
		SexpList nameOrPrincipal = SexpUtil.getNextList(ibody, "issuer body");
		SexpUtil.checkDone(ibody, "issuer");
		type = nameOrPrincipal.getType();
		Principal issuer = null;
		String name = null;
		if (type.equals("name")) {
			Iterator nbody = SexpUtil.getBody(nameOrPrincipal);
			issuer =
				Principal.parsePrincipal(
					SexpUtil.getNextList(nbody, "name issuer"));
			name = SexpUtil.getNextString(nbody, "name string");
			SexpUtil.checkDone(nbody, "issuer-name");
		} else {
			// FIXME: defaulting to principal is confusing
			issuer = Principal.parsePrincipal(nameOrPrincipal);
		}

		// (subject <subj-obj>)
		Iterator sbody =
			SexpUtil.getBody(
				SexpUtil.getNextList(cbody, "subject", "cert subject"));
		Subject subject =
			Subject.Default.parseSubject(
				SexpUtil.getNextList(sbody, "subject body"),
				issuer);
		SexpUtil.checkDone(sbody, "subject");

		Tag tag = null;
		boolean propagate = false;
		if (name == null) {
			// <deleg>? <tag>
			SexpList propOrTag =
				SexpUtil.getNextList(cbody, "cert propagate or tag");
			type = propOrTag.getType();
			if (type.equals("propagate")) {
				propagate = true;
				SexpUtil.check(
					propOrTag.size() == 1,
					"extra fields in propagate");
				propOrTag = SexpUtil.getNextList(cbody, "tag", "cert tag");
			}
			tag = Tag.parseTag(propOrTag);
		}
		// <valid>? <comment>?
		Validity validity = null;
		String comment = null;
		if (cbody.hasNext()) {
			SexpList validOrComment =
				SexpUtil.getNextList(cbody, "cert valid or comment");
			type = validOrComment.getType();
			if (type.equals("valid")) {
				validity = Validity.parseValidity(validOrComment);
				if (cbody.hasNext()) {
					validOrComment =
						SexpUtil.getNextList(cbody, "comment", "cert comment");
					type = "comment"; // FIXME: ugly!
				}
			}
			if (type.equals("comment")) {
				Iterator combody = SexpUtil.getBody(validOrComment);
				comment = SexpUtil.getNextString(combody, "comment body");
				SexpUtil.checkDone(combody, "comment");
			}
		}
		SexpUtil.checkDone(cbody, "cert");
		SexpUtil.check((name == null) != (tag == null), // sanity check
		"internal error: either name or tag must not be null");
		if (name == null) {
			return new AuthCert(
				issuer,
				subject,
				validity,
				display,
			comment,
			tag,
			propagate);
		} else {
			return new NameCert(
				issuer,
				subject,
				validity,
				display,
			comment,
			name);
		}
	}
}
