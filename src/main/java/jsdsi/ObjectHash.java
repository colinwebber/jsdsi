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
 * The cryptographic hash of an object.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/11/08 12:08:08 $
 */
public class ObjectHash extends Obj implements Subject {
    
    private static final long serialVersionUID = 5513858723159477992L;
    
	/**
	 * The <code>Hash</code> of this <code>ObjectHash</code>.
	 */
	private transient final Hash hash;

	/**
	 * The <code>Hash</code> of this <code>ObjectHash</code>.
	 */
	public ObjectHash(Hash h) {
		assert(h != null) : "null hash";
		hash = h;
	}

	/**
	 * Returns the <code>Hash</code> of this <code>ObjectHash</code>.
	 * 
	 * @return the <code>Hash</code> of this <code>ObjectHash</code>.
	 */
	public Hash getHash() {
		return hash;
	}

	/**
	 * Checks whether this <code>ObjectHash</code> is a hash of the given
	 * object.
	 * 
	 * @param  o object to check for if this object hash is a hash from it.
	 * @return <code>true</code> if this object hash is a hash of 
	 *         <code>o</code>, <code>false</code> otherwise.
	 */
	public boolean isHashOf(Obj o) {
		Hash h = new Hash(hash.getDigest(), o, hash.getURIs());
		return hash.equals(h);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof ObjectHash) && hash.equals(((ObjectHash) o).hash);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hash.hashCode();
	}

	/**
	 * Returns an S-expression of this <code>ObjectHash</code> 
	 */
	public SexpList toSexp() {
		Sexp[] ss = new Sexp[1];
		ss[0] = getHash().toSexp();
		return SexpUtil.toSexp("object-hash", ss);
	}

	/**
	 * Creates a new <code>ObjectHash</code> of an
	 * <code>SexpList</code>.
	 * 
	 * @param l the <code>SexpList</code> that holds the
	 * 	<code>ObjectHash</code>.
	 * @return the <code>ObjectHash</code> that is created.
	 * @throws SexpParseException
	 */
	static ObjectHash parseObjectHash(SexpList l) throws SexpParseException {
		Iterator obody = SexpUtil.getBody(l);
		Hash hash = Hash.parseHash(SexpUtil.getNextList(obody, "object hash"));
		SexpUtil.checkDone(obody, "object-hash");
		return new ObjectHash(hash);
	}
}