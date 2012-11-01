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
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A SDSI name: a principal and a sequence of strings.  If the sequence
 * contains just one string, this is a local name; otherwise this is an
 * extended name.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class Name extends Obj implements Subject {
    
    private static final long serialVersionUID = 3430696653946782484L;
    
	/**
	 * Principal of this Name.
	 */
	private transient final Principal issuer;
	
	/**
	 * The name string(s) for this name (more than one string for an extended
	 * name).
	 */
	private transient final String[] names;

	/**
	 * Creates a new <code>Name</code> for a given principal and an array
	 * of name-strings.
	 * 
	 * @param  i principal to create the <code>Name</code> for.
	 * @param  n array of string-names for this <code>Name</code> (if the 
	 *         length of <code>n</code> is greater than 1, an extended name
	 *         will be created).
	 */
	public Name(Principal i, String[] n) {
		assert(i != null) : "null issuer";
		assert(n != null) : "null names";
		assert(n.length > 0) : "empty names";
		issuer = i;
		names = n;
	}

	/**
	 * Creates a new local name from a given principal and name-string.
	 * 
	 * @param  i principal to create the <code>Name</code> for.
	 * @param  n name-string to create the <code>Name</code> for.
	 */
	public Name(Principal i, String n) {
		this(i, new String[] { n }); // XXX assert n != null
	}

	/**
	 * Returns the principal of the <code>Name</code>.
	 * 
	 * @return the principal of this <code>Name</code>.
	 */
	public Principal getIssuer() {
		return issuer;
	}

	/**
	 * Returns an array of name-strings of this <code>Name</code> (the array
	 * has a length of one in the case of a local name, and greater then 1 for
	 * extended names).
	 * 
	 * @return an array of strings containing the name-string(s) of this name
	 *         (more than one string for an extended string).
	 */
	public String[] getNames() {
		return names;
	}

	/**
	 * Checks if a given <code>Name</code> has the same issuer (principal) as
	 * this <code>Name</code> and if the name-strings are equal to this name's
	 * name-strings (a smaller number of name-strings are okay).
	 * 
	 * @param  n <code>Name</code> to compare with this <code>Name</code>.
	 * @return <code>false</code> if <code>n</code> has another issuer, a 
	 *         greater number of name-strings or if one name-string is not
	 *         equal if a name-string from this <code>Name</code>, returns
	 *         <code>true</code> otherwise.
	 */
	public boolean prefixOf(Name n) {
		if (!issuer.equals(n.issuer)) {
			return false;
		}
		if (names.length > n.names.length) {
			return false;
		}
		for (int i = 0; i < names.length; i++) {
			if (!names[i].equals(n.names[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates a new <code>Name</code> from this <code>Name</code> using only
	 * the first <code>i</code> name-strings.
	 * 
	 * @param  i number of name-strings of this <code>Name</code> to use for
	 *         creating a new <code>Name</code>.
	 * @return the new <code>Name</code> for the same principal as this 
	 *         <code>Name</code> using only the first <code>i</code> 
	 *         name-strings.
	 */
	public Name prefix(int i) {
		assert(i <= names.length) : "prefix too long";
		String[] ns = new String[i];
		System.arraycopy(names, 0, ns, 0, i);
		return new Name(issuer, ns);
	}

	/**
	 * Returns the local name of this name, that is a <code>Name</code> for 
	 * this <code>Name</code>s issuer with the first name-string of the list
	 * of names.
	 * 
	 * @return the new local name for this <code>Name</code>.
	 */
	public Name prefix() {
		return prefix(1);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Name) {
			Name n = (Name) o;
			return issuer.equals(n.issuer) && Util.equals(names, n.names);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return issuer.hashCode() ^ Util.hashCode(names);
	}

	public SexpList toSexp() {
		// TODO: implement this method
		return toSexp((Principal) null);
	}

	/**
	 * Returns an <code>SexpList</code>-representation of the given
	 * <code>Principal</code>.
	 * 
	 * @param iss the <code>Principal</code> to create an
	 * 	<code>SexpList</code> from.
	 * @return an <code>SexpList</code>-representation of <code>iss</code>.
	 */
	public SexpList toSexp(Principal iss) {
		List l = new ArrayList();
		if (!getIssuer().samePrincipalAs(iss)) {
			// make fully-qualified name
			l.add(getIssuer().toSexp());
		}
		for (int i = 0; i < names.length; i++) {
			l.add(SexpUtil.toSexp(names[i]));
		}
		return SexpUtil.toSexp("name", l);
	}

	/**
	 * Parses an <code>SexpList</code> to create a <code>Name</code>
	 * from it.
	 * 
	 * @param l the <code>SexpList</code> that holds a <code>Name</code>.
	 * @return a <code>Name</code> created from the values in <code>l</code>
	 * @throws SexpParseException
	 */
	static Name parseName(SexpList l) throws SexpParseException {
		return parseName(l, null);
	}

	/**
	 * Parses a name from a given <code>SexpList</code> and
	 * <code>Principal</code>.
	 * 
	 * @param l the <code>SexpList</code> to parse.
	 * @param issuerParam the <code>Principal</code> that is the issuer
	 * 	of the <code>Name</code>.
	 * @return the <code>Name</code> created from <code>l</code> and
	 * 	<code>issuerParm</code>. 
	 * @throws SexpParseException
	 */
	static Name parseName(SexpList l, Principal issuerParam)
		throws SexpParseException {
		Iterator nbody = SexpUtil.getBody(l);
		Sexp s = SexpUtil.getNext(nbody, "first name component");
		Principal issuer = null;
		String[] names = null;
		if (s instanceof SexpList) {
			// name is fully-qualified
			issuer = Principal.parsePrincipal(SexpUtil.getList(s));
			names = new String[l.size() - 2];
			for (int i = 0; i < names.length; i++) {
				names[i] = SexpUtil.getNextString(nbody, "name string #" + i);
			}
		} else if (issuerParam != null) {
			// name is relative; issuer provided as a parameter
			issuer = issuerParam;
			names = new String[l.size() - 1];
			names[0] = SexpUtil.getString(s);
			for (int i = 1; i < names.length; i++) {
				names[i] = SexpUtil.getNextString(nbody, "name string #" + i);
			}
		} else {
			throw new SexpParseException("relative name used without issuer");
		}
		SexpUtil.checkDone(nbody, "name"); // sanity check
		return new Name(issuer, names);
	}

	// TODO: boolean sameNameAs(Name n)
	// TODO: boolean sameNameAs(Principal i, String n)
}