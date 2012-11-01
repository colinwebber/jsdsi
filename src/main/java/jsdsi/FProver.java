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
 * A prover that searches issuer-to-subject.  Will only access the
 * <code>CertStore</code> using <code>AuthCertSelector</code>s and 
 * <code>NameCertSelector</code>s.
 *
 * @see CertStore
 * @see AuthCertSelector
 * @see NameCertSelector
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.3 $ $Date: 2004/06/25 19:28:03 $
 */
class FProver extends Prover {
	/**
	 * Certificates for all issuers.
	 */
	Set loadedIssuer = new HashSet();

	/**
	 * Certificates for issuers->name-string.
	 */
	Set loadedValue = new HashSet();

	/**
	 * @see jsdsi.Prover#Prover(Cert, java.security.cert.CertStore)
	 */
	FProver(Cert c, java.security.cert.CertStore s) {
		super(c, s);
	}

	/**
	 * @see jsdsi.Prover#makeProof()
	 */
	Proof makeProof() {
		try {
			if (provee instanceof NameCert) {
				loadValue(((NameCert) provee).getFullName());
			} else {
				loadIssuer(provee.getIssuer());
			}
		} catch (ProofFoundException e) {
			return e.getProof();
		}
		return null;
	}

	/**
	 * Loads all certificates for a given issuer from the cert store to this
	 * <code>FProver</code>'s stored certificates.
	 * 
	 * @param  i issuer to add the certificates from.
	 * @return a set of this <code>FProver</code>'s certificates plus the
	 *         certificates added.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	Set loadIssuer(Principal i) throws ProofFoundException {
		CertSelector sel = new AuthCertSelector(i);
		return load(loadedIssuer, i, sel, issuer);
	}

	/**
	 * Loads all certificates for the issuer of a given name and a name-string
	 * from the cert store to this <code>FProver</code>'s stored certificates.
	 * 
	 * @param  n name to add the certificates for <code>n</code>'s issuer and
	 *         name-string bindings.
	 * @return a set with the used certificates plus the certificates added.
	 * @throws ProofFoundException if a <i>proof is found</i>.
	 */
	Set loadValue(Name n) throws ProofFoundException {
		CertSelector sel = new NameCertSelector(n.getIssuer(), n.getNames()[0]);
		return load(loadedValue, n, sel, value);
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
				Set values = loadValue(key);
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
				// look up compatible certs, and compose
				Set compats = compatible.get(key);
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
				return;
			}

			if (p.getCert() instanceof AuthCert) {
				issuer.put(p.getCert().getIssuer(), p);
				reverse.put(p.getCert().getSubject(), p);

				// TODO: optimize for provee:
				// check whether p.tag implies provee.tag

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
							&& (pf.getCert().getSubject()
                                instanceof Principal)) {
							insert(pf.compose(p));
						}
					} catch (Proof.IncompatibleException e) {
                        //System.out.println("ignoring: "+e);
					}
				}

				if (((AuthCert) p.getCert()).getPropagate()
					&& (p.getCert().getSubject() instanceof Principal)) {
					// search forwards to find new auths
					Subject s = p.getCert().getSubject();
					//System.out.println("INSERT("+p.hashCode()
					//+"): fetching issuer for "+s.hashCode());
					loadIssuer((Principal) s);
				}
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
