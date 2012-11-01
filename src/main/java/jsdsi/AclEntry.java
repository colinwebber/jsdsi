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
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * An ACL entry that specifies a permission (the Tag) and a set of
 * principals (the Subject) that may access the object protected by this
 * entry's ACL.
 * 
 * @see Acl
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/11/08 12:08:08 $
 */
public class AclEntry extends Obj {
    
    private static final long serialVersionUID = -3020366729004164789L;
    
	/**
	 * The subject of this <code>AclEntry</code>.
	 */
	private transient final Subject subject;

	/**
	 * The permission of this ACL entry.
	 */
	private transient final Auth auth;

	/**
	 * The validity of this <code>AclEntry</code>.
	 */
	private transient final Validity validity;

	/**
	 * The comment of this <code>AclEntry</code>.
	 */
	private transient final String comment;

	/**
	 * Creates a new <code>AclEntry</code> from a given subject, tag,
	 * delegation bit, validity, and comment.
	 * 
	 * @param  s subject of this <code>AclEntry</code>.
	 * @param  t tag of this <code>AclEntry</code>.
	 * @param  p delegation bit of this <code>AclEntry</code>.
	 * @param  v validity of this <code>AclEntry</code>.
	 * @param  c comment of this <code>AclEntry</code>.
	 */
	public AclEntry(Subject s, Tag t, boolean p, Validity v, String c) {
		assert(s != null) : "null subject";
		assert(t != null) : "null tag";
		subject = s;
		auth = new Auth(t, p);
		validity = v; // may be null
		comment = c; // may be null
	}

	/**
	 * Returns the subject of this <code>AclEntry</code>.
	 * 
	 * @return the subject of this <code>AclEntry</code>.
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Returns the tag of this <code>AclEntry</code>'s auth.
	 * 
	 * @return the tag of this <code>AclEntry</code>'s auth.
	 */
	public Tag getTag() {
		return auth.getTag();
	}

	/**
	 * Returns the delegation bit of this <code>AclEntry</code>'s auth.
	 * 
	 * @return the delegation bit of this <code>AclEntry</code>' auth.
	 */
	public boolean getPropagate() {
		return auth.getPropagate();
	}

	/**
	 * Returns the validity of this <code>AclEntry</code>
	 * (may be <code>null</code>).
	 * 
	 * @return the validity of this <code>AclEntry</code>.
	 */
	public Validity getValidity() {
		return validity;
	}

	/**
	 * Returns the comment of this <code>AclEntry</code> 
	 * (may be <code>null</code>).
	 * 
	 * @return the comment of this <code>AclEntry</code>.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof AclEntry) {
			AclEntry e = (AclEntry) o;
			return subject.equals(e.subject)
				&& auth.equals(e.auth)
				&& Util.equals(validity, e.validity)
				&& Util.equals(comment, e.comment);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return subject.hashCode()
			^ auth.hashCode()
			^ Util.hashCode(validity)
			^ Util.hashCode(comment);
	}

	/**
	 * Returns an <code>SexpList</code> that represents this
	 * <code>AclEntry</code>.
	 */
	public SexpList toSexp() {
		List l = new ArrayList(5);
		l.add(getSubject().toSexp());
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
		return SexpUtil.toSexp("entry", l);
	}

	/**
	 * Parses an <code>AclEntry</code> from a given <code>SexpList</code>.
	 * 
	 * @param l the <code>SexpList</code> to parse.
	 * @return the <code>AclEntry</code> contained in <code>l</code>. 
	 * @throws SexpParseException
	 */
	static AclEntry parseAclEntry(SexpList l) throws SexpParseException {
		Iterator ebody = SexpUtil.getBody(l);
		// FIXME: same as Cert parsing!
		// <sub-obj>
		Subject subject =
			Subject.Default.parseSubject(
				SexpUtil.getNextList(ebody, "acl entry subject"));
		// <deleg>? <tag>
		boolean propagate = false;
		SexpList propOrTag =
			SexpUtil.getNextList(ebody, "cert propagate or tag");
		String type = propOrTag.getType();
		if (type.equals("propagate")) {
			propagate = true;
			SexpUtil.check(propOrTag.size() == 1, "extra fields in propagate");
			propOrTag = SexpUtil.getNextList(ebody, "tag", "cert tag");
		}
		Tag tag = Tag.parseTag(propOrTag);
		// <valid>? <comment>?
		Validity validity = null;
		String comment = null;
		if (ebody.hasNext()) {
			SexpList validOrComment =
				SexpUtil.getNextList(ebody, "cert valid or comment");
			type = validOrComment.getType();
			if (type.equals("valid")) {
				validity = Validity.parseValidity(validOrComment);
				if (ebody.hasNext()) {
					validOrComment =
						SexpUtil.getNextList(ebody, "comment", "cert comment");
					type = "comment"; // FIXME: ugly!
				}
			}
			if (type.equals("comment")) {
				Iterator combody = SexpUtil.getBody(validOrComment);
				comment = SexpUtil.getNextString(combody, "comment body");
				SexpUtil.checkDone(combody, "comment");
			}
		}
		return new AclEntry(subject, tag, propagate, validity, comment);
	}
}