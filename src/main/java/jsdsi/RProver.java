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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A prover that searches subject-to-issuer.  Will only access the
 * <code>CertStore</code> using <code>CompatibleCertSelectors</code> 
 * and <ocde>SubjectCertSelectors</code>.
 *
 * @see CertStore
 * @see CompatibleCertSelector
 * @see SubjectCertSelector
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.3 $ $Date: 2004/06/25 19:28:03 $
 */
class RProver extends Prover {
	/**
	 * Certificates from issuers to subject's.
	 */
	Set loadedReverse = new HashSet();

	/**
	 * Certificates for issuers to local names.
	 */
	Set loadedCompatible = new HashSet();

	/**
	 * @see jsdsi.Prover#Prover(Cert, java.security.cert.CertStore)
	 */
	RProver(Cert c, java.security.cert.CertStore s) {
		super(c, s);
		assert(!(c.getSubject() instanceof Name));
	}

	/**
	 * @see jsdsi.Prover#makeProof()
	 */
	Proof makeProof() {
		try {
			loadReverse(provee.getSubject());
		} catch (ProofFoundException e) {
			return e.getProof();
		}
		return null;
	}

	/**
	 * Loads the certificates for a given subject to this 
	 * <code>RProver</code>'s certificates.
	 * 
	 * @param  s subject to load certificates for.
	 * @return this <code>RProvers</code> set of certificates for
	 *         the subject <code>s</code>.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	Set loadReverse(Subject s) throws ProofFoundException {
		CertSelector sel = new SubjectCertSelector(s);
		return load(loadedReverse, s, sel, reverse);
	}

	/**
	 * Loads all certificates for a given name-issuer and
	 * name-string (local) to this<code>RProver</code>'s certificates.
	 * 
	 * @param  n name to load the certificates for.
	 * @return a set of certificates from the issuer of <code>n</code>
	 *         for the local name of <code>n</code>.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	Set loadCompatible(Name n) throws ProofFoundException {
		CertSelector sel =
			new CompatibleCertSelector(n.getIssuer(), n.getNames()[0]);
		return load(loadedCompatible, n, sel, compatible);
	}

	/**
	 * @see jsdsi.Prover#insert(Proof)
	 */
	void insert(Proof p) throws ProofFoundException {
		//System.out.println("INSERT("+p.hashCode()+"): "+p);
		if (p.getCert().implies(provee)) {
			//System.out.println("INSERT("+p.hashCode()+"): found proof!");
			throw new ProofFoundException(p);
		}
		if (!check.get(p.getCert()).isEmpty()) {
			//System.out.println("INSERT("+p.hashCode()+"): already inserted");
			return; // already have this proof
		}
		check.put(p.getCert(), p);

		try {
			if (p.getCert().getSubject() instanceof Name) {
				Name key = ((Name) p.getCert().getSubject()).prefix();
				compatible.put(key, p);
				// look up compatible certs, and compose
				Set values = value.get(key);
				//System.out.println("INSERT("+p.hashCode()
				//+"): inserting right-composed "+values.size());
				Iterator i = values.iterator();
				while (i.hasNext()) {
					try {
						insert(p.compose((Proof) i.next()));
					} catch (Proof.IncompatibleException e) {
                        //System.out.println("ignoring: "+e);
					}
				}
				return;
			}

			if (p.getCert() instanceof NameCert) {
				Name key = ((NameCert) p.getCert()).getFullName();
				value.put(key, p);
				reverse.put(p.getCert().getSubject(), p);
				// look up compatible certs, and compose
				Set compats = loadCompatible(key);
				//System.out.println("INSERT("+p.hashCode()
				//+"): inserting left-composed "+compats.size());
				Iterator i = compats.iterator();
				while (i.hasNext()) {
					try {
						insert(((Proof) i.next()).compose(p));
					} catch (Proof.IncompatibleException e) {
                        //System.out.println("ignoring: "+e);
					}
				}
				// search backwards to find extended names
				Subject s = p.getCert().getIssuer();
				//System.out.println("INSERT("+p.hashCode()
				//+"): fetching reverse for "+s.hashCode());
				loadReverse(s);
				return;
			}

			if (p.getCert() instanceof AuthCert) {
				issuer.put(p.getCert().getIssuer(), p);
				reverse.put(p.getCert().getSubject(), p);

				// TODO: optimize for provee:
				// p.tag implies provee.tag

				if (((AuthCert) p.getCert()).getPropagate()
					&& (p.getCert().getSubject() instanceof Principal)) {
					// search forwards locally to find auth chains
					Set issuers = issuer.get(p.getCert().getSubject());
					Iterator i = issuers.iterator();
					while (i.hasNext()) {
						try {
							insert(p.compose((Proof) i.next()));
						} catch (Proof.IncompatibleException e) {
                            //System.out.println("ignoring: "+e);
						}
					}
				}

				// search backwards locally to find auth chains
				Set reverses = reverse.get(p.getCert().getIssuer());
				Iterator i = reverses.iterator();
				while (i.hasNext()) {
					try {
						Proof pf = (Proof) i.next();
						if ((pf.getCert() instanceof AuthCert)
							&& ((AuthCert) pf.getCert()).getPropagate()
							&& (pf.getCert().getSubject() instanceof Principal)) {
							insert(pf.compose(p));
						}
					} catch (Proof.IncompatibleException e) {
                        //System.out.println("ignoring: "+e);
					}
				}

				// search backwards to find new auths
				Subject s = p.getCert().getIssuer();
				//System.out.println("INSERT("+p.hashCode()
				//+"): fetching reverse for "+s.hashCode());
				loadReverse(s);
				return;
			}

			throw new Error(
				"unhandled case: " + p.getCert().getClass().getName());
		} catch (ProofFoundException e) {
			// invalidate cache
			check.remove(p.getCert(), p);
			throw e;
		}
	}
}
