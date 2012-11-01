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
import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * Checks whether a certificate appears on a revocation list.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Revocation extends OnlineTest {
    
    private static final long serialVersionUID = 1234467293412206221L;
    
	/**
	 * @see jsdsi.OnlineTest#OnlineTest(Principal, URI[])
	 */
	public Revocation(Principal p, URI[] u) {
		super(p, u);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof Revocation) && super.equals(o);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

	String getSexpType() {
		return "crl";
	}

	Sexp[] getSexpParts() {
		return new Sexp[0];
	}

	static Revocation parseRevocation(Principal p, URI[] u, Iterator obody)
		throws SexpParseException {
		SexpUtil.checkDone(obody, "revocation"); // TODO; support s-parts
		return new Revocation(p, u);
	}
}