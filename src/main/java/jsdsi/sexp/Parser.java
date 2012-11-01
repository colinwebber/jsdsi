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
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsdsi.Obj;

/**
 * Parses S-expressions into SPKI/SDSI objects.
 * 
 * @author Sameer Ajmani
 * @version $Revision: 1.1 $ $Date: 2004/02/28 15:49:34 $
 */
public class Parser {
	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("usage: java Parser input-file");
			return;
		}
		ObjInputStream is = new ObjInputStream(new FileInputStream(args[0]));
		ObjOutputStream os = new ObjOutputStream(System.out);

		int i = 0;
		try {
			while (true) {
				Obj o = is.readObj();
				//+debug
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				Sexp s = o.toSexp();
				oos.writeObject(s);
				oos.flush();
				s =
					(Sexp) (new ObjectInputStream(new ByteArrayInputStream(bos
						.toByteArray())))
						.readObject();
				//-debug
				os.writeReadable(o, 0, 76, 0);
				os.write('\n');
				i++;
			}
		} catch (SexpParseException e) {
			System.err.println("sexp #" + i);
			throw e;
		} catch (EOFException e) {
			os.flush();
			System.err.println("done");
		}
	}
}