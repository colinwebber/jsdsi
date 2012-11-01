/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import jsdsi.JsdsiRuntimeException;


/**
 * Adaptor class to make an InputStream function as an Iterator.
 * 
 * @author Sean Radford
 * @version $Revision: 1.4 $ $Date: 2004/06/09 16:34:41 $
 */
public class InputStreamIterator implements Iterator {

    /**
     * The stream to iterate over
     */
    private InputStream is = null;
    
    /**
     * The bufferSize to use. Defaults to 128 bytes.
     */
    private int bufSize = 128;
    
    /**
     * 
     */
    public InputStreamIterator(InputStream is) {
        super();
        // make sure the input stream supports marks
        if (is.markSupported()) {
            this.is = is;
        } else {
            this.is = new BufferedInputStream(is);
        }
    }
    	
    /**
     * 
     * @param is the InputStream to iterate over
     * @param bufSize the buffer size to return
     */
    public InputStreamIterator(InputStream is, int bufSize) {
        this(is);
        this.bufSize = bufSize;
    }

    /**
     * Not supported by this implementation.
     * @throws UnsupportedOperationException as not supported by this implementation
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        if (true) throw new UnsupportedOperationException("Remove method is not supported by this Iterator");
    }

    /**
     * @see java.util.Iterator#hasNext()
     * @throws JsdsiRuntimeException wrapping any IOException
     */
    public boolean hasNext() {
        int i;
        try {
            this.is.mark(1);
            i = this.is.read();
            this.is.reset();
        } catch (IOException e) {
            throw new JsdsiRuntimeException("Error reading from InputStream", e);
        }
        return i<0 ? false: true;
    }

    /**
     * Returns the a number of bytes as a <code>byte[]</code> from the input stream
     * (up to <code>bufSize</code> in length), or <code>null</code> if the end of the
     * stream has been reached.
     * @see java.util.Iterator#next()
     * @throws JsdsiRuntimeException wrapping any IOException
     */
    public Object next() {
        byte[] bytes = new byte[this.bufSize];
        int len = -1;
        try {
            len = this.is.read(bytes);
        } catch (IOException e) {
            throw new JsdsiRuntimeException("Error reading from InputStream", e);
        }
        if (len==-1) {
            return null;
        } else if (len==this.bufSize) {
            return bytes;
        } else {
            byte[] toReturn = new byte[len];
            System.arraycopy(bytes, 0, toReturn, 0, len);
            return toReturn;
        }
    }

    /**
     * @return the bufSize
     */
    public int getBufSize() {
        return bufSize;
    }
    
}
