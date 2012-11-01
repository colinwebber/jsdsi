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
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.Iterator;

/**
 * A collection-based certificate store for SPKI/SDSI certificates.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/06/25 15:24:49 $
 */
public class CertStore extends CertStoreSpi {
	/**
	 * issuer -> set of AuthCert(issuer -> RHS)
	 */
	MultiMap auth = new MultiMap();
	 
	/**
	 * name -> set of NameCert(name -> RHS)
	 */
	MultiMap name = new MultiMap();
	
	/**
	 * name -> set of Cert(LHS -> name+X)
	 */
	MultiMap compatible = new MultiMap();
	
	/**
	 * subject -> set of Cert(LHS -> subject)
	 */
	MultiMap subject = new MultiMap();
	
	/**
	 * Adds new certificates to this <code>CertStore</code>.
	 * 
	 * @param  params collection of certificates to add to this 
	 *         <code>CertStore</code>.
	 */
	private void init(CollectionCertStoreParameters params) {
		Iterator i = params.getCollection().iterator();
		while (i.hasNext()) {
			jsdsi.Certificate c = (jsdsi.Certificate) i.next();
			if (c.getCert().getSubject() instanceof Name) {
				Name n = (Name) c.getCert().getSubject();
				compatible.put(n.prefix(), c);
			} else {
				// subject is not a name
				subject.put(c.getCert().getSubject(), c);
			}
			if (c.getCert() instanceof NameCert) {
				NameCert nc = (NameCert) c.getCert();
				name.put(nc.getFullName(), c);
			}
			if (c.getCert() instanceof AuthCert) {
				auth.put(c.getCert().getIssuer(), c);
			}
		}
	}
	
	/**
	 * Creates a new <code>CertStore</code> using the given parameters.
	 * 
	 * @see java.security.cert.CertStoreSpi#CertStoreSpi(java.security.cert.CertStoreParameters)
	 * 
	 * @param  params parameters to create the <code>CertStore</code> from.
	 * @throws InvalidAlgorithmParameterException if a problem occurs with 
	 *         <code>params</code>.
	 */
	public CertStore(CollectionCertStoreParameters params)
		throws InvalidAlgorithmParameterException {
		super(params);
		init(params);
	}
	
	/**
	 * @see java.security.cert.CertStoreSpi#CertStoreSpi(CertStoreParameters)
	 */
	public CertStore(CertStoreParameters params)
		throws InvalidAlgorithmParameterException {
		super(params);
		try {
			init((CollectionCertStoreParameters) params);
		} catch (ClassCastException e) {
			throw (InvalidAlgorithmParameterException)
                new InvalidAlgorithmParameterException().initCause(e);
		}
	}

	/**
	 * @see java.security.cert.CertStoreSpi#engineGetCertificates(CertSelector)
	 */
	public Collection engineGetCertificates(java.security.cert.CertSelector s)
		throws CertStoreException {
		if (!(s instanceof jsdsi.CertSelector)) {
			throw new CertStoreException("requires jsdsi.CertSelector");
		}
		return engineGetCertificates((jsdsi.CertSelector) s);
	}

	/**
	 * @see java.security.cert.CertStoreSpi#engineGetCertificates(CertSelector)
	 */
	public Collection engineGetCertificates(jsdsi.CertSelector s)
		throws CertStoreException {
		if (s instanceof SubjectCertSelector) {
			return subject.get(((SubjectCertSelector) s).getSubject());
		}
		if (s instanceof CompatibleCertSelector) {
			return compatible.get(((CompatibleCertSelector) s).getFullName());
		}
		if (s instanceof NameCertSelector) {
			return name.get(((NameCertSelector) s).getFullName());
		}
		if (s instanceof AuthCertSelector) {
            // FIXME: this may return certs that don't satisfy the selector!
			return auth.get(((AuthCertSelector) s).getIssuer());
		}
		throw new CertStoreException(
			"unrecognized selector: " + s.getClass().getName());
	}

	/**
	 * @see java.security.cert.CertStoreSpi#engineGetCRLs(java.security.cert.CRLSelector)
	 */
	public Collection engineGetCRLs(java.security.cert.CRLSelector s)
		throws CertStoreException {
		throw new UnsupportedOperationException();
	}
}
