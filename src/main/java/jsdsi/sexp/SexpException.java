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
 * This exception is used to signify an error when parsing an input
 * stream into a <code>Sexp</code>.  It indicates that there is some error and 
 * the bytes in the input stream do not correctly represent an S-Expression.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/08/25 23:18:11 $
 */
public class SexpException extends Exception {
    
    private static final long serialVersionUID = 5905798305971255597L;
    
	public SexpException() {
		super();
	}
	public SexpException(String s) {
		super(s);
	}
}