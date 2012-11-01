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

import java.math.BigInteger;
import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A tag that specifies a range of allowed values.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 **/
public class RangeTag extends ExprTag {
    
    private static final long serialVersionUID = -4840725353015683369L;
    
	/**
	 * The ordering parameter: alpha, numeric, time, binary, date.
	 */
	private transient final RangeOrdering ordering;

	/**
	 * The upper limit of this range tag.
	 */
	private transient final String upperLimit;

	/**
	 * Is the upper limit strict?
	 */
	private transient final boolean strictUpper;

	/**
	 * The lower limit of this range tag.
	 */
	private transient final String lowerLimit;

	/**
	 * Is the lower limit strict?
	 */
	private transient final boolean strictLower;

	/**
	 * Creates a new <code>RangeTag</code> from a given lower and upper
	 * limit.
	 * 
	 * @param  o defines the ordering.
	 * @param  u upper limit.
	 * @param  su is the upper limit strict?
	 * @param  l lower limit.
	 * @param  sl is the lower limit strict?
	 */
	public RangeTag(String o, String u, boolean su, String l, boolean sl) {
		assert(o != null) : "null ordering";
		assert(u != null) : "null upper bound"; // FIXME: allow!
		assert(l != null) : "null lower bound"; // FIXME: allow!
		ordering = RangeOrdering.parse(o);
        assert(ordering.convert(l).compareTo(ordering.convert(u)) <= 0)
            : "lower bound must be less than or equal to upper bound";
		upperLimit = u;
		strictUpper = su;
		lowerLimit = l;
		strictLower = sl;
	}

    /**
	 * @see jsdsi.Tag#intersect(Tag)
     **/
    public Tag intersect(Tag that)
    {
        if (that instanceof RangeTag) {
            return intersect((RangeTag)that);
		}
        if (that instanceof StringTag) {
            return intersect((StringTag)that);
        }
        if (that instanceof SetTag) {
            return that.intersect(this);
        }
        return Tag.NULL_TAG;
    }

    public Tag intersect(StringTag that)
    {
        if (ordering.contains(this, that.getValue())) {
            return that;
        }
        return Tag.NULL_TAG;
    }
    
