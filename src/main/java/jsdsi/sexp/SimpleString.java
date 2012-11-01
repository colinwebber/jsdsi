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
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;

/**
 * A simple string of data.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
class SimpleString implements Serializable {
	private byte data[];

	/**
	 * Creates a new SimpleString.
	 * 
	 * @param  d the content of the string
	 */
	SimpleString(byte[] d) {
		data = d;
	}

	/**
	 * Create a new SimpleString.
	 * 
	 * @param s the content of the string
	 */
	SimpleString(String s) {
		data = Sexp.encodeString(s);
	}

	/**
	 * Returns the content of this string as a String.
	 * 
	 * @return the content of this string as a String.
	 */
	public String toString() {
		return Sexp.decodeString(data);
	}

	/**
	 * Returns the content of this string as a byte array.
	 * 
	 * @return the content of this string as a byte array.
	 */
	public byte[] toByteArray() {
		return data;
	}

	/**
	 * Writes this string to a byte stream in canonical form.
	 */
	public void writeCanonical(OutputStream out) throws IOException {
		out.write(Sexp.encodeString(Integer.toString(data.length)));
		out.write(':');
		out.write(data);
	}

	/**
	 * Writes this string to a character stream in readable form.
	 * 
	 * @param  offset spaces indented from left.
	 * @param  width total width of window, in characters.
	 * @param  last spaces reserved on right (e.g., for closing parens).
	 */
	public void writeReadable(Writer out, int offset, int width, int last)
		throws IOException {
		if (canBeToken())
			writeToken(out, offset, width, last);
		else if (canBeQuoted())
			writeQuoted(out, offset, width, last);
		else if (canBeHex())
			writeHex(out, offset, width, last);
		else
			writeBase64(out, offset, width, last);
	}

	int getReadableLen() {
		if (canBeToken())
			return data.length;
		else if (canBeQuoted())
			return data.length + 2;
		else if (canBeHex())
			return 2 * data.length + 2;
		else
			return 4 * ((data.length + 2) / 3) + 2;
	}

	boolean canBeToken() {
		if (!tokenChecked) {
			tokenChecked = true;
			tokenResult = canBeTokenImpl();
		}
		return tokenResult;
	}
	private boolean tokenChecked = false;
	private boolean tokenResult = false;

	private boolean canBeTokenImpl() {
		if (data.length == 0)
			return false;
		if (Sexp.isDecimalDigit(data[0]))
			return false;
		for (int i = 0; i < data.length; i++) {
			if (!Sexp.isTokenChar(data[i]))
				return false;
		}
		return true;
	}

	boolean canBeQuoted() {
		if (!quotedChecked) {
			quotedChecked = true;
			quotedResult = canBeQuotedImpl();
		}
		return quotedResult;
	}
	private boolean quotedChecked = false;
	private boolean quotedResult = false;

	boolean canBeQuotedImpl() {
		for (int i = 0; i < data.length; i++) {
			if (!Sexp.isTokenChar(data[i]) && !(data[i] == ' '))
				return false;
		}
		return true;
	}

	boolean canBeHex() {
		return data.length < 8;
	}

	void writeToken(Writer out, int offset, int width, int last)
		throws IOException {
		Sexp.writeWrapped(Sexp.decodeString(data), out, offset, width, last);
	}

	void writeQuoted(Writer out, int offset, int width, int last)
		throws IOException {
		// TODO: encode escape sequences?
		out.write('\"');
		writeToken(out, offset, width, last + 1);
		out.write('\"');
	}

	void writeHex(Writer out, int offset, int width, int last)
		throws IOException {
		// TODO: insert newlines
		out.write('#');
		Sexp.writeHex(data, out, offset, width, last);
		out.write('#');
	}

	void writeBase64(Writer out, int offset, int width, int last)
		throws IOException {
		// TODO: insert newlines
		out.write('|');
		Sexp.writeBase64(data, out, offset, width, last);
		out.write('|');
	}
}