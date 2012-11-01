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
 * Specifies a particular authorization permission.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class Tag extends Obj {

  private static final long serialVersionUID = -2237651253804555846L;
  
	/**
	 * A <code>Tag</code> that conveys all permissions.
	 */
	public static Tag ALL_TAG = new Tag() {
        public Tag intersect(Tag that) {
            return that;
        }

		public boolean equals(Object that) {
			return this == that;
		}

		public int hashCode() {
			return 1;
		}

		public Sexp toTagSexp() {
			return SexpUtil.toSexpList("*");
		}
	};

	/**
	 * A Tag that conveys no permissions.
	 */
	public static Tag NULL_TAG = new Tag() {
            public Tag intersect(Tag that) {
                return this;
            }

            public boolean equals(Object that) {
                return this == that;
            }
            
            public int hashCode() {
                return 0;
            }
            
            public Sexp toTagSexp() {
                throw new Error("Cannot convert NULL_TAG to Sexp");
            }
            
            public String toString()
            {
                return "Tag.NULL_TAG";
            }
	};

	/**
	 * Intersects this <code>Tag</code> with another one and returns the 
	 * result.
	 * 
	 * @param  that tag to intersect this with.
	 * @return the intersection of this <code>Tag</code> and <code>that</code>.
	 */
    public abstract Tag intersect(Tag that);

	/**
	 * Checks if this <code>Tag</code> implies the given tag.
	 * Implemented as <code>return this.intersect(that).equals(that)</code>.
	 * 
	 * @param  that tag to check for if it is implied by this one.
	 * @return <code>true</code> if this tag implies <code>that</code>, 
	 *         <code>false</code> otherwise.
	 */
	public final boolean implies(Tag that)
    {
        return this.intersect(that).equals(that);
    }

	abstract protected Sexp toTagSexp();

	public final SexpList toSexp() {
		Sexp[] ss = new Sexp[1];
		ss[0] = toTagSexp();
		return SexpUtil.toSexp("tag", ss);
	}

	static Tag parseTag(SexpList l) throws SexpParseException {
		SexpUtil.checkType(l, "tag");
		Iterator tbody = SexpUtil.getBody(l);
		Sexp s = SexpUtil.getNext(tbody, "tag body");
		if (s instanceof SexpList) {
			SexpList ll = (SexpList) s;
			if (ll.getType().equals("*") && (ll.size() == 1)) {
				return ALL_TAG;
			}
		}
		return ExprTag.parseExprTag(s);
	}
}
