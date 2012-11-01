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

import java.security.cert.CertificateEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A SPKI certification path: essentially a wrapper around the 
 * <code>Proof</code> class.
 *
 * @see Proof
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class CertPath extends java.security.cert.CertPath {
    
    private static final long serialVersionUID = -2732249332091399979L;
    
	/**
	 * The <code>Proof</code> of this <code>CertPath</code>.
	 */
	private transient Proof proof;

	/**
	 * Creates a new <code>CertPath</code> from a given proof.
	 * 
	 * @param  p <code>Proof</code> to create the <code>CertPath</code> from.
	 */
	public CertPath(Proof p) {
		super("SPKI");
		assert(p != null) : "null proof";
		proof = p;
	}

	/**
	 * Returns the <code>Proof</code> of this <code>CertPath</code>.
	 * 
	 * @return the <code>Poof</code> of this <code>CertPath</code>.
	 */
	public Proof getProof() {
		return proof;
	}

	/**
	* Returns the type of this <code>CertPath</code>, namely 
	* <code>&quot;SPKI&quot;</code>.
	* 
	* @see java.security.cert.CertPath#getType()
	* 
	* @return the type of this <code>CertPath</code>, namely 
	*         <code>&quot;SPKI&quot;</code>.
	*/
	public String getType() {
		return "SPKI";
	}

	/**
	 * @see java.security.cert.CertPath#getEncodings()
	 */
	public Iterator getEncodings() {
		return Arrays.asList(new String[] { "SEXP" }).iterator();
	}

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		return (o instanceof CertPath) && proof.equals(((CertPath) o).proof);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return proof.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return proof.toString();
	}

	/**
	 * @see java.security.cert.CertPath#getEncoded()
	 */
	public byte[] getEncoded() throws CertificateEncodingException {
		return proof.toByteArray();
	}

	/**
	 * @see java.security.cert.CertPath#getEncoded(String)
	 */
	public byte[] getEncoded(String encoding)
		throws CertificateEncodingException {
		if (encoding.equals("SEXP")) {
			return getEncoded();
		}
		throw new CertificateEncodingException(
			"unsupported encoding: " + encoding);
	}

	/**
	 * @see java.security.cert.CertPath#getCertificates()
	 */
	public List getCertificates() {
		return Arrays.asList(proof.getCertificates());
	}
}