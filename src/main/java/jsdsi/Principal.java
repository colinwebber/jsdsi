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
 * A principal: an entity that can define names and can grant and
 * receive authorizations.  Represented by a public key.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class Principal
	extends Obj
	implements Subject, java.security.Principal {
    
    private static final long serialVersionUID = 4007409383894718535L;
    
	/**
	 * Compares this <code>Principal</code> with another.
	 * 
	 * @param  p another principal to compare this one with.
	 * @return <code>true</code> if both principal are the same (with respect
	 *         to their public key), <code>false</code> otherwise.
	 */
	public abstract boolean samePrincipalAs(Principal p);

	/**
	 * @see java.security.Principal#getName()
	 */
	public String getName() {
		return toString();
	}

	public abstract URI[] getURIs();
	
	static Principal parsePrincipal(SexpList l) throws SexpParseException {
		// a principal doesn't have it's own "principal" block
		String type = l.getType();
		if (type.equals("public-key"))
			return PublicKey.parsePublicKey(l);
		if (type.equals("hash"))
			return PublicKeyHash.parsePublicKeyHash(l);

		throw new SexpParseException("unrecognized principal type: " + type);
	}
}