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

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * An access control list (ACL) that restricts access to an object on
 * the local system.  The ACL contains a set of ACL entries that specify
 * which principals may access the object and how.
 *
 * @see AclEntry
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Acl extends Obj {
    
    private static final long serialVersionUID = -5980365133398522076L;
    
	/**
	 * The entries the Acl consists of.
	 */
	private transient final AclEntry[] entries;

	/**
	 * Constructs a new <code>Acl</code> from given <code>AclEntry</code>s.
	 * 
	 * @param  e array of <code>AclEntry</code>s to create a new 
	 *         <code>Acl</code> from.
	 */
	public Acl(AclEntry[] e) {
		assert(e != null) : "null entries";
		entries = e;
	}

	/**
	 * Returns an array of <code>AclEntry</code>s.
	 * 
	 * @see AclEntry
	 * 
	 * @return an array of <code>AclEntry</code>s.
	 */
	public AclEntry[] getEntries() {
		return entries;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof Acl) && Util.equals(entries, ((Acl) o).entries);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(entries);
	}

	/**
	 * Converts this <code>Acl</code> to an <code>SecpList</code>.
	 * 
	 * @return an <code>SecpList</code> that represents this <code>Acl</code>. 
	 */
	public SexpList toSexp() {
		Sexp[] ss = new Sexp[entries.length];
		for (int i = 0; i < entries.length; i++) {
			ss[i] = entries[i].toSexp();
		}
		return SexpUtil.toSexp("acl", ss);
	}

	static Acl parseAcl(SexpList l) throws SexpParseException {
		Iterator abody = SexpUtil.getBody(l);
		AclEntry[] entries = new AclEntry[l.size() - 1];
		for (int i = 0; i < entries.length; i++) {
			entries[i] =
				AclEntry.parseAclEntry(
					SexpUtil.getNextList(abody, "entry", "acl entry"));
		}
		SexpUtil.checkDone(abody, "acl");
		return new Acl(entries);
	}
}