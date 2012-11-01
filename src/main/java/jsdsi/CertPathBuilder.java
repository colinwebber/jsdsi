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

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderSpi;
import java.util.Iterator;

import jsdsi.util.*;

/**
 * Attempts to create a certification path that satisfies the given
 * parameters:  essentially a wrapper around the <code>Prover</code> class 
 * and its subclasses.
 *
 * @see CertPathParameters
 * @see Prover
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/12 00:32:40 $
 **/
public class CertPathBuilder extends CertPathBuilderSpi {
	/**
	 * @see java.security.cert.CertPathBuilderSpi#engineBuild(CertPathParameters)
	 */
	public java.security.cert.CertPathBuilderResult engineBuild(
		java.security.cert.CertPathParameters params)
		throws CertPathBuilderException, InvalidAlgorithmParameterException {
		try {
			return engineBuild((jsdsi.CertPathParameters) params);
		} catch (ClassCastException e) {
			throw (InvalidAlgorithmParameterException) new InvalidAlgorithmParameterException()
				.initCause(
				e);
		}
	}

	/**
	 * @see java.security.cert.CertPathBuilderSpi#engineBuild(CertPathParameters)
	 */
	public jsdsi.CertPathBuilderResult engineBuild(
		jsdsi.CertPathParameters params)
		throws CertPathBuilderException, InvalidAlgorithmParameterException {
		if (params instanceof IssuerCertPathParameters) {
			return engineBuild(
				new FProver(params.getCert(), params.getStore()));
		}
		if (params instanceof SubjectCertPathParameters) {
			return engineBuild(
				new RProver(params.getCert(), params.getStore()));
		}
		throw new InvalidAlgorithmParameterException(
			"unrecognized params: " + params.getClass().getName());
	}

	/**
	 * Returns a <code>CertPathBuilderResult</code> from agiven
	 * <code>Proof</code>.
	 * 
	 * @param  p prover to create a <code>CertPathBuilder</code> from.
	 * @return a <code>CertPathBuilder</code> created from the
	 *         <code>CertPath</code> from the proof of <code>p</code> and the
	 *         <code>CertPathBuilderStats</code> created from the number
	 *         of certs fetched from the cert store by the proof of 
	 *         <code>p</code>.
	 * @throws CertPathBuilderException if there is no proof in <code>p</code>.
	 */
	private jsdsi.CertPathBuilderResult engineBuild(Prover p)
		throws CertPathBuilderException {
		Proof pf = p.getProof();
		if (pf == null) {
			throw new CertPathBuilderException("no proof found");
		}
		return new CertPathBuilderResult(
			new CertPath(pf),
			new CertPathBuilderStats(p.getNumFetched()));
	}

	/**
	 * For command-line usage:<br>
	 * <code>java CertPathBuilder &lt;forward?&gt; &lt;input-file&gt;</code><br>
	 * Reads all certificates from the input-file, and tries to find a proof
	 * for every name and tag defined in the input-file.
	 * 
	 * @param  args command-line arguments.
	 * @throws Exception if an error occurs.
	 */
	public static void main(String[] args) throws Exception {
		Provider.install();
		if (args.length != 2) {
			System.err.println(
				"usage: java CertPathBuilder <forward?> <input-file>");
			return;
		}
		boolean forward = args[0].equals("true");
		Loader l = new Loader(args[1]);
		java.security.cert.CertPathBuilder builder =
			java.security.cert.CertPathBuilder.getInstance("SPKI");

		// try to find proofs from name to each key
		Iterator ni = l.getNames().iterator();
		while (ni.hasNext()) {
			Name n = (Name) ni.next();
			Iterator ki = l.getKeys().iterator();
			while (ki.hasNext()) {
				PublicKey k = (PublicKey) ki.next();
				NameCert c =
					new NameCert(
						n.getIssuer(),
						k,
						null,
						null,
						null,
						n.getNames()[0]);
				try {
					Proof pf =
						buildProof(builder, c, l.getCertStore(), forward);
					NameCert nc = (NameCert) pf.getCert();
					System.out.println(
						"RESULT: Proof for "
							+ whois(nc.getIssuer())
							+ " "
							+ nc.getName()
							+ " -> "
							+ whois((Principal) nc.getSubject()));
					System.out.println(pf);
				} catch (CertPathBuilderException e) {
					System.out.println(
						"RESULT: No proof for "
							+ n.getNames()[0]
							+ " -> "
							+ whois(k));
				}
			}
		}

		// try to find proofs with each tag, with and without propagate
		Iterator ii = l.getKeys().iterator();
		while (ii.hasNext()) {
			PublicKey issuer = (PublicKey) ii.next();
			Iterator ki = l.getKeys().iterator();
			while (ki.hasNext()) {
				PublicKey k = (PublicKey) ki.next();
				Iterator ti = l.getTags().iterator();
				while (ti.hasNext()) {
					Tag t = (Tag) ti.next();
					for (int i = 0; i < 2; i++) {
						boolean propagate = (i == 0);
						AuthCert c =
							new AuthCert(
								issuer,
								k,
								null,
								null,
								null,
								t,
								propagate);
						try {
							Proof pf =
								buildProof(
									builder,
									c,
									l.getCertStore(),
									forward);
							AuthCert ac = (AuthCert) pf.getCert();
							System.out.println(
								"RESULT: Proof for "
									+ whois(ac.getIssuer())
									+ " "
									+ (ac.getPropagate() ? "+" : "!")
									+ ((StringTag) ac.getTag()).getValue()
									+ " -> "
									+ whois((Principal) ac.getSubject()));
							System.out.println(pf);
						} catch (CertPathBuilderException e) {
							System.out.println(
								"RESULT: No proof for "
									+ whois(issuer)
									+ " "
									+ (propagate ? "+" : "!")
									+ ((StringTag) t).getValue()
									+ " -> "
									+ whois(k));
						}
					}
				}
			}
		}
	}

	/**
	 * Returns String with a sequence of bytes containing the public key 
	 * from a given principal.
	 * 
	 * @param  p <code>Principal</code> with public key to create the byte 
	 *         array from.
	 * @return a sequence of bytes in a string representing the public key of
	 *         <code>p</code>.
	 */
	private static String whois(Principal p) {
		return new String(((RSAPublicKey) p).getModulus().toByteArray());
	}

	/**
	 * Tries to find a proof for a given <code>Cert</code> and a given
	 * <code>CertStore</code> depending on a (un)set delegation bit.
	 * 
	 * @param  builder <code>CertBuilder</code> to construct the proof with.
	 * @param  c <code>Cert</code> to find the proof for.
	 * @param  s <code>Subject</code> to find the proof for.
	 * @param  forward delegation bit.
	 * @return a <code>Proof</code> for <code>c</code> and <code>s</code>.
	 * @throws CertPathBuilderException if an error occurs creating the
	 *         proof.
	 */
	private static Proof buildProof(
		java.security.cert.CertPathBuilder builder,
		Cert c,
		java.security.cert.CertStore s,
		boolean forward)
		throws CertPathBuilderException {
		try {
			jsdsi.CertPathParameters params;
			if (forward) {
				params = new IssuerCertPathParameters(c, s);
			} else {
				params = new SubjectCertPathParameters(c, s);
			}
			jsdsi.CertPathBuilderResult res =
				(jsdsi.CertPathBuilderResult) builder.build(params);
			System.out.println(
				"num fetched = " + res.getStats().getNumFetched());
			return ((jsdsi.CertPath) res.getCertPath()).getProof();
		} catch (InvalidAlgorithmParameterException e) {
			throw new Error(e);
		}
	}
}