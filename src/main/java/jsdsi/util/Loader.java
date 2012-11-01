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
package jsdsi.util;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jsdsi.AuthCert;
import jsdsi.Cert;
import jsdsi.Certificate;
import jsdsi.Hash;
import jsdsi.Name;
import jsdsi.NameCert;
import jsdsi.Principal;
import jsdsi.RSAPublicKey;
import jsdsi.Signature;
import jsdsi.StringTag;
import jsdsi.Subject;
import jsdsi.Tag;

/**
 * <p><strong>NOTE:</strong> This class is purely used for testing purposes
 * and creates invalid certificates with fake signatures.</p>
 * 
 * <p>Creates a set of certificates from a flat text file for testing.
 * Each certificate appears on its own line.  Blank lines are allowed,
 * but comments are not.
 *
 * <p>Name certs are specified as follows:
 * <br><code>ISSUER name -&gt; SUBJECT [names...]</code>
 * 
 * <p>For example,
 * <br><code>ALICE friends -&gt; BOB</code>
 * <br><code>BOB my-friends -&gt; BOB sister</code>
 * <br><code>BOB sister -&gt; CAROL</code>
 *
 * <p>Note that we use uppercase for keys and lowercase for names for
 * clarity, but this is not required.  However, lines are
 * case-sensitive, so <code>&quot;BOB&quot;</code> is different from 
 * <code>&quot;Bob&quot;</code>.
 *
 * <p>Auth certs are specified as follows:
 * <br><code>ISSUER [!|+]tag -&gt; SUBJECT [names...]</code>
 * <br>where ! means the permission <code>&quot;tag&quot;</code> cannot be 
 * delegated, while <code>&quot;+&quot;</code>
 * means that it can.
 *
 * <p>For example,
 * <br><code>ALICE !read -&gt; BOB</code>
 * <br><code>BOB +write -&gt; BOB my-friends</code>
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/08/13 07:28:24 $
 */
public class Loader {
	/**
	 * The keys.
	 */
	private Map keys = new HashMap();

	/**
	 * The certs.
	 */
	private Set certSet = new HashSet();

	/**
	 * The names.
	 */
	private Set nameSet = new HashSet();

	/**
	 * Thes tags.
	 */
	private Set tagSet = new HashSet();

	/**
	 * The cert store.
	 */
	private java.security.cert.CertStore store;

	/**
	 * Creates a new <code>Loader</code> from a given filename.
	 * 
	 * @param  filename filename to read the certificates from.
	 * @throws IOException if an error occurs reading the file
	 *         <code>filename</code>.
	 */
	public Loader(String filename) throws IOException {
		Set certs = new HashSet();
		LineNumberReader in = new LineNumberReader(new FileReader(filename));
		in.setLineNumber(1);
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			StringTokenizer t = new StringTokenizer(line);
			if (t.countTokens() == 0) {
				continue; // skip empty lines
			}
			if (t.countTokens() < 4) {
				throw new IOException(
					"bad input on line " + in.getLineNumber() + ": " + line);
			}
			Principal issuer = getPrincipal(t.nextToken());
			String name = t.nextToken();
			String arrow = t.nextToken();
			if (!arrow.equals("->")) {
				throw new IOException(
					"bad arrow on line " + in.getLineNumber() + ": " + line);
			}
			Principal sub = getPrincipal(t.nextToken());
			String[] names = new String[t.countTokens()];
			for (int i = 0; i < names.length; i++) {
				names[i] = t.nextToken();
			}
			Subject subject =
				(names.length > 0)
					? (Subject) new Name(sub, names)
					: (Subject) sub;

			// create a fake certificate with a fake signature
			Cert c;
			if (name.startsWith("!") || name.startsWith("+")) {
				// this is an auth cert
				Tag tag = new StringTag(name.substring(1));
				tagSet.add(tag);
				c =
					new AuthCert(
						issuer,
						subject,
						null,
						null,
						null,
						tag,
						name.startsWith("+"));
				// propagate?
			} else {
				// this is a name cert
				nameSet.add(new Name(issuer, name));
				c = new NameCert(issuer, subject, null, null, null, name);
			}
			certSet.add(c);
			Signature s =
				new Signature(
					issuer,
					new Hash("md5", "HASH-VALUE".getBytes(), null),
					"rsa-pkcs1-md5",
					"SIGNATURE-VALUE".getBytes());
			try {
				certs.add(new Certificate(c, s));
			} catch (java.security.cert.CertificateException e) {
				throw new Error(e);
			}
		}
		try {
			store =
				java.security.cert.CertStore.getInstance(
					"SPKI",
					new CollectionCertStoreParameters(certs));
		} catch (InvalidAlgorithmParameterException e) {
			throw new Error(e);
		} catch (NoSuchAlgorithmException e) {
			throw new Error(e);
		}
	}

	/**
	 * Returns the cert store of this <code>Loader</code>.
	 * 
	 * @return the cert store of this <code>Loader</code>.
	 */
	public java.security.cert.CertStore getCertStore() {
		return store;
	}

	/**
	 * Returns the keys of this <code>Loader</code>.
	 * 
	 * @return the keys of this <code>Loader</code>.
	 */
	public Collection getKeys() {
		return keys.values();
	}

	/**
	 * Returns the certs of this <code>Loader</code>.
	 * 
	 * @return the certs of this <code>Loader</code>.
	 */
	public Set getCerts() {
		return certSet;
	}

	/**
	 * Returns the names of this <code>Loader</code>.
	 * 
	 * @return the names of this <code>Loader</code>.
	 */
	public Set getNames() {
		return nameSet;
	}

	/**
	 * Returns the tags of this <code>Loader</code>.
	 * 
	 * @return the tags of this <code>Loader</code>.
	 */
	public Set getTags() {
		return tagSet;
	}

	/**
	 * For a given string: looks if the key is already saved in the loader
	 * or creates a new one from the bytes stored in the given key (assuming
	 * <code>MD5/RSA/PKCS#1</code> format).
	 * 
	 * @param  id key to add to the keys of this <code>Loader</code> if not 
	 *         already stored (assumes <code>MD5/RSA/PKCS#1</code> format).
	 * @return either the key already stored or the new key added to the keys.
	 */
	private Principal getPrincipal(String id) {
		RSAPublicKey k = (RSAPublicKey) keys.get(id);
		if (k == null) {
			k =
				new RSAPublicKey(
					new BigInteger(id.getBytes()),
					new BigInteger(new byte[] { 0x03 }),
					"MD5/RSA/PKCS#1",
					(URI[])null);
			keys.put(id, k);
		}
		return k;
	}
}
