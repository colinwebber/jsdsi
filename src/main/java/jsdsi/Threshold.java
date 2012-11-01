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
 * A subject that can delegate authority to any K of N specified
 * subjects.  For example, if K is 2, N is 3, and the subjects are
 * Alice, Bob, and Carol, any two of those subjects can pass on any
 * authority granted to this subject (if delegation is permitted).
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Threshold extends Obj implements Subject {

  private static final long serialVersionUID = 3335226567408806884L;

	/**
	 * The subjects involved in this <code>Threshold</code>.
	 */
	private transient final Subject[] subjects; // TODO: replace with Set (unordered)

	/**
	 * The threshold number of subjects required for this authorization
	 * (at most subject.length, of course).
	 */
	private transient final int threshold; // at most subjects.length

	/**
	 * Creates a new <code>Threshold</code> from an array of subjects and a
	 * threshold.
	 * 
	 * @param  s array of subjects.
	 * @param  t threshold number (at most subjects.length).
	 */
	public Threshold(Subject[] s, int t) {
		assert(s != null) : "null subject array";
		assert(t <= s.length) : "threshold > num subjects";
		subjects = s;
		threshold = t;
	}

	/**
	 * Returns the threshold number of this <code>Threshold</code>.
	 * 
	 * @return the threshold number of this <code>Threshold</code>.
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * Returns the array of subjects of this <code>Threshold</code>.
	 * 
	 * @return the array of subjects of this <code>Threshold</code>.
	 */
	public Subject[] getSubjects() {
		return subjects;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Threshold) {
			Threshold t = (Threshold) o;
			return (threshold == t.threshold)
				&& Util.equals(subjects, t.subjects);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(subjects) ^ threshold;
	}

	public SexpList toSexp() {
		return toSexp((Principal) null);
	}

	public SexpList toSexp(Principal iss) {
		Sexp[] ss = new Sexp[subjects.length + 2];
		ss[0] = SexpUtil.toSexp(String.valueOf(getThreshold()));
		ss[1] = SexpUtil.toSexp(String.valueOf(subjects.length));
		for (int i = 0; i < subjects.length; i++) {
			// FIXME: can we eliminate these downcasts?
			if (subjects[i] instanceof Name) {
				// tell name about issuer
				ss[i + 2] = ((Name) subjects[i]).toSexp(iss);
			} else if (subjects[i] instanceof Threshold) {
				// tell nested names about issuer
				ss[i + 2] = ((Threshold) subjects[i]).toSexp(iss);
			} else {
				ss[i + 2] = subjects[i].toSexp();
			}
		}
		return SexpUtil.toSexp("k-of-n", ss);
	}

	static Threshold parseThreshold(SexpList l) throws SexpParseException {
		return parseThreshold(l, null);
	}

	static Threshold parseThreshold(SexpList l, Principal issuer)
		throws SexpParseException {
		Iterator tbody = SexpUtil.getBody(l);
		try {
			int k = Integer.parseInt(SexpUtil.getNextString(tbody, "k"));
			int n = Integer.parseInt(SexpUtil.getNextString(tbody, "n"));
			Subject[] subjects = new Subject[n];
			for (int i = 0; i < subjects.length; i++) {
				subjects[i] =
					Subject.Default.parseSubject(
						SexpUtil.getNextList(tbody, "threshold subject #" + i),
						issuer);
			}
			SexpUtil.checkDone(tbody, "threshold");
			return new Threshold(subjects, k);
		} catch (NumberFormatException e) {
			throw new SexpParseException("badly formatted number in threshold");
		}
	}
}