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
package jsdsi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.StringWriter;

import jsdsi.sexp.ObjInputStream;
import jsdsi.sexp.ObjOutputStream;
import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * Common superclass of all SPKI/SDSI objects.
 * 
 * <p><strong>Note:</strong> As serialization is performed by writing the object to the the stream as
 * a canonical Sexpression, all subclasses of <code>jsdsi.Obj</code> should declare their fields as
 * <code>transient</code>.</p>
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.10 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class Obj implements Serializable {
    
    private static final long serialVersionUID = -9005382351047668808L;

    /** Default value for Readable S-expression format */
    private static final int OFFSET = 0;

    /** Default value for Readable S-expression format */
    private static final int WIDTH = 72;

    /** Default value for Readable S-expression format */
    private static final int LAST = 0;

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public abstract boolean equals(Object o);

    /**
     * @see java.lang.Object#hashCode()
     */
    public abstract int hashCode();

    /**
     * Creates an <code>SexpList</code> -representation from this SDSI-object.
     * 
     * @return an <code>SexpList</code> that represents this SDSI-object.
     */
    public abstract SexpList toSexp();

    /**
     * Returns the S-expression representation of <code>this</code> SPKI/SDSI object in readable
     * form, using the standard S-expression charactrer encoding (8859-1).
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toString(OFFSET, WIDTH, LAST);
    }

    /**
     * Returns the S-expression representation of <code>this</code> SPKI/SDSI object in readable
     * form, using the standard S-expression charactrer encoding (8859-1).
     * 
     * @param offset spaces indented from left.
     * @param width total width of window, in characters.
     * @param last spaces reserved on right (e.g., for closing parens)
     * @return the S-expression
     */
    public String toString(int offset, int width, int last) {
        try {
            StringWriter sw = new StringWriter();
            this.toSexp().writeReadable(sw, offset, width, last);
            return sw.toString();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /**
     * Returns the S-expression representation of <code>this</code> SPKI/SDSI object in transport
     * form. If the transport form is required as a <code>java.lang.String</code>, then simply
     * pass the resulting <code>byte[]</code> from this method into
     * {@link jsdsi.sexp.Sexp#decodeString(byte[])} (This process is required as S-expression's must use
     * the 8859-1 charactrer encoding).
     * 
     * @return the S-expression
     */
    public byte[] toTransport() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.toSexp().writeTransport(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    /**
     * Returns an array of bytes with the canonical representation of this SDSI/SPKI object.
     * 
     * @return an array of bytes with the canonical representation of this SDSI/SPKI object.
     */
    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            this.toSexp().writeCanonical(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Writes <code>this</code> SPKI/SDSI object to the given <code>java.io.ObjectOutputStream</code>.
     * @see java.io.Serializable
     * @param out the output stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        ObjOutputStream objOut = new ObjOutputStream(out);
        objOut.writeCanonical(this);
    }

    /**
     * The replacement SPKI/SDSI object read from the <code>java.io.ObjectInputStream</code> during
     * {@link #readObject(java.io.ObjectInputStream)} and to be returned via {@link #readResolve()}.
     */
    protected transient Obj _obj;

    /**
     * Reads <code>this</code> SPKI/SDSI object from the given <code>java.io.ObjectInputStream</code>.
     * @see java.io.Serializable
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjInputStream objIn = new ObjInputStream(in);
        try
        {
            _obj = objIn.readObj();
        } catch (Exception e)
        {
           e.printStackTrace();
           throw new IOException(e.toString());
        }
    }

    /**
     * Returns the SPKI/SDSI object which is the replacement after serialization.
     * @see java.io.Serializable
     * @return
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        if (_obj!=null) {
            return _obj;
        } else {
            return this;
        }
    }
    
    /**
     * Parses an S-expression an returns a SDSI object.
     * 
     * @param s the S-expression to parse.
     * @return a SDSI object stored in <code>s</code>-
     * @throws SexpParseException
     */
    public static Obj parseObj(Sexp s) throws SexpParseException {
        return parseObj(SexpUtil.getList(s));
    }

    /**
     * Parses an <code>SexpList</code> an returns a SDSI object.
     * 
     * @param l the <code>SexpList</code> that stores the SDSI object.
     * @return a SDSI object stored in <code>l</code>.
     * @throws SexpParseException
     */
    public static Obj parseObj(SexpList l) throws SexpParseException {
        String type = l.getType();
        if (type.equals("acl")) return Acl.parseAcl(l);
        if (type.equals("entry")) return AclEntry.parseAclEntry(l);
        if (type.equals("cert")) return Cert.parseCert(l);
        if (type.equals("sequence")) return Sequence.parseSequence(l);
        if (type.equals("public-key")) return PublicKey.parsePublicKey(l);
        if (type.equals("do")) return Op.parseOp(l);
        if (type.equals("signature")) return Signature.parseSignature(l);
        if (type.equals("hash")) return Hash.parseHash(l);
        if (type.equals("object-hash")) return ObjectHash.parseObjectHash(l);
        if (type.equals("tag")) return Tag.parseTag(l);
        if (type.equals("k-of-n")) return Threshold.parseThreshold(l);
        if (type.equals("name")) return Name.parseName(l);
        if (type.equals("principal")) return Principal.parsePrincipal(l);
        if (type.equals("proof")) return Proof.parseProof(l);
        if (type.equals("online")) return OnlineTest.parseOnlineTest(l);
        if (type.equals("valid")) return Validity.parseValidity(l);

        throw new SexpParseException("unrecognized object type: " + type);
    }
    
}
