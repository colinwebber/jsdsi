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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import jsdsi.Obj;

/**
  * Reads SDSI objects encoded as S-expressions from an underlying
  * stream.  Supports the canonical, transport, and readable S-expression
  * encodings.
  * 
  * @author Sameer Ajmani
  * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
  * 
  * @see SexpInputStream
  * @see ObjOutputStream
  */
public class ObjInputStream extends FilterInputStream {
	public static final int MAX_OBJ_SIZE = 4096;
	private SexpInputStream s;
	/**
	  * Creates a new ObjInputStream that reads from the given stream.
	  */
	public ObjInputStream(InputStream is) {
		super(new SexpInputStream(is));
		s = (SexpInputStream) super.in;
	}
	/**
	  * Reads an Obj from the underlying stream.
	  */
	public Obj readObj()
		throws SexpParseException, SexpException, IOException {
		try {
			if (s.markSupported()) {
				s.mark(MAX_OBJ_SIZE);
			}
			return Obj.parseObj(s.readSexp());
		} catch (SexpParseException e) {
			if (s.markSupported()) {
				s.reset();
			}
			throw e;
		} catch (SexpException e) {
			if (s.markSupported()) {
				s.reset();
			}
			throw e;
		} catch (IOException e) {
			if (s.markSupported()) {
				s.reset();
			}
			throw e;
		}
	}
}