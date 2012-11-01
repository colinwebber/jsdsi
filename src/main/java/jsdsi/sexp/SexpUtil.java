/*
 * Copyright 2002 Massachusetts Institute of Technology
 * 
 * Permission to use, copy, modify, and distribute this program for any purpose
 * and without fee is hereby granted, provided that this copyright and
 * permission notice appear on all copies and supporting documentation, the
 * name of M.I.T. not be used in advertising or publicity pertaining to
 * distribution of the program without specific prior permission, and notice be
 * given in supporting documentation that copying and distribution is by
 * permission of M.I.T. M.I.T. makes no representations about the suitability
 * of this software for any purpose. It is provided "as is" without express or
 * implied warranty.
 */
package jsdsi.sexp;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * Utilities for converting SDSI objects to S-expression objects.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/08/25 23:18:11 $
 */
public class SexpUtil {
    // Utilities to support toSexp()

    public static SexpList toSexp(String type, Sexp[] ss) {
        return new SexpList(new SexpString(type), ss);
    }

    public static SexpList toSexpList(String type) {
        return new SexpList(new SexpString(type), new Sexp[0]);
    }

    public static SexpList toSexp(String type, List l) {
        Sexp[] ss = new Sexp[l.size()];
        l.toArray(ss);
        return toSexp(type, ss);
    }

    public static SexpString toSexp(String s) {
        return new SexpString(s);
    }

    public static SexpString toSexp(byte[] b) {
        return new SexpString(b);
    }

    /**
     * @param u
     * @return @deprecated use {@link #toSexp(URI[])}
     */
    public static SexpList toSexp(URL[] u) {
        Sexp[] ss = new Sexp[u.length];
        for (int i = 0; i < u.length; i++) {
            ss[i] = toSexp(u[i].toString());
        }
        return toSexp("uri", ss);
    }

    public static SexpList toSexp(URI[] u) {
        Sexp[] ss = new Sexp[u.length];
        for (int i = 0; i < u.length; i++) {
            ss[i] = toSexp(u[i].toString());
        }
        return toSexp("uri", ss);
    }

    public static Sexp toSexpComment(String c) {
        return toSexp("comment", new Sexp[] { toSexp(c) });
    }

    public static Sexp toSexpDisplayHint(String d) {
        return toSexp("display", new Sexp[] { toSexp(d) });
    }

    static private java.text.SimpleDateFormat dateFormat;
    static {
        dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        TimeZone utc = TimeZone.getTimeZone("GMT");
        dateFormat.setTimeZone(utc);
    }

    public static SexpString toSexp(Date d) {
        return toSexp(dateFormat.format(d));
    }

    // Utilities to support fromSexp()

    public static Date parseDate(String s) throws SexpParseException {
        try {
            // need to synchronise parsing a Format objects are not thread safe
            synchronized (dateFormat) {
                return dateFormat.parse(s);
            }
        } catch (java.text.ParseException e) {
            throw new SexpParseException(e);
        }
    }

    public static void check(boolean test, String message) throws SexpParseException {
        if (!test) {
            throw new SexpParseException(message);
        }
    }

    public static SexpString getSexpString(Sexp s) throws SexpParseException {
        check(s instanceof SexpString, "expected string");
        return (SexpString) s;
    }

    public static byte[] getByteArray(Sexp s) throws SexpParseException {
        return getSexpString(s).toByteArray();
    }

    public static String getString(Sexp s) throws SexpParseException {
        return getSexpString(s).toString();
    }

    public static void checkType(SexpList l, String type) throws SexpParseException {
        check(type.equals(l.getType()), "expected type " + type);
    }

    public static SexpList getList(Sexp s, String type) throws SexpParseException {
        check(s instanceof SexpList, "expected list");
        SexpList l = (SexpList) s;
        if (type != null) {
            checkType(l, type);
        }
        return l;
    }

    public static SexpList getList(Sexp s) throws SexpParseException {
        return getList(s, null);
    }

    public static Iterator getBody(SexpList l) {
        // assumes list type has been checked
        // get iterator for list after type
        Iterator it = l.iterator();
        it.next(); // skip type
        return it;
    }

    public static Sexp getNext(Iterator i, String message) throws SexpParseException {
        check(i.hasNext(), message);
        return (Sexp) i.next();
    }

    public static byte[] getNextByteArray(Iterator i, String message) throws SexpParseException {
        return getByteArray(getNext(i, message));
    }

    public static String getNextString(Iterator i, String message) throws SexpParseException {
        return getString(getNext(i, message));
    }

    public static SexpList getNextList(Iterator i, String message) throws SexpParseException {
        return getList(getNext(i, message));
    }

    public static SexpList getNextList(Iterator i, String type, String message)
            throws SexpParseException {
        return getList(getNext(i, message), type);
    }

    public static void checkDone(Iterator i, String message) throws SexpParseException {
        if (i.hasNext()) {
            try {
                StringWriter sw = new StringWriter();
                sw.write("unexpected extra fields in " + message + ":\n");
                while (i.hasNext()) {
                    ((Sexp) i.next()).writeReadable(sw, 0, 72, 0);
                }
                check(false, sw.toString());
            } catch (IOException e) {
                throw new Error(e);
            }
        }
    }

    /**
     * @param l
     * @return @throws SexpParseException
     * @deprecated use {@link #parseURIs(SexpList)}
     */
    public static URL[] parseURLs(SexpList l) throws SexpParseException {
        Iterator ubody = getBody(l);
        URL[] urls = new URL[l.size() - 1];
        try {
            for (int i = 0; i < urls.length; i++) {
                urls[i] = new URL(getString(getNext(ubody, "uri")).toString());
            }
        } catch (MalformedURLException e) {
            throw new SexpParseException(e);
        }
        checkDone(ubody, "uris"); // sanity check
        return urls;
    }

    public static URI[] parseURIs(SexpList l) throws SexpParseException {
        Iterator ubody = getBody(l);
        URI[] uris = new URI[l.size() - 1];
        try {
            for (int i = 0; i < uris.length; i++) {
                uris[i] = new URI(getString(getNext(ubody, "uri")).toString());
            }
        } catch (URISyntaxException e) {
            throw new SexpParseException(e);
        }
        checkDone(ubody, "uris"); // sanity check
        return uris;
    }
}