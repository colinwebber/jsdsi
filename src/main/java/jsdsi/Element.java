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

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;

/**
 * An element of a Sequence.
 * 
 * @see Sequence
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public interface Element {
	public SexpList toSexp();

	static class Default {
		static Element parseElement(SexpList l) throws SexpParseException {
			String type = l.getType();

			if (type.equals("signature"))
				return Signature.parseSignature(l);
			if (type.equals("cert"))
				return Cert.parseCert(l);
			if (type.equals("do"))
				return Op.parseOp(l);
			if (type.equals("public-key"))
				return PublicKey.parsePublicKey(l);

			throw new SexpParseException(
				"unrecognized sequence element type: " + type);
		}
	}
}