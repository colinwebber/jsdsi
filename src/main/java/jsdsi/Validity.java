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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A validity period and a set of online tests.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/11/08 12:08:08 $
 * 
 * @todo Change tests from OnlineTest[] to java.util.Set
 */
public class Validity extends Obj {

  private static final long serialVersionUID = 6865522911895159585L;
  
	/**
	 * Valid not before ... (may be <code>null</code>).
	 */
	private transient final Date notBefore;
	
	/**
	 * Valid not after ... (may be <code>null</code>).
	 */
	private transient final Date notAfter;

	/**
	 * Array of online tests (my be <code>null</code>).
	 */
	private transient final OnlineTest[] tests;

	/**
	 * Creates a new <code>Validity</code> from two given date bounds 
	 * and an array of online tests.
	 * 
	 * @param  b not-before-bound.
	 * @param  a not-after-bound.
	 * @param  t array of onlie tests (may be <code>null</code>).
	 */
	public Validity(Date b, Date a, OnlineTest[] t) {
		if( t==null ) {
			t = new OnlineTest[0];
		}
		notBefore = b;
		notAfter = a;
		tests = t;
	}

	/**
	 * Creates a new <code>Validity</code> from two given date bounds.
	 * 
	 * @param  b not-before-bound.
	 * @param  a not-after-bound.
	 */
	public Validity(Date b, Date a) {
		this(b, a, new OnlineTest[0]);
	}

	/**
	 * Checks if this <code>Validity</code> is valid now.
	 * 
	 * @return <code>true</code> if this <code>Validity</code> is valid at 
	 *         this juncture, <code>false</code> otherwise.
	 */
	public boolean valid() {
		// FIXME: needs to run online tests (parameters?)
		Date now = new Date();
		return (notBefore == null || notBefore.before(now))
			&& (notAfter == null || notAfter.after(now));
	}

	/**
	 * Returns the not-before date.
	 * 
	 * @return the not-before date.
	 */
	public Date getNotBefore() {
		return notBefore;
	}

	/**
	 * Returns the not-after date.
	 * 
	 * @return the not-after date.
	 */
	public Date getNotAfter() {
		return notAfter;
	}

	/**
	 * Returns an array of online tests.
	 * 
	 * @return an array of online tests.
	 */
	public OnlineTest[] getOnlineTests() {
		return tests;
	}

	/**
	 * Intersects this <code>Validity</code> with another and returns the 
	 * resulting <code>Validity</code>.
	 * 
	 * @param  v <code>Validity</code> to intersect this <code>Validity</code>
	 *         with.
	 * @return the intersection of this and <code>v</code>.
	 */
	public Validity intersect(Validity v) {
		Date nb = notBefore;
		if ((nb == null) || (v.notBefore != null && v.notBefore.after(nb))) {
			nb = v.notBefore;
		}
		Date na = notAfter;
		if ((na == null) || (v.notAfter != null && v.notAfter.before(na))) {
			na = v.notAfter;
		}
		// combine lists of tests
		OnlineTest[] ts = new OnlineTest[tests.length + v.tests.length];
		System.arraycopy(tests, 0, ts, 0, tests.length);
		System.arraycopy(v.tests, 0, ts, tests.length, v.tests.length);
		return new Validity(nb, na, ts);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Validity) {
			Validity v = (Validity) o;
			return Util.equals(notBefore, v.notBefore)
				&& Util.equals(notAfter, v.notAfter)
				&& Util.equals(tests, v.tests);
		}
		return false;
	}

    /**
     * @return true iff a Validity with not-before d1 is valid whenever
     * a validity with not-before d2 is valid (all else equal).
     */
    private static boolean impliesNB(Date d1, Date d2) {
        if (d1 == null) {
            return true;  // null not-before is always valid
        }
        if (d2 == null) {
            return false; // d1 is non-null, so it's weaker
        }
        return d1.equals(d2) || d1.before(d2);
    }
    
    /**
     * @return true iff a Validity with not-after d1 is valid whenever
     * a Validity with not-after d2 is valid (all else equal).
     */
    private static boolean impliesNA(Date d1, Date d2) {
        if (d1 == null) {
            return true;  // null not-after is always valid
        }
        if (d2 == null) {
            return false; // d1 is non-null, so it's weaker
        }
        return d1.equals(d2) || d1.after(d2);
    }
    
    /**
     * @return true iff a Validity with tests t1 is valid whenever
     * a Validity with tests t2 is valid (all else equal).
     */
    private static boolean impliesTests(OnlineTest[] t1, OnlineTest[] t2) {
        if (t1 == null) {
            return true;  // null tests is always valid
        }
        if (t2 == null) {
            return false; // t1 is non-null, so it's weaker
        }
        return Arrays.asList(t1).containsAll(Arrays.asList(t2));
    }
    
    /**
     * @return true iff v1 is valid whenever v2 is valid
     */
    public static boolean implies(Validity v1, Validity v2) {
        if (v1 == null) {
            return true;  // null validity is always valid
        }
        if (v2 == null) {
            return false; // v1 is non-null, so it's weaker
        }
        return impliesNB(v1.notBefore, v2.notBefore)
            && impliesNA(v1.notAfter, v2.notAfter)
            && impliesTests(v1.tests, v2.tests);
    }

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(notBefore)
			^ Util.hashCode(notAfter)
			^ Util.hashCode(tests);
	}

	public SexpList toSexp() {
		List l = new ArrayList(2);
		if (getNotBefore() != null) {
			Sexp[] ss = new Sexp[1];
			ss[0] = SexpUtil.toSexp(getNotBefore());
			l.add(SexpUtil.toSexp("not-before", ss));
		}
		if (getNotAfter() != null) {
			Sexp[] ss = new Sexp[1];
			ss[0] = SexpUtil.toSexp(getNotAfter());
			l.add(SexpUtil.toSexp("not-after", ss));
		}
		for (int i = 0; i < tests.length; i++) {
			l.add(tests[i].toSexp());
		}
		return SexpUtil.toSexp("valid", l);
	}

	static Validity parseValidity(SexpList l) throws SexpParseException {
		Iterator vbody = SexpUtil.getBody(l);
		SexpList check = SexpUtil.getNextList(vbody, "validity check");
		String type = check.getType();
		Date notBefore = null;
		Date notAfter = null;
		int numOnlineTests = l.size() - 1;
		if (type.equals("not-before")) {
			numOnlineTests--;
			Iterator nb = SexpUtil.getBody(check);
			notBefore =
				SexpUtil.parseDate(
					SexpUtil.getNextString(nb, "not-before date"));
			SexpUtil.checkDone(nb, "not-before");
			if (vbody.hasNext()) {
				check = SexpUtil.getNextList(vbody, "validity check");
				type = check.getType();
			}
		}
		if (type.equals("not-after")) {
			numOnlineTests--;
			Iterator na = SexpUtil.getBody(check);
			notAfter =
				SexpUtil.parseDate(
					SexpUtil.getNextString(na, "not-after date"));
			SexpUtil.checkDone(na, "not-after");
			if (vbody.hasNext()) {
				check = SexpUtil.getNextList(vbody, "validity check");
				type = check.getType();
			}
		}
		OnlineTest[] tests = new OnlineTest[numOnlineTests];
		for (int i = 0; i < tests.length; i++) {
			tests[i] =
				OnlineTest.parseOnlineTest(
					SexpUtil.getNextList(
						vbody,
						"online test " + (i + 1) + "/" + tests.length));
		}
		SexpUtil.checkDone(vbody, "valid");
		return new Validity(notBefore, notAfter, tests);
	}
}
