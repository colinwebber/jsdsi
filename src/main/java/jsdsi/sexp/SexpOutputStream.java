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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Writes serialized S-expressions to an underlying stream.  Supports
 * the canonical, transport, and readable S-expression encodings.
 * 
 * @see SexpInputStream
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class SexpOutputStream extends FilterOutputStream {
	/**
	 * Creates a new <code>SexpOutputStream</code> that writes to the given 
	 * stream.
	 */
	public SexpOutputStream(OutputStream os) {
		super(os);
	}

	/**
	 * Writes an S-expression to the stream in readable form.
	 * 
	 * @param  s the S-expression to write.
	 * @param  offset spaces indented from left.
	 * @param  width total width of window, in characters.
	 * @param  last spaces reserved on right (e.g., for closing parens).
	 * @throws IOException if there is an IO error.
	 */
	public void writeReadable(Sexp s, int offset, int width, int last)
		throws IOException {
		Writer w = new OutputStreamWriter(out);
		s.writeReadable(w, offset, width, last);
		w.flush();
	}

	/**
	 * Writes an S-expression to the stream in transport form.
	 * 
	 * @param  s the S-expression to write.
	 * @throws IOException if there is an IO error.
	 */
	public void writeTransport(Sexp s) throws IOException {
		s.writeTransport(out);
	}

	/**
	 * Writes an S-expression to the stream in canonical form.
	 * 
	 * @param  s the S-expression to write
	 * @throws IOException if there is an IO error
	 */
	public void writeCanonical(Sexp s) throws IOException {
		s.writeCanonical(out);
	}

    /**
     * Returns a new SexpOutput object that writes S-expressions to this
     * stream in readable form.
     *
	 * @param  offset spaces indented from left.
	 * @param  width total width of window, in characters.
	 * @param  last spaces reserved on right (e.g., for closing parens).
     **/
    public SexpOutput toReadable(final int offset,
                                 final int width,
                                 final int last)
    {
        return new SexpOutput() {
                public void writeSexp(Sexp s) throws IOException
                {
                    SexpOutputStream.this.writeReadable
                        (s, offset, width, last);
                }
                public void flush() throws IOException
                {
                    SexpOutputStream.this.flush();
                }
            };
    }
    
    /**
     * Returns a new SexpOutput object that writes S-expressions to this
     * stream in canonical form.
     **/
    public SexpOutput toCanonical() {
        return new SexpOutput() {
                public void writeSexp(Sexp s) throws IOException
                {
                    SexpOutputStream.this.writeCanonical(s);
                }
                public void flush() throws IOException
                {
                    SexpOutputStream.this.flush();
                }
            };
    }

    /**
     * Returns a new SexpOutput object that writes S-expressions to this
     * stream in transport form.
     **/
    public SexpOutput toTransport() {
        return new SexpOutput() {
                public void writeSexp(Sexp s) throws IOException
                {
                    SexpOutputStream.this.writeTransport(s);
                }
                public void flush() throws IOException
                {
                    SexpOutputStream.this.flush();
                }
            };
    }
}