    public Tag intersect(RangeTag that)
    {
        if (this.ordering != that.ordering) {
            return Tag.NULL_TAG;
        }
        return ordering.intersect(this, that);
    }
    
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object that) {
		if (that instanceof RangeTag) {
			RangeTag r = (RangeTag) that;
			return this.ordering == r.ordering
				&& this.upperLimit.equals(r.upperLimit)
				&& this.strictUpper == r.strictUpper
				&& this.lowerLimit.equals(r.lowerLimit)
				&& this.strictLower == r.strictLower;
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return ordering.hashCode()
			^ upperLimit.hashCode()
			^ lowerLimit.hashCode()
			^ (strictUpper ? 1 : 0)
			^ (strictLower ? 1 : 0);
	}

	/**
	 * Returns the ordering of this <code>RangeTag</code> that is
	 * <code>"alpha" | "numeric" | "time" | "binary" | "date"</code>.
	 * 
	 * @return the ordering of this <code>RangeTag</code>.
	 */
	public String getOrdering() {
		return ordering.toString();
	}

	/**
	 * Returns the upper limit of this <code>RangeTag</code>.
	 * 
	 * @return the upper limit of this <code>RangeTag</code>.
	 */
	public String getUpperLimit() {
		return upperLimit;
	}

	/**
	 * Returns the lower limit of this <code>RangeTag</code>.
	 * 
	 * @return the lower limit of this <code>RangeTag</code>.
	 */
	public String getLowerLimit() {
		return lowerLimit;
	}

	/**
	 * Returns whether the upper limit of this <code>RangeTag</code> is strict.
	 * 
	 * @return <code>true</code> if the upper limit of this 
	 *         <code>RangeTag</code> is strict, <code>false</code> otherwise.
	 */
	public boolean upperIsStrict() {
		return strictUpper;
	}

	/**
	 * Returns whether the lower limit of this <code>RangeTag</code> is strict.
	 * 
	 * @return <code>true</code> if the lower limit of this 
	 *         <code>RangeTag</code> is strict, <code>false</code> otherwise.
	 */
	public boolean lowerIsStrict() {
		return strictLower;
	}

	public Sexp toTagSexp() {
		Sexp[] ss = new Sexp[6];
		ss[0] = SexpUtil.toSexp("range");
		ss[1] = SexpUtil.toSexp(getOrdering());
		ss[2] = SexpUtil.toSexp(lowerIsStrict() ? "g" : "ge");
		ss[3] = SexpUtil.toSexp(getLowerLimit());
		ss[4] = SexpUtil.toSexp(upperIsStrict() ? "l" : "le");
		ss[5] = SexpUtil.toSexp(getUpperLimit());
		return SexpUtil.toSexp("*", ss);
	}

	static RangeTag parseRangeTag(Iterator tbody) throws SexpParseException {
		String ordering = SexpUtil.getNextString(tbody, "range ordering");
		// lower strictness and limit
		String gte = SexpUtil.getNextString(tbody, "range gte");
		boolean strictLower = gte.equals("g");
		if (!strictLower && !gte.equals("ge"))
			throw new SexpParseException(
				"range lower strictness is not 'g' or 'ge': " + gte);
		String lowerLimit = SexpUtil.getNextString(tbody, "range lower limit");
		// upper strictness and limit
		String lte = SexpUtil.getNextString(tbody, "range lte");
		boolean strictUpper = lte.equals("l");
		if (!strictUpper && !lte.equals("le"))
			throw new SexpParseException(
				"range upper strictness is not 'l' or 'le': " + lte);
		String upperLimit = SexpUtil.getNextString(tbody, "range upper limit");
		SexpUtil.checkDone(tbody, "range tag");
		return new RangeTag(ordering,
                            upperLimit,
                            strictUpper,
                            lowerLimit,
                            strictLower);
	}
    private static abstract class RangeOrdering {
        public abstract Comparable convert(String s);
        public boolean contains(RangeTag r, String s)
        {
            assert(r.ordering == this);
            try {
                Comparable val = convert(s);
                int lc = convert(r.lowerLimit).compareTo(val);
                if (lc > 0 || (lc == 0 && r.strictLower)) {
                    return false;
                }
                int uc = convert(r.upperLimit).compareTo(val);
                if (uc < 0 || (uc == 0 && r.strictUpper)) {
                    return false;
                }
                return true;
            } catch (IllegalArgumentException e) {
                // convert() failed, so fail safely
                return false;
            }
        }
        public Tag intersect(RangeTag r, RangeTag that)
        {
            assert(r.ordering == this);
            assert(that.ordering == this);
            try {
                String  ll, ul;
                boolean sl, su;
                int lc = convert(r.lowerLimit).compareTo
                    (convert(that.lowerLimit));
                if (lc > 0 || (lc == 0 && r.strictLower)) {
                    ll = r.lowerLimit;
                    sl = r.strictLower;
                } else {
                    ll = that.lowerLimit;
                    sl = that.strictLower;
                }
                int uc = convert(r.upperLimit).compareTo
                    (convert(that.upperLimit));
                if (uc < 0 || (uc == 0 && r.strictUpper)) {
                    ul = r.upperLimit;
                    su = r.strictUpper;
                } else {
                    ul = that.upperLimit;
                    su = that.strictUpper;
                }
                if (convert(ll).compareTo(convert(ul)) > 0) {
                    // lower limit greater than upper limit
                    return Tag.NULL_TAG;
                }
                return new RangeTag(this.toString(), ul, su, ll, sl);
            } catch (IllegalArgumentException e) {
                // convert() failed, so fail safely
                return Tag.NULL_TAG;
            }
        }
        public static RangeOrdering ALPHA = new RangeOrdering() {
                public Comparable convert(String s)
                {
                    return s;
                }
                public String toString()
                {
                    return "alpha";
                }
            };
        public static RangeOrdering BINARY = new RangeOrdering() {
                public Comparable convert(String s)
                {
                    return new BigInteger(s.getBytes());
                }
                public String toString()
                {
                    return "binary";
                }
            };
        public static RangeOrdering NUMERIC = new RangeOrdering() {
                public Comparable convert(String s)
                {
                    try {
                        return new Float(Float.parseFloat(s));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("bad float:"+s);
                    }
                }
                public String toString()
                {
                    return "numeric";
                }
            };
        public static RangeOrdering TIME = new RangeOrdering() {
                public Comparable convert(String s)
                {
                    try {
                        return SexpUtil.parseDate(s);
                    } catch (SexpParseException e) {
                        throw new IllegalArgumentException("bad date: "+s);
                    }
                }
                public String toString()
                {
                    return "time";
                }
            };
        public static RangeOrdering DATE = new RangeOrdering() {
                public Comparable convert(String s)
                {
                    try {
                        return SexpUtil.parseDate(s);
                    } catch (SexpParseException e) {
                        throw new IllegalArgumentException("bad date: "+s);
                    }
                }
                public String toString()
                {
                    return "date";
                }
            };
        public static RangeOrdering parse(String name)
        {
            if (name.equals("alpha")) {
                return ALPHA;
            }
            if (name.equals("numeric")) {
                return NUMERIC;
            }
            if (name.equals("time")) {
                return TIME;
            }
            if (name.equals("binary")) {
                return BINARY;
            }
            if (name.equals("date")) {
                return DATE;
            }
            throw new IllegalArgumentException(name);
        }
    }
}
