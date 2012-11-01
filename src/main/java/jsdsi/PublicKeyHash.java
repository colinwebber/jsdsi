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

import java.net.URI;

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;

/**
 * The cryptographic hash of a public key.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/08 12:08:08 $
 */
public class PublicKeyHash extends Principal {
    
    private static final long serialVersionUID = -1085604011787649865L;
    
	/**
	 * The hash.
	 */
	private transient final Hash hash;

	/**
	 * Creates a new <code>PublicKeyHash</code>.
	 * 
	 * @param  h hash to create the <code>PublicKeyHash</code>.
	 */
	public PublicKeyHash(Hash h) {
		assert(h != null) : "null hash";
		hash = h;
	}

	/**
	 * Returns the hash.
	 * 
	 * @return the hash.
	 */
	public Hash getHash() {
		return hash;
	}

	/**
	 * @see jsdsi.Principal#getURIs()
	 */
	public URI[] getURIs() {
	    return hash.getURIs();
	}
	
	/**
	 * @see jsdsi.Principal#samePrincipalAs(Principal)
	 */
	public boolean samePrincipalAs(Principal p) {
		if (p == null) {
			return false;
		}
		if (p instanceof PublicKeyHash) {
			return equals(p);
		}
		// p is a public key, so check that hashes match
		Hash h = new Hash(hash.getDigest(), p, hash.getURIs());
		return hash.equals(h);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof PublicKeyHash)
			&& hash.equals(((PublicKeyHash) o).hash);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hash.hashCode();
	}

	public SexpList toSexp() {
		return getHash().toSexp();
	}

	public static PublicKeyHash parsePublicKeyHash(SexpList l)
		throws SexpParseException {
		return new PublicKeyHash(Hash.parseHash(l));
	}
}