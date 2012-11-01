/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;

import jsdsi.JsdsiRuntimeException;


/**
 * <p>Experimental class and as such may be removed without warning.</p>
 * Utility class for Signatures.
 * 
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 */
public class SignatureUtils {

    /**
     * 
     */
    private SignatureUtils() {
        super();
    }

    /**
     * Return a java.security.Signature. The required algorythm is usually generated from {@link Algorithms}:
     * <pre>
     * 	Algorithms.jdkSignatureName(DigestAlgoEnum.XXX, KeyEnum.XXX)
     * </pre>
     * @param jdkSignatureAlgo signature algorythm required in JDK format
     * @return the Signature
     * @throws jsdsi.JsdsiRuntimeException on error
     */
    public static Signature getJdkSignature(String jdkSignatureAlgo) {
        return getJdkSignature(jdkSignatureAlgo, null);
    }
    
    /**
     * Return a java.security.Signature. The required algorythm is usually generated from {@link Algorithms}:
     * <pre>
     * 	Algorithms.jdkSignatureName(DigestAlgoEnum.XXX, KeyEnum.XXX)
     * </pre>
     * @param jdkSignatureAlgo signature algorythm required in JDK format
     * @param provider the Provider to use
     * @return the Signature
     * @throws jsdsi.JsdsiRuntimeException on error
     */
    public static Signature getJdkSignature(String jdkSignatureAlgo, String provider) {
        if (jdkSignatureAlgo == null || jdkSignatureAlgo.equals("")) {
            throw new IllegalArgumentException("jdkSignatureAlgo is NULL");
        }
        try {
            if (provider==null) {
                return Signature.getInstance(jdkSignatureAlgo);
            } else {
                return Signature.getInstance(jdkSignatureAlgo, provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new JsdsiRuntimeException("No such Signature algorythm: " + jdkSignatureAlgo);
        } catch (NoSuchProviderException e) {
            throw new JsdsiRuntimeException("No such Signature provider: " + provider);
        }
    }
    
    /**
     * Returns the MessageDigester for a JDK Signature algorythm
     * @param jdkSignatureAlgo the Signature algorythm in JDK format
     * @return the MessageDigest to use
     */
    public static MessageDigest getDigesterForJdkSignature(String jdkSignatureAlgo) {
        if (jdkSignatureAlgo == null || jdkSignatureAlgo.equals("")) {
            throw new IllegalArgumentException("jdkSignatureAlgo is NULL");
        }
        int pos = jdkSignatureAlgo.indexOf("with");
        if (pos==-1) {
            throw new JsdsiRuntimeException("Illegal jdkSignatureAlgo. No 'with' found: "+jdkSignatureAlgo);
        }
        String jdkName = jdkSignatureAlgo.substring(0, pos);
        DigestAlgoEnum enum = DigestAlgoEnum.fromJdkSignature(jdkName);
        return DigestUtils.getDigest(enum);
    }
    
    
}
