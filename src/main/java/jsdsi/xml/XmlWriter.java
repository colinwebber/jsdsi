/*
 * Created on Feb 24, 2004
 *
 */
package jsdsi.xml;

import jsdsi.sexp.Sexp;
import jsdsi.sexp.SexpList;
import jsdsi.sexp.SexpOutput;
import jsdsi.sexp.SexpString;
import java.io.FilterWriter;
import java.io.Writer;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Sameer Ajmani
 **/
public class XmlWriter extends FilterWriter
    implements SexpOutput
{
	static final String starTagPrefix = "star";
	private static final String indentation = "  ";
	private static final int width = 72;
	/**
	 * @param out
	 */
	public XmlWriter(Writer out) {
		super(out);
	}
	public  void writeSexp(Sexp o) throws IOException {
		write(o, 0);
	}
	private static int indentSize(int depth) {
		return depth * indentation.length();
	}
	private void indent(int depth) throws IOException {
		for (int i = 0; i < depth; i++) {
			write(indentation);
		}
	}
	private void write(String s, int depth) throws IOException {
		indent(depth);
		out.write(s);
	}
	private void write(Sexp o, int depth) throws IOException {
		if (o instanceof SexpList) {
			SexpList l = (SexpList)o;
			Iterator i = l.iterator();
			i.next(); // skip type
			String type = l.getType();
			if (type.startsWith("*")){
				if (type.equals("*") && i.hasNext()) {
					// (* set ...)
					type = starTagPrefix + i.next().toString();
				} else {
					// (*) or (*set ...) [no space]
					type = starTagPrefix + type.substring(1);
				}
			}
			if (!i.hasNext()) {
				write("<"+type+"/>\n", depth);
				return;
			}
			write("<"+type+">\n", depth);
			while (i.hasNext()) {
				write((Sexp)i.next(), depth+1);
			}
			write("</"+type+">\n", depth);
		} else {
			SexpString s = (SexpString)o;
			indent(depth);
			s.writeReadable(out, indentSize(depth), width, 0);
			out.write("\n");
		}
	}
}
