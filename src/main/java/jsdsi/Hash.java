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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.DigestUtils;

/**
 * A cryptographic hash.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.9 $ $Date: 2004/11/08 12:08:08 $
 * 
 * @todo add verify method(s)
 */
public class Hash extends Obj {
    
    private static final long serialVersionUID = -1318052205486635960L;
    
	/**
	 * Hash algorithm.
	 */
	private transient final DigestAlgoEnum digest;

	/**
	 * Hash value.
	 */
	private transient final byte[] data;

	/**
	 * URIs of the hashed data.
	 */
	private transient final URI[] uris;

	/**
	 * Creates a new <code>Hash</code>.
	 * 
	 * @param  a hash algorithm
	 * @param  d hash value (<i>not</i> the data to hash)
	 * @param  u URIs of the hashed data (may be <code>null</code>).
	 * 
	 */
	public Hash(DigestAlgoEnum a, byte[] d, URI[] u) {
		assert(a != null) : "null algo";
		assert(d != null) : "null data";
		this.digest = a;
		this.data = d;
		this.uris = u;
	}
	
	/**
	 * Creates a new <code>Hash</code>.
	 * 
	 * @param  a hash algorithm.
	 * @param  o JSDSI object to hash.
	 * @param  u URIs of the hashed data (may be <code>null</code>).
	 * 
	 */
	public Hash(DigestAlgoEnum a, Obj o, URI[] u) {
		assert(a != null) : "null algo";
		assert(o != null) : "null object";
		this.digest = a;
		this.data = DigestUtils.getDigest(a).digest(o.toByteArray());
		this.uris = u;
	}
	
	/**
	 * Creates a new <code>Hash</code>.
	 * 
	 * @param  a SPKI hash algorithm name
	 * @param  d hash value (<i>not</i> the data to hash)
	 * @param  u URIs of the hashed data
	 * 
	 * @deprecated use {@link #Hash(jsdsi.util.DigestAlgoEnum, byte[], URI[])}
	 */
	public Hash(String a, byte[] d, URI[] u) {
	    this(DigestAlgoEnum.fromSpki(a), d, u);
	}

	/**
	 * Creates a new <code>Hash</code>.
	 * 
	 * @param  a hash algorithm name.
	 * @param  o object to hash.
	 * @param  u URIs of the hashed data.
	 * 
	 * @deprecated use {@link #Hash(jsdsi.util.DigestAlgoEnum, Obj, URI[])}
	 */
	public Hash(String a, Obj o, URI[] u) {
	    this(DigestAlgoEnum.fromSpki(a), o, u);
	}
    
 	/**
	 * Creates a new <code>Hash</code> from an algorithm name and a byte array
	 * that contains a hash value.
	 * 
	 * @param  a name of the hash algorithm used.
	 * @param  d the value of the hash  (<i>not</i> the data to hash).
	 * 
	 * @deprecated use {@link #Hash(jsdsi.util.DigestAlgoEnum, byte[], java.net.URI[])}
	 */
	public Hash(String a, byte[] d) {
		this(a, d, null);
	}

	/**
	 * Creates a new <code>Hash</code> from an algorithm name and an object.
	 * 
	 * @param  a name of the hash algorithm used.
	 * @param  o JSDSI object to calculate the hash value from.
	 * 
	 * @deprecated use {@link #Hash(jsdsi.util.DigestAlgoEnum, jsdsi.Obj, java.net.URI[])}
	 */
	public Hash(String a, Obj o) {
		this(a, o, null);
	}

	
	/**
	 * Creates a new <code>Hash</code> using a given algorithm name, a 
	 * value to hash, and an array of URIs.
	 * 
	 * @param  a hash algorithm
	 * @param  d data to hash
	 * @param  u URIs of the data to hash
	 * @return the created Hash object (may be <code>null</code>)
	 */
    public static Hash create(DigestAlgoEnum a, byte[] d, URI[] u) {
		return create(a, new ByteArrayInputStream(d), u);
    }
    
