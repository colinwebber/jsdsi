/*
 * Created on Feb 24, 2004
 *
 */
package jsdsi.xml;

import jsdsi.sexp.*;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * @author Sameer Ajmani
 **/
public class XmlReader extends FilterReader
    implements SexpInput
{
    private static class Handler extends DefaultHandler
    {
        private static class ListMaker {
            private String type;
            private List l = new ArrayList();
            public ListMaker(final String t) {
            	type = t;
            }
            public void add(Sexp s) {
                l.add(s);
            }
            public Sexp toSexp()
            {
            	if (type.startsWith(XmlWriter.starTagPrefix)) {
            		if (type.substring(XmlWriter.starTagPrefix.length()).length()
							== 0) {
            			return new SexpList(new SexpString("*"),
            				(Sexp[])l.toArray(new Sexp[0]));
            		}
            		ArrayList l2 = new ArrayList();
            		l2.add(new SexpString
            				(type.substring(XmlWriter.starTagPrefix.length())));
            		l2.addAll(l);
            		return new SexpList(new SexpString("*"),
            			(Sexp[])l2.toArray(new Sexp[0]));
            	}
            	return new SexpList(new SexpString(type),
            			(Sexp[])l.toArray(new Sexp[0]));
            }
        }
        StringBuffer buf = new StringBuffer();
        Stack stack = new Stack();
        Sexp sexp = null;
        public Handler()
        {
        }
        public Sexp toSexp()
        {
            return sexp;
        }
        public void characters(char[] ch,
                               int start,
                               int length)
            throws SAXException
        {
            buf.append(ch, start, length);
        }
        private void consumeBuffer()
            throws SAXException
        {
            if (buf.length() == 0 ) return;
            if (stack.empty()) return;
            ListMaker top = (ListMaker)stack.peek();
            SexpInputStream in = new SexpInputStream
                (new ByteArrayInputStream
                 (Sexp.encodeString(buf.toString())));
            try {
                while (true) {
                    top.add(in.readSexp());
                }
            } catch (EOFException e) {
                // ok
            } catch (IOException e) {
                throw new SAXException(e);
            } catch (SexpException e) {
                throw new SAXException(e);
            } finally {
                buf = new StringBuffer();
            }
        }
        public void startElement(String uri,
                                 String localName,
                                 String qName,
                                 Attributes attributes)
            throws SAXException
        {
            consumeBuffer();
            ListMaker top = new ListMaker(qName);
            stack.push(top);
        }
        public void endElement(String uri,
                               String localName,
                               String qName)
            throws SAXException
        {
            consumeBuffer();
            ListMaker top = (ListMaker)stack.pop();
            if (stack.empty()) {
                sexp = top.toSexp();
            } else {
                ((ListMaker)stack.peek()).add(top.toSexp());
            }
        }
    }
    private InputSource is;
    private SAXParser p;
	/**
	 * @param in
	 */
	public XmlReader(Reader in)
    {
		super(in);
        is = new InputSource(in);
        try {
            p = SAXParserFactory.newInstance().newSAXParser();
        } catch (SAXException e) {
            throw new Error(e);
        } catch (ParserConfigurationException e) {
            throw new Error(e);
        }
	}
    public Sexp readSexp() throws IOException
    {
        try {
            Handler handler = new Handler();
            p.parse(is, handler);
            return handler.toSexp();
        } catch (SAXException e) {
            throw (IOException)new IOException().initCause(e);
        }
    }
}
