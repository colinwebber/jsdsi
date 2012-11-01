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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A statement (a <code>Cert</code>) and a sequence of 
 * <code>Certificates</code> (<code>Certs</code> + <code>validators</code>) 
 * that proves that the statement holds.  Proofs are
 * self-validating and can be composed to create new proofs.
 * 
 * @see Certificate
 * @see Cert
 * 
 * @author Sameer Ajmani
 * @author michael.jaeger@in-flux.de
 * @author Sean Radford
 * @version $Revision: 1.6 $ $Date: 2004/11/08 12:08:08 $
 */
public class Proof extends Obj {
    
    private static final long serialVersionUID = -1084625036476163346L;
    
	/**
	 * New Exception used by methods in this class.
	 * 
	 * @author Sameer Ajmani
	 */
	public static class IncompatibleException extends Exception {
	    private static final long serialVersionUID = -8565230129462618920L;
		/**
		 * @see java.lang.Throwable#Throwable(String)
		 */
		IncompatibleException(String message) {
			super(message);
		}
	}
	
	/**
	 * The statement to prove.
	 */
	private transient Cert cert;
	
	/**
	 * The certificates that prove it.
	 */
	private transient Certificate[] certs;
	
	/**
	 * Creates a new proof from a given <code>Certificate</code>.
	 * 
	 * @param  c <code>Certificate</code> to create the <code>Proof</code> 
	 *         from.
	 */
	public Proof(Certificate c) {
		assert(c != null) : "null certificate";
		cert = c.getCert();
		certs = new Certificate[] { c };
	}
	
	/**
	 * Creates a new proof from a <code>Cert</code> and an array of
	 * <code>Certificate</code>s.
	 * 
	 * @param  c <code>Cert</code> to create the proof from.
	 * @param  cs array of <code>Certificate</code> to create the proof from.
	 */
	private Proof(Cert c, Certificate[] cs) {
		assert(c != null) : "null cert";
		assert(cs != null) : "null certificates";
		cert = c;
		certs = cs;
	}
	
	/**
	 * Returns the <code>Cert</code> of this <code>Proof</code>.
	 * 
	 * @return the <code>Cert</code> of this <code>Proof</code>.
	 */
	public Cert getCert() {
		return cert;
	}
	
	/**
	 * Returns the array of <code>Certificate</code>s of this proof.
	 * 
	 * @return an array of <code>Certificate</code>s
     * of this <code>Proof</code>.
	 */
	public Certificate[] getCertificates() {
		return certs;
	}
	
