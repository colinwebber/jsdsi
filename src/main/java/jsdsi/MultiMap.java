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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A one-to-many map: each key is associated with a set of values.  Note
 * that get(key) returns the empty set if no value has previously been
 * put() for that key.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
class MultiMap {
	/**
	 * Stores the key -> Set bindings.
	 */
	Map map = new HashMap();

	/**
	 * Returns the <code>Set</code> for a given key. If the key hasn't been
	 * previously inserted with the put-method an empty set is returned.
	 * 
	 * @see #put(Object, Object)
	 * 
	 * @param  key key so return the set for.
	 * @return the set that has been added with key <code>key</code>
	 *         previously.
	 */
	public Set get(Object key) {
		Set set = (Set) map.get(key);
		if (set == null) {
			set = new HashSet();
			map.put(key, set);
		}
		return set;
	}

	/**
	 * Adds a <code>Set</code> for a given key to this <code>MultiMap</code>.
	 * 
	 * @param  key key to add the set for.
	 * @param  value set to add with key <code>key</code>.
	 */
	public void put(Object key, Object value) {
		get(key).add(value);
	}

	/**
	 * Adds the elements of a given collection to the set for a given key in 
	 * this <code>MultiMap</code>.
	 * 
	 * @param  key key to add the collections for.
	 * @param  coll collection to add for <code>key</code>.
	 */
	public void putAll(Object key, Collection coll) {
		get(key).addAll(coll);
	}

	/**
	 * Remove an object from the set added with the key <code>key</code>.
	 * 
	 * @param  key key of set to remove <code>value</code> from.
	 * @param  value value to remove from the set that has been put with key
	 *         <code>key</code>.
	 */
	public void remove(Object key, Object value) {
		get(key).remove(value);
	}
}