/*
 * Copyright 2002 Massachusetts Institute of Technology Permission to use, copy, modify, and
 * distribute this program for any purpose and without fee is hereby granted, provided that this
 * copyright and permission notice appear on all copies and supporting documentation, the name of
 * M.I.T. not be used in advertising or publicity pertaining to distribution of the program without
 * specific prior permission, and notice be given in supporting documentation that copying and
 * distribution is by permission of M.I.T. M.I.T. makes no representations about the suitability of
 * this software for any purpose. It is provided "as is" without express or implied warranty.
 */
package jsdsi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpParseException;
import jsdsi.sexp.SexpUtil;

/**
 * A tag that specifies a set of allowed values.
 * <p>
 * <strong>Note: Currently empty and singleton sets are allowed, but will be made illegal in a
 * future release. </strong>
 * </p>
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.13 $ $Date: 2005/01/21 18:04:17 $
 */
public class SetTag extends ExprTag {

    private static final long serialVersionUID = -5653879491817721014L;

    /**
     * Elements in this <code>SetTag</code>.
     */
    private transient final List elements;

    /**
     * Creates a new <code>SetTag</code> for a given array of tags.
     * <p>
     * <strong>Note: Currently empty and singleton sets are allowed, but will be made illegal in a
     * future release. </strong>
     * </p>
     * 
     * @param e array of ExprTags.
     * @throws IllegalArgumentException if <code>e</code> contains less than 2 elements (IN A
     *                  FUTURE RELEASE)
     * @todo check that <code>e</code> does not contain any <code>null</code> s or duplicates
     *           (is a set)
     * @todo check that <code>list</code> does not contain NULL_TAG or ALL_TAG
     * @todo uncomment check for size of array when backwards compatibility is deemed safe
     */
    public SetTag(ExprTag[] e) {
        //		if (e == null || e.length < 2)
        //		{
        //			throw new IllegalArgumentException("element array must contain at least 2 elements");
        //		}
        elements = Arrays.asList(e);
    }

    /**
     * Creates a new <code>SetTag</code> for a given collection of tags.
     * <p>
     * <strong>Note: Currently empty and singleton sets are allowed, but will be made illegal in a
     * future release. </strong>
     * </p>
     * 
     * @param list list of ExprTags.
     * @throws IllegalArgumentException if <code>list</code> contains less than 2 elements (IN A
     *                  FUTURE RELEASE)
     * @todo check that <code>list</code> does not contain any <code>null</code> s or duplicates
     *           (is a set)
     * @todo check that <code>list</code> does not contain NULL_TAG or ALL_TAG
     * @todo uncomment check for size of array when backwards compatibility is deemed safe
     */
    public SetTag(List /* <ExprTag> */list) {
        assert (list != null) : "null elements";
        //		if (list.size() < 2)
        //		{
        //			throw new IllegalArgumentException("list must contain at least 2 elements");
        //		}
        // create our own List from list so as to make this SetTag immutable
        elements = new LinkedList(list);
    }

    /**
     * Creates a new <code>SetTag</code> for a given collection of tags.
     * <p>
     * <strong>Note: Currently empty and singleton sets are allowed, but will be made illegal in a
     * future release. </strong>
     * </p>
     * 
     * @param c collection of ExprTags.
     * @deprecated use {@link #SetTag(List)}
     */
    public SetTag(Collection /* <ExprTag> */c) {
        assert (c != null) : "null elements";
        // create our own List from list so as to make this SetTag immutable
        elements = new LinkedList(c);
    }

    /**
     * If <code>that</code> is a <code>SetTag</code>, returns {@link SetTag#intersect(SetTag)}.
     * Else returns a <code>Tag</code> of the intersections of <code>that</code> with each
     * <code>Tag</code> in this <code>SetTag</code> (if the the result is a single element then
     * that Tag itself is returned.) Otherwise returns NULL_TAG.
     * 
     * @param that
     * @return a <code>SetTag</code>,<code>ExprTag</code>, or <code>Tag.NULL_TAG</code>
     *             representing the intersection.
     * @see jsdsi.Tag#intersect(Tag)
     */
    public Tag intersect(Tag that) {
        if (that instanceof SetTag) {
            return intersect((SetTag) that);
        }
        LinkedList newTags = new LinkedList();
        for (Iterator i = this.elements.iterator(); i.hasNext();) {
            ExprTag thisTag = (ExprTag) i.next();
            Tag newTag = thisTag.intersect(that);
            if (newTag instanceof ExprTag) {
                // excludes ALL_TAG and NULL_TAG
                newTags.add(newTag);
            }
        }
        if (newTags.isEmpty()) {
            return Tag.NULL_TAG;
        } else {
            if (newTags.size() > 1) {
                reduce(newTags);
            }
            if (newTags.size() > 1) {
                return new SetTag(newTags);
            } else {
                return (Tag) newTags.iterator().next();
            }
        }
    }

