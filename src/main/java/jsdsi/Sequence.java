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
 * A sequence of SPKI/SDSI objects, typically used to present certs and
 * validators that prove a particular statement.
 *
 * @see Proof
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Sequence extends Obj {
    
    private static final long serialVersionUID = 7379198170031908180L;
    
	/**
	 * Elements of this <code>Sequence</code>.
	 */
	private transient final Element[] elements;

	/**
	 * Creates a new <code>Sequence</code> from a given array of elements.
	 * 
	 * @param  e array of elements to create the <code>Sequence</code> from.
	 */
	public Sequence(Element[] e) {
		assert(e != null) : "null elements";
		elements = e;
	}

	/**
	 * Returns the elements of this <code>Sequence</code>.
	 * 
	 * @return the elements of this <code>Sequence</code>.
	 */
	public Element[] getElements() {
		return elements;
	}

	/**
	 * Concatenates this <code>Sequence</code> with a given one.
	 * 
	 * @param  s sequence to concat this sequence with.
	 * @return the sequence containing the elements of this sequence
	 *         and the elements of <code>s</code>.
	 */
	public Sequence concat(Sequence s) {
		Element[] els = new Element[elements.length + s.elements.length];
		System.arraycopy(elements, 0, els, 0, elements.length);
		System.arraycopy(
			s.elements,
			0,
			els,
			elements.length,
			s.elements.length);
		return new Sequence(els);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof Sequence)
			&& Util.equals(elements, ((Sequence) o).elements);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Util.hashCode(elements);
	}

	public SexpList toSexp() {
		Sexp[] ss = new Sexp[elements.length];
		for (int i = 0; i < elements.length; i++) {
			ss[i] = elements[i].toSexp();
		}
		return SexpUtil.toSexp("sequence", ss);
	}

	static Sequence parseSequence(SexpList l) throws SexpParseException {
		Iterator sbody = SexpUtil.getBody(l);
		Element[] elems = new Element[l.size() - 1];
		for (int i = 0; i < elems.length; i++) {
			elems[i] =
				Element.Default.parseElement(
					SexpUtil.getNextList(sbody, "sequence element"));
		}
		SexpUtil.checkDone(sbody, "sequence"); // sanity check
		return new Sequence(elems);
	}
}