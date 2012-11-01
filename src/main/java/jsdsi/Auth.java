/*
 * Copyright 2002 Massachusetts Institute of Technology Permission to use, copy, modify, and
 * distribute this program for any purpose and without fee is hereby granted, provided that this
 * copyright and permission notice appear on all copies and supporting documentation, the name of
 * M.I.T. not be used in advertising or publicity pertaining to distribution of the program without
 * specific prior permission, and notice be given in supporting documentation that copying and
 * distribution is by permission of M.I.T. M.I.T. makes no representations about the suitability of
 * this software for any purpose. It is provided "as is" without express or implied warranty.
 */
package jsdsi;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * A SPKI authorization: a tag and a propagate flag.
 * 
 * @see AuthCert
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/12/15 19:18:11 $
 */
public class Auth implements Serializable {
    static final Auth NULL_AUTH = new Auth(Tag.NULL_TAG, false);

    /**
     * The tag that holds the authorization.
     */
    private final Tag tag;

    /**
     * The propagate/delegation bit.
     */
    private final boolean propagate;

    /**
     * Creates a new <code>Auth</code> for a given <code>Tag</code> and propagation bit.
     * 
     * @param t tag
     * @param p propagation bit
     */
    public Auth(Tag t, boolean p) {
        assert (t != null) : "null tag";
        tag = t;
        propagate = p;
    }

    /**
     * @return this.tag
     */
    public Tag getTag() {
        return tag;
    }

    /**
     * @return this.propagate
     */
    public boolean getPropagate() {
        return propagate;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object o) {
        if (o instanceof Auth) {
            Auth a = (Auth) o;
            return tag.equals(a.tag) && propagate == a.propagate;
        }
        return false;
    }

    /**
     * @return true iff this is valid whenever a is valid
     */
    public boolean implies(Auth a) {
        return (propagate || !a.propagate) && tag.implies(a.tag);
        // The above says (this => a) when (a.prop => this.prop):
        // if a has propagate, then this must also propagate
        // if a doesn't propagate, then this can propagate or not
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return tag.hashCode() ^ (propagate ? 1 : 0);
    }

    /**
     * Writes <code>this</code> Auth to the given <code>java.io.ObjectOutputStream</code>.
     * 
     * @see java.io.Serializable
     * @param out the output stream
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        if (this.tag.equals(Tag.NULL_TAG)) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.tag);
        }
        out.writeBoolean(this.propagate);
    }

    /**
     * Reads an Auth from the given <code>java.io.ObjectInputStream</code>.
     * @see java.io.Serializable
     * @param in the input stream
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        Tag tag = null;
        if (in.readBoolean()) {
            tag = (Tag) in.readObject();
        } else {
            tag = Tag.NULL_TAG;
        }
        _replacement = new Auth(tag, in.readBoolean());
    }

    protected transient Auth _replacement;

    /**
     * Returns the Auth object which is the replacement after serialization.
     * @see java.io.Serializable
     * @return the replacement
     * @throws ObjectStreamException
     */
    protected Object readResolve() throws ObjectStreamException {
        if (_replacement != null) {
            return _replacement;
        } else {
            return this;
        }
    }
    
}