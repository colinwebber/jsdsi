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

import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A tag that matches all strings with a given prefix.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/08 12:08:08 $
 */
public class PrefixTag extends ExprTag {
    
    private static final long serialVersionUID = 4409242379275402071L;
    
	/**
	 * The Prefix.
	 */
	private transient final String prefix;

	/**
	 * Creates a new <code>PrefixTag</code> wit ha given prefix string.
	 * 
	 * @param  p the prefix string.
	 */
	public PrefixTag(String p) {
		assert(p != null) : "null prefix";
		prefix = p;
	}

	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(Tag that)
    {
        if (that instanceof PrefixTag) {
            return intersect((PrefixTag)that);
        }
        if (that instanceof StringTag) {
            return intersect((StringTag)that);
        }
        if (that instanceof ReversePrefixTag) {
        	return that.intersect(this);
        }
        if (that instanceof SetTag) {
            return that.intersect(this);
        }
        return Tag.NULL_TAG;
    }
    
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(PrefixTag that)
    {
        if (this.prefix.startsWith(that.prefix)) {
            return this;
        }
        if (that.prefix.startsWith(this.prefix)) {
            return that;
        }
        return Tag.NULL_TAG;
    }
    
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(StringTag that)
    {
        if (that.getValue().startsWith(this.prefix)) {
            return that;
        }
        return Tag.NULL_TAG;
    }
    
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object that) {
		return (that instanceof PrefixTag)
			&& this.prefix.equals(((PrefixTag) that).prefix);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return prefix.hashCode();
	}

	/**
	 * Returns the prefix of this tag.
	 * 
	 * @return the prefix of this tag.
	 */
	public String getPrefix() {
		return prefix;
	}

	public Sexp toTagSexp() {
		Sexp[] ss = new Sexp[2];
		ss[0] = SexpUtil.toSexp("prefix");
		ss[1] = SexpUtil.toSexp(getPrefix());
		return SexpUtil.toSexp("*", ss);
	}

	static PrefixTag parsePrefixTag(Iterator tbody) throws SexpParseException {
		String p = SexpUtil.getNextString(tbody, "prefix tag");
		SexpUtil.checkDone(tbody, "prefix tag");
		return new PrefixTag(p);
	}
}
