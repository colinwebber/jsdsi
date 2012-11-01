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
import jsdsi.sexp.SexpString;
import jsdsi.sexp.SexpUtil;

/**
 * Distinguishes tags defined by expressions from tags in general (the
 * latter includes Tag.ALL_TAG and Tag.NULL_TAG).  Only ExprTags can
 * appear within other tags (like SetTags or SimpleTags).
 *
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class ExprTag extends Tag {
    
    private static final long serialVersionUID = -5113636612205223121L;
    
	static ExprTag parseExprTag(Sexp s) throws SexpParseException {
		if (s instanceof SexpString) {
			return new StringTag(((SexpString) s).toString());
		}
		SexpList l = (SexpList) s;
		if (!l.getType().equals("*")) {
			return SimpleTag.parseSimpleTag(l);
		}
		Iterator tbody = SexpUtil.getBody(l);
		String type = SexpUtil.getNextString(tbody, "ExprTag type");
		if (type.equals("set"))
			return SetTag.parseSetTag(tbody);
		if (type.equals("range"))
			return RangeTag.parseRangeTag(tbody);
		if (type.equals("prefix"))
			return PrefixTag.parsePrefixTag(tbody);
		if (type.equals("reverse-prefix"))
			return ReversePrefixTag.parseReversePrefixTag(tbody);
		throw new SexpParseException("Unrecognized ExprTag type: " + type);
	}
}