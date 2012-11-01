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
 * Statistics from a cert path builder process.
 *
 * @see CertPathBuilder
 * @see CertPathBuilderResult
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertPathBuilderStats {
	/**
	 * Number of certs fetched from cert store.
	 */
	private int numFetched;

	/**
	 * Creates a new <code>CertPathBuilderStats</code> with a given
	 * number of certs fetched from the cert store.
	 * 
	 * @param  fetched number of certs fetched from the cert store.
	 */
	CertPathBuilderStats(int fetched) {
		numFetched = fetched;
	}

	/**
	 * Returns the number of certs fetched from the cert store.
	 * 
	 * @return the number of certs fetched from the cert store.
	 */
	public int getNumFetched() {
		return numFetched;
	}
}