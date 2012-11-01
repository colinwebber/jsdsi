/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import jsdsi.JsdsiRuntimeException;

/**
 * Utility class for MessageDigests
 * 
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/12/18 16:02:42 $
 */
public class DigestUtils {

    /**
     * 
     */
    private DigestUtils() {
        super();
    }

    /**
     * Return a java.security.MessageDigest
     * @param digest a DigestAlgoEnum representing the type of digest sought
     * @return the MessageDigest
     * @throws jsdsi.JsdsiRuntimeException on error
     */
    public static MessageDigest getDigest(DigestAlgoEnum digest) {
        return getDigest(digest, null);
    }
    
    /**
     * Return a java.security.MessageDigest
     * @param digest a DigestAlgoEnum representing the type of digest sought
     * @param provider the Provider to use
     * @return the MessageDigest
     * @throws jsdsi.JsdsiRuntimeException on error
     */
    public static MessageDigest getDigest(DigestAlgoEnum digest, String provider) {
        if (digest == null) {
            throw new IllegalArgumentException("name is NULL");
        }
        try {
            if (provider==null) {
                return MessageDigest.getInstance(digest.jdkName());
            } else {
                return MessageDigest.getInstance(digest.jdkName(), provider);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new JsdsiRuntimeException("No such MessageDigest algorythm: " + digest.jdkName());
        } catch (NoSuchProviderException e) {
            throw new JsdsiRuntimeException("No such MessageDigest provider: " + provider);
        }
    }
    
    /**
     * Calculate and return the MD5 digest of the given data
     * @param bytes data to digest
     * @return the digest
     */
    public static byte[] md5(byte[] bytes) {
       MessageDigest digester = DigestUtils.getDigest(DigestAlgoEnum.MD5);
       return digester.digest(bytes);
    }
    
    /**
     * Calculate and return the SHA1 digest of the given data
     * @param bytes data to digest
     * @return the digest
     */
    public static byte[] sha1(byte[] bytes) {
       MessageDigest digester = DigestUtils.getDigest(DigestAlgoEnum.SHA1);
       return digester.digest(bytes);
    }
    
}
