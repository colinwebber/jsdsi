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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.spec.InvalidKeySpecException;

import jsdsi.Obj;

/**
 * Creates public keys from S-expressions.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class KeyFactory extends KeyFactorySpi {
	private Obj generateObj(java.security.spec.KeySpec spec)
		throws InvalidKeySpecException {
		if (!(spec instanceof jsdsi.sexp.KeySpec)) {
			throw new InvalidKeySpecException(
				"Not a " + jsdsi.sexp.KeySpec.class.getName());
		}

		try {
			return new ObjInputStream(
				new ByteArrayInputStream(
					((jsdsi.sexp.KeySpec) spec).getEncoded()))
				.readObj();
		} catch (SexpException e) {
			throw new InvalidKeySpecException(
				"Invalid S-expression: " + e.getMessage());
		} catch (SexpParseException e) {
			throw new InvalidKeySpecException(
				"Invalid SDSI object: " + e.getMessage());
		} catch (IOException e) {
			throw (InvalidKeySpecException) new InvalidKeySpecException()
				.initCause(
				e);
		}
	}

	protected java.security.PublicKey engineGeneratePublic(
		java.security.spec.KeySpec spec)
		throws InvalidKeySpecException {
		try {
			return (java.security.PublicKey) generateObj(spec);
		} catch (ClassCastException e) {
			throw (InvalidKeySpecException) new InvalidKeySpecException()
				.initCause(
				e);
		}
	}

	protected java.security.PrivateKey engineGeneratePrivate(
		java.security.spec.KeySpec spec)
		throws InvalidKeySpecException {
		try {
			return (java.security.PrivateKey) generateObj(spec);
		} catch (ClassCastException e) {
			throw (InvalidKeySpecException) new InvalidKeySpecException()
				.initCause(
				e);
		}
	}

	protected java.security.spec.KeySpec engineGetKeySpec(Key key, Class spec)
		throws InvalidKeySpecException {
		if (!jsdsi.sexp.KeySpec.class.isAssignableFrom(spec)) {
			throw new InvalidKeySpecException(
				"Not a "
					+ jsdsi.sexp.KeySpec.class.getName()
					+ ": "
					+ spec.getName());
		}
		if (!(key instanceof Obj)) {
			throw new InvalidKeySpecException(
				"Not a "
					+ Obj.class.getName()
					+ ": "
					+ key.getClass().getName());
		}
		return new jsdsi.sexp.KeySpec(((Obj) key).toByteArray());
	}

	protected Key engineTranslateKey(Key key) throws InvalidKeyException {
		if (!(key instanceof Obj)) {
			throw new InvalidKeyException(
				"Not a "
					+ Obj.class.getName()
					+ ": "
					+ key.getClass().getName());
		}
		return key;
	}
}