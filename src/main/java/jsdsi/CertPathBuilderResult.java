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

/**
 * The result of a call to the cert path builder: contains the
 * certification path itself.
 *
 * @see CertPathBuilder
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertPathBuilderResult
	implements java.security.cert.CertPathBuilderResult {
	/**
	 * Certificate path of this <code>CertPathBuilderResult</code>.
	 */
	private CertPath path;

	/**
	 * Stats of this <code>CertPathBuilderResult</code>.
	 */
	private CertPathBuilderStats stats;

	/**
	 * Creates a new <code>CertPathBuilderResult</code> from a given
	 * <code>CertPath</code> and <code>CertPathBuilderStats</code>.
	 * 
	 * @param  p <code>CertPath</code> for the new 
	 *         <code>CertPathBuilderResult</code>.
	 * @param  s <code>CertPathBuilderStats</code> for the new
	 *         <code>CertPathBuilderResult</code>.
	 */
	public CertPathBuilderResult(CertPath p, CertPathBuilderStats s) {
		path = p;
		stats = s;
	}

	/**
	 * Returns the <code>CertPath</code> of this 
	 * <code>CertPathBuilderResult</code>.
	 * 
	 * @return the <code>CertPath</code> of this 
	 *         <code>CertPathBuilderResult</code>.
	 */
	public java.security.cert.CertPath getCertPath() {
		return path;
	}

	/**
	 * Returns the stats of this <code>CertPathBuilderResult</code>.
	 * 
	 * @return the stats of this <code>CertPathBuilderResult</code>.
	 */
	public CertPathBuilderStats getStats() {
		return stats;
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		return new CertPathBuilderResult(path, stats);
	}
}