    /**
     * Removes duplicate tags and tags implied by others in the list (ensures that <code>tags</code>
     * is a mathematical set).
     * 
     * @param tags
     */
    private void reduce(List tags) {
        int i = 0;
        while (i < tags.size()) {
            for (int j = tags.size() - 1; j >= 0; j--) {
                Tag iTag = (Tag) tags.get(i);
                Tag jTag = (Tag) tags.get(j);
                if (iTag != jTag) {
                    if (iTag.equals(jTag) || iTag.implies(jTag)) {
                        tags.remove(j);
                    }
                }
            }
            i++;
        }
    }

    /**
     * Returns a new Tag whose elements are the all-pairs intersections of the elements of
     * <code>this</code> and <code>that</code>, excluding non-ExprTags such as NULL_TAG and
     * ALL_TAG.
     * 
     * @param that
     * @return the intersection
     */
    public Tag intersect(SetTag that) {
        LinkedList tags = new LinkedList();
        for (Iterator i = this.elements.iterator(); i.hasNext();) {
            ExprTag thisTag = (ExprTag) i.next();
            for (Iterator j = that.elements.iterator(); j.hasNext();) {
                ExprTag thatTag = (ExprTag) j.next();
                Tag newTag = thisTag.intersect(thatTag);
                // exclude ALL_TAG and NULL_TAG
                if (newTag instanceof ExprTag) {
                    // check to make sure we don't have the newTag already (we will only if a SetTag
                    // is malformed, i.e. not a true mathematical set)
                    if (!tags.contains(newTag)) {
                        tags.add(newTag);
                    }
                }
            }
        }
        int len = tags.size();
        if (len == 0) {
            return Tag.NULL_TAG;
        } else if (len == 1) {
            return (Tag) tags.get(0);
        } else {
            return new SetTag(tags);
        }
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object that) {
        if (that instanceof SetTag) {
            SetTag other = (SetTag) that;
            if (elements.size() == other.getElements().length) {
                Iterator it = elements.iterator();
                while (it.hasNext()) {
                    if (other.contains((ExprTag) it.next()) == false) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the index in this list of the first occurrence of the specified tag, or -1 if this
     * list does not contain this tag.
     * 
     * @param tag tag to search for.
     * @return the index in this list of the specified tag, or -1 if this SetTag does not contain
     *             this tag.
     */
    public int indexOf(ExprTag tag) {
        return elements.indexOf(tag);
    }

    /**
     * Returns <code>true</code> if this SetTag contains the specified tag.
     * 
     * @param tag tag whose presence in this list is to be tested.
     * @return <code>true</code> if this list contains the specified tag.
     */
    public boolean contains(ExprTag tag) {
        return elements.contains(tag);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return elements.hashCode();
    }

    /**
     * Returns the tags of this <code>SetTag</code>.
     * 
     * @return the tags of this <code>SetTag</code>.
     */
    public ExprTag[] getElements() {
        return (ExprTag[]) elements.toArray(new ExprTag[0]);
    }

    /**
     * @see jsdsi.Tag#toTagSexp()
     */
    public Sexp toTagSexp() {
        Sexp[] ss = new Sexp[elements.size() + 1];
        ss[0] = SexpUtil.toSexp("set");
        int i = 1;
        for (Iterator es = elements.iterator(); es.hasNext();) {
            ss[i] = ((ExprTag) es.next()).toTagSexp();
            i++;
        }
        return SexpUtil.toSexp("*", ss);
    }

    /**
     * @param tbody
     * @return
     * @throws SexpParseException
     */
    static SetTag parseSetTag(Iterator tbody) throws SexpParseException {
        ArrayList l = new ArrayList();
        while (tbody.hasNext()) {
            l.add(ExprTag.parseExprTag(SexpUtil.getNext(tbody, "set tag")));
        }
        SexpUtil.checkDone(tbody, "set tag"); // sanity check
        return new SetTag(l);
    }
}