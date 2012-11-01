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

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * Verifies whether a certificate is valid by contacting an online
 * principal or its agent.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class OnlineTest extends Obj {
    
    private static final long serialVersionUID = -2569498341322684308L;
    
	/**
	 * Principal of this online test.
	 */
	private transient final Principal principal;

	/**
	 * URIs of this online test's principal (may be <code>null</code>).
	 */
	private transient final URI[] uris; // may be null

	/**
	 * Creates a new <code>OnlineTest</code> from a given principal and
	 * array of URIs.
	 * 
	 * @param  p principal for the new online test.
	 * @param  u array of URIs for the new online test.
	 */
	public OnlineTest(Principal p, URI[] u) {
		assert(p != null) : "null principal";
		principal = p;
		uris = u;
	}

	/**
	 * Returns the principal of this online test.
	 * 
	 * @return the principal of this online test.
	 */
	public Principal getPrincipal() {
		return principal;
	}

	/**
	 * Returns the URIs from this online test.
	 * 
	 * @return an array of URIs from this online test.
	 */
	public URI[] getURIs() {
		return uris;
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof OnlineTest) {
			OnlineTest t = (OnlineTest) o;
			return principal.equals(t.principal) && Util.equals(uris, t.uris);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return principal.hashCode() ^ Util.hashCode(uris);
	}

	abstract String getSexpType();
	abstract Sexp[] getSexpParts();

	public final SexpList toSexp() {
		List l = new ArrayList();
		l.add(SexpUtil.toSexp(getSexpType()));
		l.add(SexpUtil.toSexp(getURIs()));
		l.add(getPrincipal().toSexp());
		Sexp[] parts = getSexpParts();
		for (int i = 0; i < parts.length; i++) {
			l.add(parts[i]);
		}
		return SexpUtil.toSexp("online", l);
	}

	static OnlineTest parseOnlineTest(SexpList l) throws SexpParseException {
		Iterator obody = SexpUtil.getBody(l);
		String type = SexpUtil.getNextString(obody, "online test type");
		URI[] uris =
			SexpUtil.parseURIs(
				SexpUtil.getNextList(obody, "uri", "online test uris"));
		Principal principal =
			Principal.parsePrincipal(
				SexpUtil.getNextList(obody, "online test principal"));
		if (type.equals("crl")) {
			return Revocation.parseRevocation(principal, uris, obody);
		}
		if (type.equals("reval")) {
			return Revalidation.parseRevalidation(principal, uris, obody);
		}
		if (type.equals("one-time")) {
			return OneTime.parseOneTime(principal, uris, obody);
		}
		throw new SexpParseException("unrecognized online test type: " + type);
	}
}