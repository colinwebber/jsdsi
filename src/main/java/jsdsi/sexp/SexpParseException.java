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
package jsdsi.sexp;

/**
 * This exception is used to signify an error when parsing an Sexp into
 * a SDSI Object.  It indicates that the S-Expression does not correctly
 * represent a SDSI Object.
 * 
 * @author Alexander Morcos, Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class SexpParseException extends Exception {
	Sexp faulty;

	public SexpParseException(String s) {
		super(s);
	}
	
	public SexpParseException(Throwable t) {
		super(t);
	}
	
	public SexpParseException(String s, Throwable t) {
		super(s, t);
	}
	
	public SexpParseException(String s, Sexp faulty) {
		this(s);
		this.faulty = faulty;
	}
	
	public Sexp getSexp() {
		return faulty;
	}
}