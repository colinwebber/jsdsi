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

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import jsdsi.sexp.SexpInput;
import jsdsi.sexp.SexpInputStream;
import jsdsi.sexp.SexpOutput;
import jsdsi.sexp.SexpOutputStream;

import jsdsi.xml.XmlReader;
import jsdsi.xml.XmlWriter;

/**
 * Reads a file containing S-expressions or XML and outputs its contents
 * in canonical, readable, transport, or XML format to stdout.
 *
 * XML input currently fails on cache1.xml because XML doesn't allow
 * multiple top-level elements in a file.  Try it on cert.xml instead.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.2 $ $Date: 2004/03/18 14:40:41 $
 */
class S2X {
	public static void main(String[] args) throws Exception
    {
		if (args.length < 3) {
			System.err.println("usage: java S2X input-file "
                               + "[sexp|xml] [xml|canon|read|xprt]");
			return;
		}
        FileInputStream fin = new FileInputStream(args[0]);
        final String inFormat = args[1];
        final String outFormat = args[2];
        SexpInput in = null;
        if (inFormat.equals("sexp")) {
            in = new SexpInputStream(fin);
        } else if (inFormat.equals("xml")) {
            in = new XmlReader(new InputStreamReader(fin));
        } else {
            throw new Error("Unrecognized inFormat: "+inFormat);
        }
        SexpOutput out = null;
        if (outFormat.equals("xml")) {
            out = new XmlWriter(new OutputStreamWriter(System.out));
        } else if (outFormat.equals("canon")) {
            out = new SexpOutputStream(System.out).toCanonical();
        } else if (outFormat.equals("read")) {
            out = new SexpOutputStream(System.out).toReadable(0, 72, 0); 
        } else if (outFormat.equals("xprt")) {
            out = new SexpOutputStream(System.out).toTransport();
        } else {
            throw new Error("Unrecognized outFormat: "+outFormat);
        }
        try {
            while (true) {
                out.writeSexp(in.readSexp());
                out.flush();
                System.out.println();
                // looping causes an IOException for xml input,
                // because it only expects one document per file.
                if (inFormat.equals("xml")) break;
            }
        } catch (EOFException e) {
            out.flush();
        }
        System.err.println("done");
	}
}
