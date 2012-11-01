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
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A tag that contains a string type and a sequence of other tags.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class SimpleTag extends ExprTag {

  private static final long serialVersionUID = -7996132866305015527L;
  
	/**
	 * Value of this <code>SimpleTag</code>.
	 */
	private transient final String value;

	/**
	 * Sequence of tags.
	 */
	private transient final ExprTag[] tags;


	/**
	 * Creates a new <code>SimpleTag</code> from a string and an array of tags.
	 * 
	 * @param  v type of this <code>SimpleTag</code>.
	 * @param  t sequence of tags.
	 */
	public SimpleTag(String v, ExprTag[] t) {
		assert(v != null) : "null value";
		assert(t != null) : "null tags";
		value = v;
		tags = t;
	}
    
    /**
     * If <code>that</code> is a SimpleTag, returns
     * <code>intersect((SimpleTag)that)</code>.  If <code>that</code> is
     * a SetTag, returns <code>that.intersect(this)</code>.  Otherwise
     * returns NULL_TAG.
     *
	 * @see jsdsi.Tag#intersect(Tag)
     **/
    public Tag intersect(Tag that)
    {
        if (that instanceof SimpleTag) {
            return intersect((SimpleTag)that);
        }
        if (that instanceof SetTag) {
            return that.intersect(this);
        }
        return NULL_TAG;
    }
    
	/**
	 * If <code>that</code> has the same value as <code>this</code>,
     * <code>intersect</code> returns a new SimpleTag as long as the
     * longer of the two tags whose elements are the intersection of the
     * corresponding elements.  Elements past the end of the shorter tag
     * are copied from the longer tag.  If any of the intersections
     * is not an ExprTag, or if <code>that</code> is not a SimpleTag,
     * or if that's value differs from <code>this</code>'s value,
     * <code>intersect</code> returns NULL_TAG.
	 */
    public Tag intersect(SimpleTag that)
    {    
        if (!this.value.equals(that.value)) {
            return Tag.NULL_TAG;
        }

        // the intersection will be as long as the longer tag
        ExprTag[] tagRes;
        if (this.tags.length > that.tags.length) {
            tagRes = new ExprTag[this.tags.length];
        } else {
            tagRes = new ExprTag[that.tags.length];
        }
        // each element is the intersection of this and that elements
        for (int i = 0; i < tagRes.length; i++) {
            if (i >= this.tags.length) {
                tagRes[i] = that.tags[i];
            } else if (i >= that.tags.length) {
                tagRes[i] = this.tags[i];
            } else {
                Tag tag = this.tags[i].intersect(that.tags[i]);
                if (!(tag instanceof ExprTag)) {
                    // this excludes NULL_TAG and ALL_TAG
                    return Tag.NULL_TAG;
                }
                tagRes[i] = (ExprTag)tag;
            }
        }
        return new SimpleTag(value, tagRes);
    }    

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object that) {
		return (that instanceof SimpleTag)
			&& this.value.equals(((SimpleTag) that).value)
			&& Util.equals(this.tags, ((SimpleTag) that).tags);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(value) ^ Util.hashCode(tags);
	}

	/**
	 * Returns the string value of this <code>SimpleTag</code>.
	 * 
	 * @return the string value of this <code>SimpleTag</code>.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the tags of this <code>SimpleTag</code>.
	 * 
	 * @return the tags of this <code>SimpleTag</code>.
	 */
	public ExprTag[] getTags() {
		return tags;
	}

	public Sexp toTagSexp() {
		Sexp[] ss = new Sexp[tags.length];
		for (int i = 0; i < tags.length; i++) {
			ss[i] = tags[i].toTagSexp();
		}
		return SexpUtil.toSexp(getValue(), ss);
	}

	static SimpleTag parseSimpleTag(SexpList l) throws SexpParseException {
		String value = l.getType();
		Iterator lbody = SexpUtil.getBody(l);
		ExprTag[] tags = new ExprTag[l.size() - 1];
		for (int i = 0; i < tags.length; i++) {
			tags[i] =
				ExprTag.parseExprTag(SexpUtil.getNext(lbody, "simple tag"));
		}
		SexpUtil.checkDone(lbody, "simple tag");
		return new SimpleTag(value, tags);
	}
}
