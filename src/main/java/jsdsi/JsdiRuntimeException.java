/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi;


/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/06/09 16:30:08 $
 * 
 * Superclass for JSDSI Exceptions. Extending <code>RuntimeException</code> was chosen as in the vast
 * majority of scenarios, the calling application cannot recover. Whether, a seperate JsdsiException that
 * decends <code>Exception</code> is also needed needs to be finalised.
 * 
 * @deprecated This class is incorrectly spelt. Use {@link JsdsiRuntimeException} instead.
 */
public class JsdiRuntimeException extends RuntimeException {

    /**
     * 
     */
    public JsdiRuntimeException() {
        super();
    }

    /**
     * @param message
     */
    public JsdiRuntimeException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public JsdiRuntimeException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public JsdiRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
