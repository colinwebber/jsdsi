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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Static utility methods.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.6 $ $Date: 2005/02/17 16:47:14 $
 */
public class Util {
	/**
	 * Returns <code>true</code> iff both parameters are <code>null</code> or 
	 * they are equals().
	 * 
	 * @param  a object to compare with <code>b</code>.
	 * @param  b object to compare with <code>a</code>.
	 * @return <code>true</code> iff both parameters are <code>null</code> or 
	 *         they are equals(), <code>false</code> otherwise.
	 */
	public static boolean equals(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if ((a instanceof Object[]) && (b instanceof Object[])) {
			return equals((Object[]) a, (Object[]) b);
		} else if ((a instanceof byte[]) && (b instanceof byte[])) {
		    return equals((byte[])a, (byte[])b);
		}
		return a.equals(b);
	}

	/**
	 * Returns <code>true</code> iff both parameters are <code>null</code> or 
	 * if elements are equals().
	 * 
	 * @param  a array of objects to compare with <code>b</code>.
	 * @param  b array of objects to compare with <code>a</code>.
	 * @return <code>true</code> if both parameters are <code>null</code> or 
	 *         if elements are equals(), <code>false</code> otherwise.
	 */
	public static boolean equals(Object[] a, Object[] b) {
		if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		for (int i = 0; i < a.length; i++) {
			if (!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param a array of bytes to compare with <code>b</code>
	 * @param b array of bytes to compare with <code>a</code>
	 * @return <code>true</code> if both parameters are <code>null</code> or if the elements are equal,
	 * <code>false</code> otherwise.
	 */
	public static boolean equals(byte[] a, byte[] b) {
	    if (a == null && b == null) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		if (a.length != b.length) {
			return false;
		}
		for (int i=0; i<a.length; i++) {
		    if (a[i] != b[i]) {
		        return false;
		    }
		}
	    return true;
	}
	
	/**
	 * Returns the hash code of an object (which also can be an object array).
	 * 
	 * @param  o object to return the hash-value for.
	 * @return the hash-value for <code>o</code>.
	 */
	public static int hashCode(Object o) {
		if (o == null) {
			return 0;
		}
		if (o instanceof Object[]) {
			return hashCode((Object[]) o);
		} else if (o instanceof byte[]) {
		    return hashCode((byte[])o);
		}
		return o.hashCode();
	}

	/**
	 * Returns the hash-value for an array of objects.
	 * 
	 * @param  oa object array to create the hash-value from.
	 * @return the hash-value of <code>oa</code>.
	 */
	public static int hashCode(Object[] oa) {
		if (oa == null) {
			return 0;
		}
		int hc = 0;
		for (int i = 0; i < oa.length; i++) {
			hc = hc ^ hashCode(oa[i]);
		}
		return hc;
	}
	
	/**
	 * Returns the hash-value for an array of bytes.
	 * Unlike byte[].hashCode(), this will return a hash code based on the actual bytes within
	 * the array, and not the address.
	 * @param ba the byte array
	 * @return the hash code
	 */
	public static int hashCode(byte[] ba) {
		if (ba == null) {
			return 0;
		}
		int hc = 0;
		for (int i = 0; i < ba.length; i++) {
			hc = hc ^ ba[i];
		}
		return hc;
	}
	
	/**
	 * Converts an array of java.net.URLs to an array of java.net.URIs
	 * ?Temporary method whilst removing <code>java.net.URL</code>s from {@link jsdsi.Obj}s.
	 * @param urls
	 * @return
	 */
	static URI[] convert(URL[] urls) {
		if (urls == null) {
			return null;
		}
		URI[] result = new URI[urls.length];
		for (int index=0; index<urls.length; index++) {
			try
			{
				result[index] = new URI( urls[index].toString() );
			} catch (URISyntaxException e)
			{
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
	/**
	 * Converts an array of java.net.URIs to an array of java.net.URLs
	 * ?Temporary method whilst removing <code>java.net.URL</code>s from {@link jsdsi.Obj}s.
	 * @param uris
	 * @return
	 */
	static URL[] convert(URI[] uris) {
		if (uris == null) {
			return null;
		}
		URL[] result = new URL[uris.length];
		for (int index=0; index<uris.length; index++) {
			try
			{
				result[index] = new URL( uris[index].toString() );
			} catch (MalformedURLException e)
			{
				throw new RuntimeException(e);
			}
		}
		return result;
	}
	
}