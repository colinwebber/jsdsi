/*
  Copyright 2002 Massachusetts Institute of Technology
    
  Permission to use, copy, modify, and distribute this program for any
  purpose and without fee is hereby granted, provided that this
  copyright and permission notice appear on all copies and supporting
  documentation, the name of M.I.T. not be used in advertising or
  publicity pertaining to distribution of the program without specific
  prior permission, and notice be given in supporting documentation that
  copying and distribution is by permission of M.I.T.  M.I.T. makes no
  representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied warranty.
*/
package jsdsi.sexp;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jsdsi.Obj;

/**
   Writes serialized Objs as S-expressions to an underlying stream.
   Supports the canonical, transport, and readable S-expression
   encodings.
   
   @author Sameer Ajmani
   @see SexpOutputStream
   @see ObjInputStream
**/
public class ObjOutputStream extends FilterOutputStream
{
  SexpOutputStream s;
  /**
     Creates a new ObjOutputStream that writes to the given stream.
  **/
  public ObjOutputStream(OutputStream os)
  {
    super(new SexpOutputStream(os));
    s = (SexpOutputStream)super.out;
  }
  /**
     Writes an Obj to the stream as a readable S-expression.
     @param o the Obj to write
     @param offset spaces indented from left
     @param width total width of window, in characters
     @param last spaces reserved on right (e.g., for closing parens)
     @throws IOException if there is an IO error
  **/
  public void writeReadable(Obj o, int offset, int width, int last)
    throws IOException
  {
    s.writeReadable(o.toSexp(), offset, width, last);
  }
  /**
     Writes an Obj to the stream as a transport S-expression.
     @param o the Obj to write
     @throws IOException if there is an IO error
  **/
  public void writeTransport(Obj o) throws IOException
  {
    s.writeTransport(o.toSexp());
  }
  /**
     Writes an Obj to the stream as a canonical S-expression.
     @param o the Obj to write
     @throws IOException if there is an IO error
  **/
  public void writeCanonical(Obj o) throws IOException
  {
    s.writeCanonical(o.toSexp());
  }
}
