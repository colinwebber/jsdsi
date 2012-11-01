/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi;

import java.util.Iterator;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A tag that matches strings that prefix its own string value, i.e.
 * (tag (* reverse-prefix /my/private)) implies (tag /my) but not (tag /my/private/file)
 * 
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/08 12:08:08 $
 *
 */
public class ReversePrefixTag extends ExprTag {
    
    private static final long serialVersionUID = -1771391400337244600L;

	/**
	 * The value
	 */
	private transient final String value;
	
	/**
	 * Creates a new <code>ReversePrefixTag</code> with a given prefix string.
	 * 
	 * @param value the string to match against.
	 */
	public ReversePrefixTag(String value) {
		assert(value != null) : "null value";
		this.value = value;
	}
	
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(Tag that)
	{
		if (that instanceof ReversePrefixTag) {
			return intersect((ReversePrefixTag)that);
		}
		if (that instanceof PrefixTag) {
			return intersect((PrefixTag)that);
		}
		if (that instanceof StringTag) {
			return intersect((StringTag)that);
		}
		if (that instanceof SetTag) {
			return that.intersect(this);
		}
		return Tag.NULL_TAG;
	}
	
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(ReversePrefixTag that)
	{
		if (this.value.startsWith(that.value)) {
			return that;
		}
		if (that.value.startsWith(this.value)) {
			return this;
		}
		return Tag.NULL_TAG;
	}
	
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(PrefixTag that)
	{
		if (this.value.startsWith(that.getPrefix())==false) {
			return Tag.NULL_TAG;
		}
		int diff = indexOfDifference(value, that.getPrefix());
		if (diff==-1) {
			// values are equals
			return new StringTag(value);
		} 
		// ReversePrefix startswith Prefix, so need a Set of Strings from Prefix to ReversePrefix
		// (* reverse-prefix /my/pub) intersect (* prefix /my)
		// is (* set /my /my/ /my/p /my/pu /my/pub)
		int num = value.length() - diff +1; // number in Set
		ExprTag[] eTags = new ExprTag[num];
		for (int index = 0; index<num; index++) {
			eTags[index] = new StringTag(value.substring(0, diff + index));
		}
		return new SetTag( eTags );
	}
	
	/**
	 * Compares two Strings, and returns the index at which the Strings begin to differ.
	 * 
	 * @param str1
	 * @param str2
	 * @return the index where str2 and str1 begin to differ; -1 if they are equal
	 */
	private static int indexOfDifference(String str1, String str2) {
		if (str1 == str2) {
			return -1;
		}
		if (str1 == null || str2 == null) {
			return 0;
		}
		int i;
		for (i = 0; i < str1.length() && i < str2.length(); ++i) {
			if (str1.charAt(i) != str2.charAt(i)) {
				break;
			}
		}
		if (i < str2.length() || i < str1.length()) {
			return i;
		}
		return -1;
	}
	
	
	/**
	 * @see jsdsi.Tag#intersect(Tag)
	 */
	public Tag intersect(StringTag that)
	{
		if (this.value.startsWith(that.getValue())) {
			return that;
		}
		return Tag.NULL_TAG;
	}
	
	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object that) {
		return (that instanceof ReversePrefixTag)
		&& this.value.equals(((ReversePrefixTag) that).value);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value.hashCode();
	}

	/**
	 * Returns the value of this tag.
	 * 
	 * @return the value of this tag.
	 */
	public String getValue() {
		return value;
	}

	public Sexp toTagSexp() {
		Sexp[] ss = new Sexp[2];
		ss[0] = SexpUtil.toSexp("reverse-prefix");
		ss[1] = SexpUtil.toSexp(getValue());
		return SexpUtil.toSexp("*", ss);
	}

	static ReversePrefixTag parseReversePrefixTag(Iterator tbody) throws SexpParseException {
		String p = SexpUtil.getNextString(tbody, "reverse-prefix tag");
		SexpUtil.checkDone(tbody, "reverse-prefix tag");
		return new ReversePrefixTag(p);
	}
}
