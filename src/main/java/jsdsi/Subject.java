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
 * A subject of a certificate or an ACL entry.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public interface Subject {
	public SexpList toSexp();
	// TODO: boolean sameSubjectAs(Subject s);

	static class Default {
		static Subject parseSubject(SexpList l) throws SexpParseException {
			return parseSubject(l, null);
		}

		static Subject parseSubject(SexpList l, Principal issuer)
			throws SexpParseException {
			// assumes already parsed outer "subject" block, if any
			String type = l.getType();
			if (type.equals("name"))
				return Name.parseName(l, issuer);
			if (type.equals("k-of-n"))
				return Threshold.parseThreshold(l, issuer);
			if (type.equals("object-hash"))
				return ObjectHash.parseObjectHash(l);
			// FIXME: defaulting to principal is confusing
			return Principal.parsePrincipal(l);
		}
	}
}