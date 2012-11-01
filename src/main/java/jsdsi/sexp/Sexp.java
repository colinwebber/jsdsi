/*
 * Copyright 2002 Massachusetts Institute of Technology
 * 
 * Permission to use, copy, modify, and distribute this program for any purpose
 * and without fee is hereby granted, provided that this copyright and
 * permission notice appear on all copies and supporting documentation, the
 * name of M.I.T. not be used in advertising or publicity pertaining to
 * distribution of the program without specific prior permission, and notice be
 * given in supporting documentation that copying and distribution is by
 * permission of M.I.T. M.I.T. makes no representations about the suitability
 * of this software for any purpose. It is provided "as is" without express or
 * implied warranty.
 */
package jsdsi.sexp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 *
 * Abstract S-expression superclass. Provides static utility functions.
 * S-expressions are immutable.
 * 
 * @see SexpString
 * @see SexpList
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/08/25 23:18:11 $
 */
public abstract class Sexp implements Serializable {
    
    private static final long serialVersionUID = 1708696962807474053L;
    
    /**
     * Returns the character encoding for S-expressions (8859_1).
     *
     * @return "8859_1"
     **/
    public static final String getEncoding() {
        return "8859_1";
    }
	/**
	 * Tests if a character is whitespace.
	 * 
	 * @param c
	 *            the character to be tested
	 * @return <code>true</code> if the character is whitespace, <code>false</code>
	 *         otherwise
	 */
	public static final boolean isWhiteSpace(int c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}

	/**
	 * Tests if a character is a decimal digit.
	 * 
	 * @param c
	 *            the character to be tested
	 * @return <code>true</code> if the character is a digit between 0 and 9
	 *         inclusive, <code>false</code> otherwise
	 */
	public static final boolean isDecimalDigit(int c) {
		return (c >= '0' && c <= '9');
	}

	/**
	 * Tests if a character is a hexadecimal digit.
	 * 
	 * @param c
	 *            the character to be tested.
	 * @return <code>true</code> if the character is a decimal digit or a
	 *         letter between 'a' and 'f' upper or lowercase, <code>false</code>
	 *         otherwise .
	 */
	public static final boolean isHexDigit(int c) {
		return (
			isDecimalDigit(c)
				|| (c >= 'a' && c <= 'f')
				|| (c >= 'A' && c <= 'F'));
	}

	/**
	 * Tests if a character is a base 64 digit.
	 * 
	 * @param c
	 *            the character to be tested.
	 * @return <code>true</code> if the character is a decimal digit, a
	 *         letter of the alphabet or '+' or '/', <code>false</code>
	 *         otherwise.
	 */
	public static final boolean isBase64Digit(int c) {
		return (
			isDecimalDigit(c)
				|| (c >= 'a' && c <= 'z')
				|| (c >= 'A' && c <= 'Z')
				|| c == '+'
				|| c == '/');
	}

	/**
	 * Tests if a character is a token character.
	 * 
	 * @param c
	 *            the character to be tested.
	 * @return <code>true</code> if the character is a legal token character
	 *         for an S-expression, <code>false</code> otherwise.
	 */
	public static final boolean isTokenChar(int c) {
		return (
			isBase64Digit(c)
				|| c == '.'
				|| c == '-'
				|| c == '='
				|| c == '*'
				|| c == ':'
				|| c == '_');
	}

	/**
	 * Maps integers to hexadecimal digits.
	 */
	public static final char[] hexDigit =
		{
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			'A',
			'B',
			'C',
			'D',
			'E',
			'F' };

	/**
	 * Encodes data in hexadecimal and writes it to a character stream
	 * without any line-wrapping.
	 * 
	 * @param data
	 *            the data to encode.
	 * @param out
	 *            the character stream.
	 */
	static void writeHex(byte[] data, Writer out) throws IOException {
		for (int i = 0; i < data.length; i++) {
			out.write(Sexp.hexDigit[data[i] >>> 4 & 0x0F]);
			out.write(Sexp.hexDigit[data[i] & 0x0F]);
		}
	}

	/**
	 * Encodes data in hexadecimal and writes it to a character stream
	 * with appropriat line-wrapping.
	 * 
	 * @param data
	 *            the data to encode.
	 * @param out
	 *            the character stream.
	 * @param offset
	 *            spaces indented from left.
	 * @param width
	 *            total width of window, in characters.
	 * @param last
	 *            spaces reserved on right (e.g., for closing parens).
	 */
	static void writeHex(
		byte[] data,
		Writer out,
		int offset,
		int width,
		int last)
		throws IOException {
		StringWriter w = new StringWriter();
		writeHex(data, w);
		writeWrapped(w.toString(), out, offset, width, last);
	}

	/**
	 * Maps integers to base-64 digits.
	 */
	public static final char[] base64Digit =
		{
			'A',
			'B',
			'C',
			'D',
			'E',
			'F',
			'G',
			'H',
			'I',
			'J',
			'K',
			'L',
			'M',
			'N',
			'O',
			'P',
			'Q',
			'R',
			'S',
			'T',
			'U',
			'V',
			'W',
			'X',
			'Y',
			'Z',
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'o',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'x',
			'y',
			'z',
			'0',
			'1',
			'2',
			'3',
			'4',
			'5',
			'6',
			'7',
			'8',
			'9',
			'+',
			'/' };

