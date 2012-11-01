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
 * A hash operation that instructs a verifier to hash an object for
 * later reference.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class HashOp extends Op {
    
    private static final long serialVersionUID = 731956766278526069L;
    
	/**
	 * The hash algorithm used in this hash op.
	 */
	private transient final String algo;

	/**
	 * Creates a new <code>HashOp</code> for a given hash algorithm.
	 * 
	 * @param  a name of the hash algorithm for this hash op.
	 */
	public HashOp(String a) {
		assert(a != null) : "null algo";
		algo = a;
	}

	/**
	 * Returns the name of the hash algorithm of this <code>HashOp</code>.
	 * 
	 * @return the name of the hash algorithm used.
	 */
	public String getAlgorithm() {
		return algo;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof HashOp) && algo.equals(((HashOp) o).algo);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return algo.hashCode();
	}

	/**
	 * Creates an <code>SexpList</code>-representation of this
	 * <code>HashOp</code>-
	 * 
	 * @return the <code>SexpList</code> representing this
	 * 	<code>HashOp</code>.
	 */
	public SexpList toSexp() {
		Sexp[] ss = new Sexp[2];
		ss[0] = SexpUtil.toSexp("hash");
		ss[1] = SexpUtil.toSexp(getAlgorithm());
		return SexpUtil.toSexp("do", ss);
	}

	/**
	 * Parses a <code>HashOp</code> where the parameters are stored
	 * in a given <code>Iterator</code>.
	 * 
	 * @param obody the <code>Iterator</code> that holds the parameters
	 * 	of the <code>HashOp</code>-
	 * @return a <code>HashOp</code> constructed from the parameters
	 * 	stores in <code>obody</code>.
	 * @throws SexpParseException
	 */
	static HashOp parseHashOp(Iterator obody) throws SexpParseException {
		String algo = SexpUtil.getNextString(obody, "hash algo");
		SexpUtil.checkDone(obody, "op hash");
		return new HashOp(algo);
	}
}