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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;

import cryptix.util.mime.Base64InputStream;

/**
 * Reads serialized S-expressions from an underlying stream.  Supports
 * the canonical, transport, and readable S-expression encodings.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 * 
 * @see SexpOutputStream
 */
public class SexpInputStream extends FilterInputStream
    implements SexpInput
{
	private PushbackInputStream p;
	
	private int lineno = 1;
	
	/**
	 * Creates a new SexpInputStream that reads from the given stream.
	 */
	public SexpInputStream(InputStream is) {
		super(new PushbackInputStream(is));
		p = (PushbackInputStream) super.in;
	}
	
	private int readSafe() throws SexpException, IOException {
		int nextChar = p.read();
		if (nextChar == -1) {
			throw new SexpException("Line " + lineno + ": Unexpected EOF");
		}
		return nextChar;
	}
	private int readSkipWhitespace() throws SexpException, IOException {
		int nextChar;
		do {
			nextChar = readSafe();
			if (nextChar == '\n') {
				lineno++;
			}
		} while (Character.isWhitespace((char) nextChar));
		return nextChar;
	}

	/**
	 * Reads the next S-expression from the stream.
	 * 
	 * @return the decoded S-expression.
	 * @throws EOFException if the stream is empty.
	 * @throws SexpException if there is a decoding error.
	 * @throws IOException if there is an IO error.
	 */
	public Sexp readSexp() throws SexpException, IOException {
		int nextChar;
		try {
			nextChar = readSkipWhitespace();
		} catch (SexpException e) {
			// this EOF is not necessarily "unexpected"
			throw new EOFException();
		}
		if (nextChar == '(') {
			return readSexpList();
		}
		if (nextChar == '{') {
			// read transport form
			byte[] decoded = readBase64Until('}');
			return (new SexpInputStream(new ByteArrayInputStream(decoded)))
				.readSexp();
		}
		p.unread(nextChar);
		return readSexpString();
	}

	private SexpList readSexpList() throws SexpException, IOException {
		// read type
		int nextChar = readSkipWhitespace();
		if (nextChar == ')') {
			throw new SexpException("Line " + lineno + ": Empty list");
		}
		p.unread(nextChar);
		Sexp type = readSexp();
		if (!(type instanceof SexpString)) {
			throw new SexpException("Line " + lineno + ": Expected list type");
		}
		// read elements
		ArrayList l = new ArrayList();
		while (true) {
			nextChar = readSkipWhitespace();
			if (nextChar == ')') {
				// complete list
				Sexp[] elems = new Sexp[l.size()];
				l.toArray(elems);
				return new SexpList((SexpString) type, elems);
			}
			// add next sexp to list
			p.unread(nextChar);
			l.add(readSexp());
		}
	}

	private SexpString readSexpString() throws SexpException, IOException {
		int nextChar = readSafe();
		byte[] display = null;
		if (nextChar == '[') {
			// read display hint
			display = readByteArray();
			if (readSafe() != ']') {
				throw new SexpException(
					"Line " + lineno + ": Missing ']' after display hint");
			}
		} else {
			p.unread(nextChar);
		}
		// read string content
		byte[] content = readByteArray();
		if (display == null) {
			return new SexpString(content);
		}
		return new SexpString(display, content);
	}

	private byte[] readByteArray() throws SexpException, IOException {
		int nextChar = readSafe();
		if (nextChar == '0') {
			// leading zero allowed only if it defines the empty string
			if (readSafe() != ':') {
				throw new SexpException(
					"Line " + lineno + ": Unexpected leading '0'");
			}
			return new byte[0];
		}

		int length = 0; // we handled length == 0 already
		if (Sexp.isDecimalDigit(nextChar)) {
			// read the length field
			ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
			do {
				bos.write(nextChar);
				nextChar = readSafe();
			} while (Sexp.isDecimalDigit(nextChar));
			length = Integer.parseInt(new String(bos.toByteArray(), "8859_1"));
		}
		// read the data itself
		switch (nextChar) {
			case ':' :
				if (length > 0) {
					byte[] data = new byte[length];
					(new DataInputStream(p)).readFully(data);
					return data;
				} else {
					return readTokenData(nextChar);
				}
			case '#' :
				return readHexData(length);
			case '|' :
				return readBase64Data(length);
			case '\"' :
				return readQuotedData(length);
			default :
				return readTokenData(nextChar);
		}
	}

	private byte[] readTokenData(int nextChar)
		throws SexpException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		do {
			bos.write(nextChar);
			nextChar = readSafe();
		} while (Sexp.isTokenChar(nextChar));
		p.unread(nextChar);
		return bos.toByteArray();
	}

	private byte[] readHexData(int length) throws SexpException, IOException {
		byte[] bs = new byte[2];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while (true) {
			int nextChar = readSkipWhitespace();
			if (nextChar == '#') {
				byte[] data = bos.toByteArray();
				if ((length > 0) && (length != data.length)) {
					throw new SexpException(
						"Line " + lineno + ": Base-16 encoded length mismatch");
				}
				return data;
			}
			bs[0] = (byte) nextChar;
			bs[1] = (byte) readSkipWhitespace();
			bos.write(Integer.parseInt(new String(bs, "8859_1"), 16));
		}
	}

	private byte[] readBase64Data(int length)
		throws SexpException, IOException {
		if (length > 0) {
			// length provided: read the expected amount of data
			byte[] data = new byte[length];
			(new DataInputStream(new Base64InputStream(p))).readFully(data);
			if (readSkipWhitespace() != '|') {
				throw new SexpException(
					"Line "
						+ lineno
						+ ": Missing '|' after base-64 encoded data");
			}
			return data;
		}
		// no length provided: read until we find terminator
		return readBase64Until('|');
	}

	private byte[] readBase64Until(int terminator)
		throws SexpException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int numEquals = 0;
		int nextChar = readSkipWhitespace();
		while (Sexp.isBase64Digit(nextChar)) {
			bos.write(nextChar);
			nextChar = readSkipWhitespace();
		}
		while (nextChar == '=') {
			numEquals++;
			bos.write(nextChar);
			nextChar = readSkipWhitespace();
		}
		if (nextChar != terminator) {
			throw new SexpException(
				"Line "
					+ lineno
					+ ": Missing '"
					+ terminator
					+ "' after base-64 encoded data, got "
					+ (char) nextChar);
		}
		// decode the encoded data
		byte[] encoded = bos.toByteArray();
		if ((encoded.length % 4) != 0) {
			throw new SexpException(
				"Line "
					+ lineno
					+ ": Base-64 encoded length not a multiple of 4");
		}
		byte[] decoded = new byte[3 * (encoded.length / 4) - numEquals];
		ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
		(new DataInputStream(new Base64InputStream(bis))).readFully(decoded);
		if (bis.read() != -1) {
			// sanity check: we should have read the entire encoded array
			throw new Error("Did not fully read Base-64 encoded data");
		}
		return decoded;
	}

	private byte[] readQuotedData(int length)
		throws SexpException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while (true) {
			int nextChar = readSafe();
			if (nextChar == '\"') {
				byte[] data = bos.toByteArray();
				if ((length > 0) && (length != data.length)) {
					throw new SexpException(
						"Line " + lineno + ": Quoted string length mismatch");
				}
				return data;
			}
			if (nextChar == '\\') {
				nextChar = processEscapeSequence();
				if (nextChar == -1)
					continue; // read next char
			}
			bos.write(nextChar);
		}
	}

	private int processEscapeSequence() throws SexpException, IOException {
		int nextChar = readSafe();
		switch (nextChar) {
			case 'b' :
				return '\b';
			case 't' :
				return '\t';
			case 'v' :
				// no vertical tab
				return '\t';
			case 'f' :
				return '\f';
			case 'n' :
				return '\n';
			case 'r' :
				return '\r';
			case '\'' :
				return '\'';
			case '\"' :
				return '\"';
			case '\\' :
				return '\\';
			case '\n' :
				// slash separates lines; check for \n\r
				nextChar = readSafe();
				if (nextChar != '\r') {
					p.unread(nextChar);
				}
				return -1;
			case '\r' :
				// slash separates lines; check for \r\n
				nextChar = readSafe();
				if (nextChar != '\n') {
					p.unread(nextChar);
				}
				return -1;
			case 'x' :
				byte[] hex = new byte[2];
				hex[0] = (byte) readSafe();
				hex[1] = (byte) readSafe();
				return Integer.parseInt(new String(hex, "8859_1"), 16);
			default :
				byte[] oct = new byte[3];
				oct[0] = (byte) nextChar;
				oct[1] = (byte) readSafe();
				oct[2] = (byte) readSafe();
				return Integer.parseInt(new String(oct, "8859_1"), 8);
		}
	}
}