	/**
	 * Encodes data in base-64 and writes it to a character stream without any
	 * line-wrapping.
	 * 
	 * @param data
	 *            the data to encode.
	 * @param out
	 *            the character stream.
	 */
	static void writeBase64(byte[] data, Writer out) throws IOException {
		int carry = 0;
		for (int i = 0; i < data.length; i++) {
			switch (i % 3) {
				case 0 :
					out.write(Sexp.base64Digit[data[i] >>> 2 & 0x3F]);
					carry = data[i] << 4 & 0x30;
					break;
				case 1 :
					out.write(Sexp.base64Digit[carry + (data[i] >>> 4 & 0x0F)]);
					carry = data[i] << 2 & 0x3C;
					break;
				case 2 :
					out.write(Sexp.base64Digit[carry + (data[i] >>> 6 & 0x03)]);
					out.write(Sexp.base64Digit[data[i] & 0x3F]);
					carry = 0;
					break;
			}
		}
		switch (data.length % 3) {
			case 1 :
				out.write(Sexp.base64Digit[carry & 0x3F]);
				out.write('=');
				out.write('=');
				break;
			case 2 :
				out.write(Sexp.base64Digit[carry & 0x3F]);
				out.write('=');
				break;
			default :
				break;
		}
	}

	/**
	 * Encodes data in base-64 and writes it to a character stream with
	 * appropriate line-wrapping.
	 * 
	 * @param data
	 *            the data to encode.
	 * @param out
	 *            the character stream.
	 * @param offset
	 *            spaces indented from left.
	 * @param width
	 *            total width of window, in characters.
	 * @param last
	 *            spaces reserved on right (e.g., for closing parens).
	 */
	static void writeBase64(
		byte[] data,
		Writer out,
		int offset,
		int width,
		int last)
		throws IOException {
		StringWriter w = new StringWriter();
		writeBase64(data, w);
		writeWrapped(w.toString(), out, offset, width, last);
	}

	static void newline(Writer out, int offset) throws IOException {
		out.write('\n');
		for (int i = 0; i < offset; i++) {
			out.write(' ');
		}
	}

	/**
	 * Writes s to out with line-wrapping as needed to fit in the window.
	 *
	 * @param s
	 *            the string to write.
	 * @param out
	 *            the character stream.
	 * @param offset
	 *            spaces indented from left.
	 * @param width
	 *            total width of window, in characters.
	 * @param last
	 *            spaces reserved on right (e.g., for closing parens).
	 * @throws IOException
	 */
	static void writeWrapped(
			String s,
			Writer out,
			int offset,	
			int width,
			int last)
	throws IOException {
		char[] cs = s.toCharArray();
		int linesize = Math.max(width - offset - last, 2);
		int i = 0;
		while (true) {
			int towrite = Math.min(cs.length - i, linesize);
			out.write(cs, i, towrite);
			i += towrite;
			if (i < cs.length) {
				newline(out, offset);
			} else {
				break;
			}
		}
	}

	/**
	 * Converts a byte array to a string using the 8859_1 encoding.
	 */
	public static String decodeString(byte[] b) {
		try {
			return new String(b, getEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	/**
	 * Converts a string to a byte array using the 8859_1 encoding.
	 */
	public static byte[] encodeString(String s) {
		try {
			return s.getBytes(getEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	/**
	 * Writes this S-expression to a byte stream in canonical form.
	 */
	public abstract void writeCanonical(OutputStream out) throws IOException;

	/**
	 * Writes this S-expression to a byte stream in transport form.
	 */
	public final void writeTransport(OutputStream out) throws IOException {
		OutputStreamWriter w = new OutputStreamWriter(out);
		w.write('{');
		// NOTE: Cryptix's Base64OutputStream doesn't work here
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		writeCanonical(bos);
		byte[] data = bos.toByteArray();
		writeBase64(data, w, 0, 4 * ((data.length + 2) / 3), 0);
		w.write('}');
		w.flush();
	}

	/**
	 * Writes this S-expression to a character stream in readable form.
	 * 
	 * @param offset
	 *            spaces indented from left.
	 * @param width
	 *            total width of window, in characters.
	 * @param last
	 *            spaces reserved on right (e.g., for closing parens).
	 */
	public abstract void writeReadable(
		Writer out,
		int offset,
		int width,
		int last)
		throws IOException;

	/**
	 * Returns the length of this S-expression as if it were printed on one
	 * line. Caches result for efficiency.
	 * 
	 * @see #getReadableLenImpl()
	 */
	public final int getReadableLen() {
		if (readableLen < 0) {
			readableLen = getReadableLenImpl();
		}
		return readableLen;
	}

	private int readableLen = -1;

	/**
	 * Returns the length of this S-expression as if it were printed on one
	 * line.
	 */
	abstract int getReadableLenImpl();
}
