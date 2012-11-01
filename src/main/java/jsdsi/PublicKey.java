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
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;
import jsdsi.util.DigestAlgoEnum;

/**
 * A public key.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class PublicKey
	extends Principal
	implements Element, java.security.PublicKey {
    
    private static final long serialVersionUID = 6633153639072781861L;

	/**
	 * Name of the algorithm used.
	 */
	private transient final String algo;

	/**
	 * List of URIs.
	 */
	private transient final URI[] uris;

	/**
	 * Creates a new <code>PublicKey</code> for a given algorithm name and
	 * list of URLs.
	 * 
	 * @param  a name of the algorithm used.
	 * @param  u list of URLs (may be <code>null</code>).
	 * @deprecated use {@link #PublicKey(String, URI[])}
	 */
	public PublicKey(String a, URL[] u) {
		assert(a != null) : "null algo";
		algo = a;
		uris = Util.convert(u); // may be null
	}

	/**
	 * Creates a new <code>PublicKey</code> for a given algorithm name and
	 * list of URIs.
	 * 
	 * @param  a name of the algorithm used.
	 * @param  u list of URIs (may be <code>null</code>).
	 */
	public PublicKey(String a, URI[] u) {
		assert(a != null) : "null algo";
		algo = a;
		uris = u; // may be null
	}
	
	/**
	 * Creates a new <code>PublicKey</code> from an algorithm name.
	 * 
	 * @param  a name of the algorithm name used.
	 */
	public PublicKey(String a) {
		this(a, (URI[])null);
	}

	/**
	 * @see java.security.Key#getAlgorithm()
	 */
	public String getAlgorithm() {
		return algo;
	}
	
	/**
	 * @see java.security.Key#getFormat()
	 */
	public String getFormat() {
		return "SEXP";
	}

	/**
	 * @see java.security.Key#getEncoded()
	 */
	public byte[] getEncoded() {
		return toByteArray();
	}

	/**
	 * Returns a list of URLs of this <code>PublicKey</code>.
	 * 
	 * @return a list of URLs of this <code>PublicKey</code>.
	 * @deprecated use {@link PublicKey#getURIs()}
	 */
	public URL[] getURLs() {
		return Util.convert(uris);
	}

	/**
	 * Returns a list of URIs where information about this <code>PublicKey</code> maybe found.
	 * 
	 * @return the list of URIs.
	 */
	public URI[] getURIs() {
		return uris;
	}
	
	/**
	 * Generate the corresponding PublicKeyHash for this PublicKey.
	 * @param da the digest algorithm for the hash function. 
	 * @return
	 */
	public PublicKeyHash publicKeyHash(DigestAlgoEnum da) {
	    Hash h = new Hash(da, this, getURIs());
	    return new PublicKeyHash(h);
	}
	
	/**
	 * @see jsdsi.Principal#samePrincipalAs(Principal)
	 */
	public boolean samePrincipalAs(Principal p) {
		if (p == null) {
			return false;
		}
		if (p instanceof PublicKey) {
			return equals(p);
		}
		// p is a PublicKeyHash
		return p.samePrincipalAs(this);
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof PublicKey) {
			PublicKey p = (PublicKey) o;
			return algo.equals(p.algo) && Util.equals(uris, p.uris);
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return algo.hashCode() ^ Util.hashCode(uris);
	}

	abstract SexpList toPublicKeySexp();
	public final SexpList toSexp() {
		List l = new ArrayList(2);
		l.add(toPublicKeySexp());
		if (getURIs() != null) {
			l.add(SexpUtil.toSexp(getURIs()));
		}
		return SexpUtil.toSexp("public-key", l);
	}

	/**
	 * Parses an Sexp of a publickey returning the <code>PublicKey</code>.
	 * @param l the Sexpression
	 * @return the PublicKey
	 * @throws SexpParseException
	 */
	static PublicKey parsePublicKey(SexpList l) throws SexpParseException {
		Iterator kbody = SexpUtil.getBody(l);
		SexpList key = SexpUtil.getNextList(kbody, "public key body");
		// see if there are any uri's
		URI[] uris = null;
		if (kbody.hasNext()) {
			SexpList uri = SexpUtil.getNextList(kbody, "public key uri");
			String type = uri.getType();
			if (!type.equals("uri"))
				throw new SexpParseException(
						"unrecognized public-key field: " + type);
			uris = SexpUtil.parseURIs(uri);
		}
		// check parsing of public key is done
		SexpUtil.checkDone(kbody, "public-key");
		String type = key.getType();
		if (type.startsWith("rsa")) {
			return RSAPublicKey.parseRSAPublicKey(key, "RSA", uris);
		}
		throw new SexpParseException("unrecognized public-key type: " + type);
	}
}