	/**
	 * Returns a <code>Sequence</code> of this <code>certs</code>' elements.
	 * 
	 * @return a <code>Sequence</code> of this <code>certs</code>' elements.
	 */
	public Sequence getSequence() {
		List elems = new ArrayList();
		for (int i = 0; i < certs.length; i++) {
			certs[i].toElements(elems);
		}
		Element[] es = new Element[elems.size()];
		return new Sequence((Element[]) elems.toArray(es));
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof Proof) {
			Proof p = (Proof) o;
			return cert.equals(p.cert) && Util.equals(certs, p.certs);
		}
		return false;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return cert.hashCode() ^ Util.hashCode(certs);
	}

	public SexpList toSexp() {
		Sexp[] ss = new Sexp[2];
		ss[0] = getCert().toSexp();
		ss[1] = getSequence().toSexp();
		return SexpUtil.toSexp("proof", ss);
	}

	static Proof parseProof(SexpList l) throws SexpParseException {
		Iterator pbody = SexpUtil.getBody(l);
        Cert cert = Cert.parseCert
            (SexpUtil.getNextList(pbody, "cert"));
        Sequence seq = Sequence.parseSequence
            (SexpUtil.getNextList(pbody, "sequence"));
		SexpUtil.checkDone(pbody, "proof"); // sanity check
        // convert the sequence to and array of Certificates
        Iterator elms = Arrays.asList(seq.getElements()).iterator();
        List certs = new ArrayList();
        while (elms.hasNext()) {
            try {
                certs.add(Certificate.fromElements(elms));
            } catch (CertificateException e) {
                throw new SexpParseException(e);
            }
        }
		return new Proof
            (cert, (Certificate[])certs.toArray(new Certificate[0]));
	}

	/**
	 * Verifies this proof.
	 * 
	 * @see java.security.cert.Certificate#verify(java.security.PublicKey)
	 * 
	 * @throws CertificateException if there are no certificates in this 
	 *         <code>Proof</code> or they don't match/compose correctly.
	 * @throws NoSuchAlgorithmException if an unknown crypto algorithm 
	 *         has been used.
	 * @throws InvalidKeyException if a key in the certificate is not correct.
	 * @throws NoSuchProviderException if there is no security provider for 
	 *         this algorithm.
	 * @throws SignatureException if an error occured with a signature.
	 */
	public void verify()
		throws
			CertificateException,
			NoSuchAlgorithmException,
			InvalidKeyException,
			NoSuchProviderException,
			SignatureException {
		if (certs.length == 0) {
			throw new CertificateException("no certificates");
		}
		certs[0].verify(certs[0].getPublicKey());
		Proof p = new Proof(certs[0]);
		try {
			for (int i = 1; i < certs.length; i++) {
				certs[i].verify(certs[i].getPublicKey());
				p = p.compose(new Proof(certs[i]));
			}
		} catch (IncompatibleException e) {
			throw (CertificateException)
                new CertificateException
                ("certificates do not compose correctly")
                .initCause(e);
		}
		if (!p.getCert().equals(cert)) {
			throw new CertificateException
                ("composed certificates do not match proof");
		}
	}

	/**
	 * Tries to compose a <code>Name</code> with another for a given
	 * <code>Principal</code>, that is it checks if the second name given is
	 * a prefix of the first name and if so it tries to create a new name
	 * from the rest of the name-strings the first name posesses more than
	 * the second one and substitutes this name-strings from the first name
	 * in the second name with the subject given.
	 * 
	 * @see Name#prefixOf(Name)
	 * 
	 * @param  lhName <code>Name</code> to compose with <code>rhName</code>.
	 * @param  rhName <code>Name</code> to compose with <code>lhName</code>.
	 *         (should be a prefix of <code>lhName</code>)
	 * @param  rhSubject <code>Pincipal</code> for 
	 * @return a new <code>Name</code> if <code>rhName</code> is a prefix of
	 *         <code>lhName</code> and bot hare not equal, or 
	 *         <code>rhSubject</code> if both names are equal.
	 * @throws IncompatibleException if <code>rhName</code> is not a prefix
	 *         of <code>lhName</code> or another problem occurs with both 
	 *         names.
	 */
	private Subject composeNames(Name lhName, Name rhName, Subject rhSubject)
		throws IncompatibleException {
		if (!rhName.prefixOf(lhName)) {
			throw new IncompatibleException(
				"Cannot compose " + lhName + " with " + rhName);
		}
		int diff = lhName.getNames().length - rhName.getNames().length;
		if (diff == 0) {
			return rhSubject;
		}
		assert(diff > 0) : "expected righthand name length <= lefthand";
		if (!(rhSubject instanceof Principal)) {
			throw new IncompatibleException("Cannot compose name with non-reducing cert");
		}
		// replace rhName with rhSubject in lhName
		String[] names = new String[diff];
		int off = lhName.getNames().length - diff;
		for (int i = 0; i < names.length; i++) {
			names[i] = lhName.getNames()[off + i];
		}
		return new Name((Principal) rhSubject, names);
	}

	/**
	 * Tries to compose a name certificate with a given <code>NameCert</code>
	 * by composing a new name from both names and the subject of the second
	 * name certificate.
	 * 
	 * @param  lhs name certificate to compose with <code>rhs</code>.
	 * @param  rhs name certificate to compose with <code>lhs</code>.
	 * @return a new name from the composition of both names or the
	 *         subject of <code>rhs</code> if both names are equal.
	 * @throws IncompatibleException if <code>lhs</code> is not a 
	 *         <code>Name</code>.
	 */
	private Subject composeWithNameCert(Cert lhs, NameCert rhs)
		throws IncompatibleException {
		if (!(lhs.getSubject() instanceof Name)) {
			throw new IncompatibleException("Cannot compose reducing Cert with NameCert");
		}
		Name lhName = (Name) lhs.getSubject();
		// TODO: allow variable-length names on RHS
		Name rhName = rhs.getFullName();
		return composeNames(lhName, rhName, rhs.getSubject());
	}

	/**
	 * Intersects to validities and returns the result.
	 * 
	 * @param  v1 validity to intersect with the other.
	 * @param  v2 validity to intersect with the other.
	 * @return the intersection of <code>v1</code> and <code>v2</code>.
	 */
	private Validity intersect(Validity v1, Validity v2) {
		if (v1 == null) {
			return v2;
		}
		if (v2 == null) {
			return v1;
		}
		return v1.intersect(v2);
	}

	/**
	 * Composes to given names and creates a new <code>NameCert</code> from it.
	 * 
	 * @param  lhs first name to inersect.
	 * @param  rhs second name to intersect.
	 * @return a new <code>NameCert</code> composed of <code>lhs</code> and
	 *         <code>rhs</code>.
	 * @throws IncompatibleException if <code>lhs</code> is not a proper name 
	 *         (should never happen!).
	 */
	private NameCert composeNameName(NameCert lhs, NameCert rhs)
		throws IncompatibleException {
		return new NameCert(
			lhs.getIssuer(),
			composeWithNameCert(lhs, rhs),
			intersect(lhs.getValidity(), rhs.getValidity()),
			lhs.getDisplay(),
			lhs.getComment(),
			lhs.getName());
	}

	/**
	 * Creates a new <code>AuthCert</code> from a given <code>AuthCert</code>
	 * and <code>NameCert</code>.
	 * 
	 * @param  lhs <code>AuthCert</code> to compose with <code>rhs</code>.
	 * @param  rhs <code>NameCert</code> to compose with <code>lhs</code>.
	 * @return a new <code>AuthCert</code> composed from <code>lhs</code>
	 *         and <code>rhs</code>.
	 * @throws IncompatibleException if <code>lhs</code> is not a proper name 
	 *         (should never happen!).
	 */
	private AuthCert composeAuthName(AuthCert lhs, NameCert rhs)
		throws IncompatibleException {
		return new AuthCert(
			lhs.getIssuer(),
			composeWithNameCert(lhs, rhs),
			intersect(lhs.getValidity(), rhs.getValidity()),
			lhs.getDisplay(),
			lhs.getComment(),
			lhs.getTag(),
			lhs.getPropagate());
	}

	/**
	 * Composes one <code>AuthCert</code> with another if the delegation bit
	 * in the first auth certificate is set.
	 * 
	 * @param  lhs <code>AuthCert</code> to compose with <code>rhs</code>.
	 * @param  rhs <code>AuthCert</code> that should be composed with 
	 *         <code>lhs</code>.
	 * @return a new <code>AuthCert</code> from <code>lhs</code> and 
	 *         <code>rhs</code>.
	 * @throws IncompatibleException if the delegation bit in <code>lhs</code>
	 *         is not set.
	 */
	private AuthCert composeAuthAuth(AuthCert lhs, AuthCert rhs)
		throws IncompatibleException {
		if (!lhs.getPropagate()) {
			throw new IncompatibleException("cannot propagate this auth");
		}
        Tag newTag = lhs.getTag().intersect(rhs.getTag());
        if (newTag == Tag.NULL_TAG) {
            throw new IncompatibleException("auths have null intersection");
        }
		return new AuthCert(
			lhs.getIssuer(),
			rhs.getSubject(),
			intersect(lhs.getValidity(), rhs.getValidity()),
			lhs.getDisplay(),
			lhs.getComment(),
			newTag,
			rhs.getPropagate());
	}

	/**
	 * Returns an array of certificates from this <code>Proof</code> and
	 * a given <code>Proof</code>.
	 * 
	 * @param  p <code>Proof</code> to concat this certificates with.
	 * @return an array of <code>Certificate</code> that contains this
	 *         <code>Proof</code>'s certificates and the certificates from
	 *         <code>p</code>.
	 */
	private Certificate[] concat(Proof p) {
		Certificate[] cs = new Certificate[certs.length + p.certs.length];
		System.arraycopy(certs, 0, cs, 0, certs.length);
		System.arraycopy(p.certs, 0, cs, certs.length, p.certs.length);
		return cs;
	}

	/**
	 * Composes this proof with another proof.
	 * 
	 * @param  p the proof to compose this with.
	 * @return a new proof that is the composition of this with <code>p</code>.
	 * @throws IncompatibleException if this cannot be composed with 
	 *         <code>p</code>.
	 */
	public Proof compose(Proof p) throws IncompatibleException {
		if (cert instanceof NameCert) {
			if (!(p.cert instanceof NameCert)) {
				throw new IncompatibleException
                    ("Cannot compose NameCert with: "
                     + p.cert.getClass().getName());
			}
			return new Proof
                (composeNameName((NameCert) cert, (NameCert) p.cert),
                 concat(p));
		}
		if (cert instanceof AuthCert) {
			if (p.cert instanceof NameCert) {
				return new Proof
                    (composeAuthName((AuthCert) cert, (NameCert) p.cert),
                     concat(p));
			}
			if (p.cert instanceof AuthCert) {
				return new Proof
                    (composeAuthAuth((AuthCert) cert, (AuthCert) p.cert),
                     concat(p));
			}
            throw new ClassCastException
                ("Cannot compose AuthCert with: "
                 + p.cert.getClass().getName());
		}
		throw new ClassCastException
            ("Unrecognized Cert subclass: "
             + cert.getClass().getName());
	}
}
