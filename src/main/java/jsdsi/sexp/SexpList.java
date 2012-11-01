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
package jsdsi.sexp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A parenthesized list of S-expressions, where the first element is
 * always a SexpString and is called the list's "type".
 * 
 * @see Sexp
 * @see SexpString
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/08/25 23:18:11 $
 */
public class SexpList extends Sexp {
    
    private static final long serialVersionUID = 232350493156018658L;
    
	private List elements;

	// implements serializable
	SexpList() {
	}

	// implements serializable
	private void writeObject(ObjectOutputStream out) throws IOException {
		writeCanonical(out);
	}

	// implements serializable
	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException {
		try {
			SexpList l = (SexpList) (new SexpInputStream(in)).readSexp();
			this.elements = l.elements;
		} catch (SexpException e) {
			throw (IOException) new IOException().initCause(e);
		} catch (ClassCastException e) {
			throw (IOException) new IOException().initCause(e);
		}
	}

	/**
	 * Creates a new list from a type string and an array of
	 * S-expressions.
	 * 
	 * @param  type the list type string.
	 * @param  expressions the elements of the list.
	 */
	public SexpList(SexpString type, Sexp[] expressions) {
		elements = new ArrayList(expressions.length + 1);
		elements.add(type);
		for (int i = 0; i < expressions.length; i++) {
			elements.add(expressions[i]);
		}
	}

	/**
	 * Returns the type of this list.
	 * 
	 * @return the type of this list.
	 */
	public String getType() {
		return ((SexpString) elements.get(0)).toString();
	}

	/**
	 * Returns the size of this list, including its type.
	 * 
	 * @return the size of this list, including its type.
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * Returns an iterator over this list, including its type.
	 * Each element is an Sexp.
	 * 
	 * @return an iterator over this list, including its type.
	 */
	public Iterator iterator() {
		return elements.iterator();
	}

	/**
	 * Returns <code>true</code> if <code>this</code> list is empty
	 * (i.e., only has a type), <code>false</code> otherwise.
	 * 
	 * @return <code>true</code> if <code>this</code> list is empty
	 * (i.e., only has a type), <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		return elements.size() == 1;
	}

	public void writeCanonical(OutputStream out) throws IOException {
		out.write('(');
		Iterator i = iterator();
		while (i.hasNext()) {
			((Sexp) i.next()).writeCanonical(out);
		}
		out.write(')');
	}

	public void writeReadable(Writer out, int offset, int width, int last)
		throws IOException {
		out.write('(');
		boolean newLines = false;
		if (getReadableLen() > width - offset - last) {
			newLines = true;
			offset += 2;
			if (offset >= width)
				offset = 0;
		}
		Iterator i = iterator();
		boolean doneFirst = false;
		while (i.hasNext()) {
			Sexp s = (Sexp) i.next();
			if (doneFirst) {
				if (newLines) {
					out.write('\n');
					for (int j = 0; j < offset; j++) {
						out.write(' ');
					}
				} else {
					out.write(' ');
				}
			} else {
				doneFirst = true;
			}
			if (i.hasNext()) {
				s.writeReadable(out, offset, width, 0);
			} else {
				s.writeReadable(out, offset, width, last + 1);
			}
		}
		out.write(')');
	}

	int getReadableLenImpl() {
		int len = 1; // the opening paren
		for (Iterator i = iterator(); i.hasNext();) {
			len += ((Sexp) i.next()).getReadableLen();
			len++; // a space or the closing paren
		}
		return len;
	}
}