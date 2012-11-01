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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A byte string with an optional display hint.
 * 
 * @see Sexp
 * 
 * @author Alexander Morcos, Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class SexpString extends Sexp {
	private SimpleString display;
	private SimpleString content;

	// implements serializable
	SexpString() {
	}

	// implements serializable
	private void writeObject(ObjectOutputStream out) throws IOException {
		writeCanonical(out);
	}

	// implements serializable
	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		try {
			SexpString s = (SexpString) (new SexpInputStream(in)).readSexp();
			this.display = s.display;
			this.content = s.content;
		} catch (SexpException e) {
			throw (IOException) new IOException().initCause(e);
		} catch (ClassCastException e) {
			throw (IOException) new IOException().initCause(e);
		}
	}

	/**
	 * Creates a new <code>SexpString</code> with the given display-hint 
	 * and content.
	 * 
	 * @param  d the display hint.
	 * @param  c the content of the string.
	 */
	public SexpString(byte[] d, byte[] c) {
		display = new SimpleString(d);
		content = new SimpleString(c);
	}

	/**
	 * Creates a new <code>SexpString</code> with no display-hint.
	 * 
	 * @param  c the content of the string.
	 */
	public SexpString(String c) {
		display = null;
		content = new SimpleString(c);
	}

	/**
	 * Creates a new <code>SexpString</code> with the given display-hint 
	 * and content.
	 * 
	 * @param  d the display hint.
	 * @param  c the content of the string.
	 */
	public SexpString(String d, String c) {
		display = new SimpleString(d);
		content = new SimpleString(c);
	}

	/**
	 * Creates a new <code>SexpString</code> with no display-hint.
	 * 
	 * @param  c the content of the string.
	 */
	public SexpString(byte[] c) {
		display = null;
		content = new SimpleString(c);
	}

	/**
	 * Creates a new <code>SexpString</code> with the given display-hint 
	 * and content.
	 * 
	 * @param  d the display hint
	 * @param  c the content of the string.
	 */
	public SexpString(String d, byte[] c) {
		display = new SimpleString(d);
		content = new SimpleString(c);
	}

	/**
	 * Returns the content of this string as a <code>String</code>.
	 * 
	 * @return the content of this string as a <code>String</code>.
	 */
	public String toString() {
		return content.toString();
	}

	/**
	 * Returns the content of this string as a byte array.
	 * 
	 * @return the content of this string as a byte array.
	 */
	public byte[] toByteArray() {
		return content.toByteArray();
	}

	public void writeCanonical(OutputStream out) throws IOException {
		if (display != null) {
			out.write('[');
			display.writeCanonical(out);
			out.write(']');
		}
		content.writeCanonical(out);
	}

	public void writeReadable(Writer out, int offset, int width, int last)
		throws IOException {
		if (display != null) {
			out.write('[');
			display.writeReadable(out, offset, width, last);
			out.write(']');
			if (getReadableLen() > width - offset - last) {
				out.write('\n');
			}
		}
		content.writeReadable(out, offset, width, last);
	}

	int getReadableLenImpl() {
		if (display != null) {
			return display.getReadableLen() + 2 + content.getReadableLen();
		} else {
			return content.getReadableLen();
		}
	}
}