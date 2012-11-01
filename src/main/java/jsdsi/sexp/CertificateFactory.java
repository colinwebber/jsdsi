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
package jsdsi.sexp;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactorySpi;
import java.security.cert.CertificateParsingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jsdsi.Obj;
import jsdsi.Proof;
import jsdsi.Sequence;

/**
 * Creates certificates and certification paths from S-expressions.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class CertificateFactory extends CertificateFactorySpi {
	private Obj readObj(InputStream in)
		throws SexpParseException, SexpException, IOException {
		return new ObjInputStream(in).readObj();
	}
	private java.security.cert.Certificate readCertificate(InputStream in)
		throws CertificateException, SexpParseException, SexpException, IOException {
		try {
			return jsdsi.Certificate.fromSequence((Sequence) readObj(in));
		} catch (ClassCastException e) {
			throw (CertificateParsingException) new CertificateParsingException(
				"expected a Sequence").initCause(
				e);
		}
	}
	public java.security.cert.Certificate engineGenerateCertificate(
		InputStream in)
		throws CertificateException {
		try {
			return readCertificate(in);
		} catch (CertificateException e) {
			throw e;
		} catch (Exception e) {
			throw (CertificateParsingException) new CertificateParsingException()
				.initCause(
				e);
		}
	}

	public Collection engineGenerateCertificates(InputStream in)
		throws CertificateException {
		Collection c = new ArrayList();
		while (true) {
			try {
				c.add(readCertificate(in));
			} catch (EOFException e) {
				break;
			} catch (CertificateException e) {
				throw e;
			} catch (Exception e) {
				throw (CertificateParsingException) new CertificateParsingException()
					.initCause(
					e);
			}
		}
		return c;
	}

	public java.security.cert.CertPath engineGenerateCertPath(InputStream in)
		throws CertificateException {
		try {
			return new jsdsi.CertPath((Proof) readObj(in));
		} catch (ClassCastException e) {
			throw (CertificateParsingException) new CertificateParsingException(
				"expected a Proof").initCause(
				e);
		} catch (Exception e) {
			throw (CertificateParsingException) new CertificateParsingException()
				.initCause(
				e);
		}
	}

	public java.security.cert.CertPath engineGenerateCertPath(
		InputStream in,
		String encoding)
		throws CertificateException {
		if (encoding.equals("SEXP")) {
			return engineGenerateCertPath(in);
		}
		throw new CertificateParsingException(
			"unsupported encoding: " + encoding);
	}

	public java.security.cert.CertPath engineGenerateCertPath(List certs)
		throws CertificateException {
		// we can't support this interface
		throw new UnsupportedOperationException();
	}

	public Iterator engineGetCertPathEncodings() {
		return Arrays.asList(new String[] { "SEXP" }).iterator();
	}

	public CRL engineGenerateCRL(InputStream in) throws CRLException {
		throw new UnsupportedOperationException();
	}

	public Collection engineGenerateCRLs(InputStream in) throws CRLException {
		throw new UnsupportedOperationException();
	}
}
