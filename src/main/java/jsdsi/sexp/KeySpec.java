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
 * An S-expression encoded key.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2005/02/17 13:37:45 $
 */
public class KeySpec extends java.security.spec.EncodedKeySpec {
	public KeySpec(byte[] encodedKey) {
		super(encodedKey);
	}
	public String getFormat() {
		return "SEXP";
	}
}