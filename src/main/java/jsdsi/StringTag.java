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

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpUtil;

/**
 * A tag defined by a simple string.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/08 12:08:08 $
 */
public class StringTag extends ExprTag {

  private static final long serialVersionUID = -2461811044266013845L;
  
	/**
	 * The string value of this <code>StringTag</code>.
	 */
	private transient final String value;

	/**
	 * Creates a new <code>StringTag</code> for a given string value.
	 * 
	 * @param  v string to create the <code>StringTag</code>.
	 */
	public StringTag(String v) {
		assert(v != null) : "null value";
		value = v;
	}

    /**
	 * @see jsdsi.Tag#intersect(Tag)
     */
    public Tag intersect(Tag that)
    {
        if (that instanceof StringTag) {
            return this.intersect((StringTag)that);
        }
        if (that instanceof PrefixTag) {
            return that.intersect(this);
        }
        if (that instanceof ReversePrefixTag) {
        	return that.intersect(this);
        }
        if (that instanceof RangeTag) {
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
    public Tag intersect(StringTag that)
    {
        if (this.equals(that)) {
            return this;
        }
        return Tag.NULL_TAG;
    }
    
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object that) {
		return (that instanceof StringTag)
			&& this.value.equals(((StringTag)that).value);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Returns the string value of this <code>StringTag</code>.
	 * 
	 * @return the string value of this <code>StringTag</code>.
	 */
	public String getValue() {
		return value;
	}

	public Sexp toTagSexp() {
		return SexpUtil.toSexp(getValue());
	}
}
