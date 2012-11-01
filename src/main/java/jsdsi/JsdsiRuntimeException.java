/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi;


/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 * 
 * Superclass for JSDSI Exceptions. Extending <code>RuntimeException</code> was chosen as in the vast
 * majority of scenarios, the calling application cannot recover. Whether, a separate JsdsiException that
 * descends <code>Exception</code> is also needed needs to be finalised.
 */
public class JsdsiRuntimeException extends RuntimeException {

    /**
     * 
     */
    public JsdsiRuntimeException() {
        super();
    }

    /**
     * @param message
     */
    public JsdsiRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JsdsiRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JsdsiRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
