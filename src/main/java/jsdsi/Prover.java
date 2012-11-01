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

import java.security.cert.CertStoreException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Given a statement (a <code>Cert</code>) and a <code>CertStore</code>, 
 * attempts to construct a <code>Proof</code> that the statement holds using 
 * certificates from the store.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
abstract class Prover {
	/**
	 * Thrown when the Prover <i>finds a proof</i>.  This is an abuse of
	 * exceptions, but it makes it easy to return a proof from deep
	 * within a recursive call.  Ugly, but effective.
	 * 
	 * @author Sameer Ajmani
	 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
	 */
	static class ProofFoundException extends Exception {
		Proof proof;
		ProofFoundException(Proof p) {
			super("found proof: " + p);
			proof = p;
		}
		Proof getProof() {
			return proof;
		}
	}
	
	/**
	 * Statement to prove.
	 */
	Cert provee;
	
	/**
	 * The <code>CertStore</code> used by this <code>Proof</code>.
	 */
	java.security.cert.CertStore store;
	
	/**
	 * cert -> set of proof(cert)
	 */
	MultiMap check = new MultiMap();
	
	/**
	 * name -> set of proof(name -> principal)
	 */
	MultiMap value = new MultiMap();
	
	/**
	 * name -> set of proof(LHS -> name+X)
	 */
	MultiMap compatible = new MultiMap();
	// name -> set of proof(LHS -> name+X)
	
	/**
	 * issuer -> set of proof(issuer -> RHS)
	 */
	MultiMap issuer = new MultiMap();
	
	/**
	 * subject -> set of proof(LHS -> subject)
	 */
	MultiMap reverse = new MultiMap();
	// subject -> set of proof(LHS -> subject)

	/**
	 * Number of certs fetched from cert store.
	 */
	private int numFetched = 0;
	
	/**
	 * Returns the number of certificates fetched from the cert-store.
	 * 
	 * @return the number of certificates fetched from the cert-store.
	 */
	int getNumFetched() {
		return numFetched;
	}

	/**
	 * Creates a new <code>Prover</code> from a given <code>Cert</code> and
	 * a given <code>CertStore</code>.
	 * 
	 * @param  c <code>Cert</code> for this prover.
	 * @param  s <code>CertStore</code> for this prover.
	 */
	Prover(Cert c, java.security.cert.CertStore s) {
		provee = c;
		store = s;
	}

	/**
	 * Indicates if there has already be an attempt to find a proof.
	 */
	private boolean attempted = false;

	/**
	 * The proof found by this prover.
	 */
	private Proof proof; // the proof; null if no proof found

	/**
	 * Returns the proof found by this prover.
	 * 
	 * @return the proof found by this prover.
	 */
	public final Proof getProof() {
		if (!attempted) {
			attempted = true;
			proof = makeProof();
		}
		return proof;
	}

	/**
	 * Creates a new <code>Proof</code>.
	 * 
	 * @return a new <code>Proof</code>.
	 */
	abstract Proof makeProof();
	
	/**
	 * Inserts the certificates of a given <code>Proof</code> to this
	 * <code>Proof</code>.
	 * 
	 * @param  p <code>Proof</code> containing certificates to add to
	 *         this <code>Proof</code>.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	abstract void insert(Proof p) throws ProofFoundException;

	/**
	 * Provides new certificates for this prover. Adds the given certificates
	 * to the certificates already used.
	 * 
	 * @param  certs certificates to add to the prover.
	 * @throws ProofFoundException if a <i>proof is found</i> while
	 * adding the certificates.
	 */
	void insertCertificates(Collection certs) throws ProofFoundException {
		// insert stored proofs
		//System.out.println("INSERT: inserting certs "+certs.size());
		Iterator i = certs.iterator();
		while (i.hasNext()) {
			insert(new Proof((Certificate) i.next()));
		}
	}

	/**
	 * If a given set does not contain a given object, all certficates
	 * from the cert-store for a given <code>CertSelector</code> will
	 * be added to this proof's certificates.
	 * 
	 * @param  cache set with objects.
	 * @param  key key to search in <code>cache</code>.
	 * @param  sel cert selector to add certificates from to this proof's
	 *         certificates.
	 * @param  map multi-map with sets stored for keys.
	 * @return the set stored in <code>map</code> for <code>key</code> if
	 *         no error occures.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	Set load(Set cache, Object key, CertSelector sel, MultiMap map)
		throws ProofFoundException {
		try {
			if (!cache.contains(key)) {
				cache.add(key);
				// fetch stored proofs
				try {
					Collection stored = store.getCertificates(sel);
					numFetched += stored.size();
					insertCertificates(stored);
				} catch (CertStoreException e) {
					throw new Error(e);
				}
			}
			return map.get(key);
		} catch (ProofFoundException e) {
			// invalidate cache
			cache.remove(key);
			throw e;
		}
	}
}