    /**
     * Creates a new <code>Hash</code> of the data from the given InputStream, using the given algorithm
     * @param  a hash algorithm
	 * @param  is data to hash
	 * @param  u URIs of the data being hashed (may be <code>null</code>)
     * @return the created Hash object
     */
    public static Hash create(DigestAlgoEnum a, InputStream is, URI[] u) {
        assert(a != null) : "null algo";
		assert(is != null) : "null data";
		MessageDigest digester = DigestUtils.getDigest(a);
		Iterator it = new jsdsi.util.InputStreamIterator(is);
		while (it.hasNext()) {
		    digester.update( (byte[]) it.next() );
		}
		byte[] value = digester.digest();
		return new Hash(a, value, u);		
    }    
	
	/**
	 * Creates a new <code>Hash</code> using a given algorithm name, a 
	 * value to hash, and an array of URIs.
	 * 
	 * @param  a hash algorithm name
	 * @param  d data to hash
	 * @param  u URIs of the data to hash
	 * @return the created Hash object
	 * 
	 * @deprecated use {@link Hash#create(jsdsi.util.DigestAlgoEnum, byte[], java.net.URI[])}
	 */
    public static Hash create(String a, byte[] d, URI[] u) throws NoSuchAlgorithmException {
		return create(DigestAlgoEnum.fromJdk(a), new ByteArrayInputStream(d), u);
    }
    
    /**
     * Creates a new <code>Hash</code> of the data from the given InputStream, using the given algorithm
     * @param  a hash algorithm name
	 * @param  is data to hash
	 * @param  u URIs of the data being hashed
     * @return the created Hash object
     * 
     * @deprecated use {@link Hash#create(jsdsi.util.DigestAlgoEnum, java.io.InputStream, java.net.URI[])}
     */
    public static Hash create(String a, InputStream is, URI[] u) throws NoSuchAlgorithmException {
        return create(DigestAlgoEnum.fromJdk(a), is, u);	
    }

    /**
     * @return the digest for this Hash
     */
    public DigestAlgoEnum getDigest() {
        return this.digest;
    }
    
	/**
	 * Returns the JDK name of the hash algorithm.
	 * 
	 * @return the name of the hash algorithm.
	 */
	public String getAlgorithm() {
		return this.digest.jdkName();
	}

	/**
	 * Returns the hash value.
	 * 
	 * @return the hash value.
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * Returns the URIs of the hashed data.
	 * 
	 * @return the URIs of the hashed data.
	 */
	public URI[] getURIs() {
		return uris;
	}

	public boolean equals(Object o) {
		if (o instanceof Hash) {
			Hash h = (Hash) o;
			return this.digest.equals(h.digest)
				&& java.util.Arrays.equals(data, h.data)
				&& Util.equals(uris, h.uris);
		}
		return false;
	}

	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.digest.hashCode() ^ Util.hashCode(this.data) ^ Util.hashCode(this.uris);
	}

	public SexpList toSexp() {
		List l = new ArrayList(3);
		l.add(SexpUtil.toSexp(this.digest.spkiName()));
		l.add(SexpUtil.toSexp(getData()));
		if (getURIs() != null) {
			l.add(SexpUtil.toSexp(getURIs()));
		}
		return SexpUtil.toSexp("hash", l);
	}

	public static Hash parseHash(SexpList l) throws SexpParseException {
		Iterator hbody = SexpUtil.getBody(l);
		String algo = SexpUtil.getNextString(hbody, "hash algo");
		DigestAlgoEnum digest = calculateDigestEnum(algo);
		byte[] data = SexpUtil.getNextByteArray(hbody, "hash data");
		SexpUtil.checkDone(hbody, "hash"); // TODO: support URIs
		return new Hash(digest, data, null);
	}
	
	/**
	 * Temporary method to cope with legacy certificates/signatures which incorrectly have
	 * JDK algorythm names in their SExpression.
	 * @param algo
	 * @return
	 * @todo Method should be removed in the future when legacy data is no longer an issue 
	 */
	static DigestAlgoEnum calculateDigestEnum(String algo) {
	    try {
            return DigestAlgoEnum.fromSpki(algo);
        } catch (JsdsiRuntimeException e) {
            // was not a SPKI algo name, so was it a legacy JDK one...
            return DigestAlgoEnum.fromJdk(algo);
        }
	}
}
