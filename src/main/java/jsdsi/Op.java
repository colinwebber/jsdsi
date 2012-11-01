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
 * An operation instruction for a verifier.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class Op extends Obj implements Element {
    
    private static final long serialVersionUID = 3639502914529665105L;
    
	static Op parseOp(SexpList l) throws SexpParseException {
		SexpUtil.checkType(l, "do");
		Iterator obody = SexpUtil.getBody(l);
		String opcode = SexpUtil.getNextString(obody, "opcode");
		if (opcode.equals("hash")) {
			return HashOp.parseHashOp(obody);
		}
		throw new SexpParseException("unrecognized opcode: " + opcode);
	}
}