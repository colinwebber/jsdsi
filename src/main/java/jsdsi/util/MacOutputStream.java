/*
 * Copyright �, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.crypto.Mac;

/**
 * @author Sean Radford
 * @version $Revision: 1.1 $ $Date: 2005/02/17 16:47:14 $
 *  
 */
public class MacOutputStream extends FilterOutputStream {

    protected javax.crypto.Mac mac;

    public MacOutputStream(OutputStream stream, Mac mac) {
        super(stream);
        this.mac = mac;
    }

    public void write(int b) throws IOException {
        mac.update((byte) b);
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        mac.update(b, off, len);
        out.write(b, off, len);
    }

    public Mac getMac() {
        return mac;
    }

}