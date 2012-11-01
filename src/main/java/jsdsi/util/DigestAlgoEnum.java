/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.util.HashMap;
import java.util.Map;

import jsdsi.JsdsiRuntimeException;


/**
 * <p>Experimental class and as such may be removed without warning.</p>
 * Enum class for MessageDigest algorythms.
 * 
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 */
public class DigestAlgoEnum extends AlgorithmEnum {

    /**
     * The name of this Digest when part of a JDK Signautre algorithm
     */
    private String jdkSigName;
    
    /**
     * @param jdkName
     * @param spkiName
     */
    protected DigestAlgoEnum(String jdkName, String spkiName) {
        super(jdkName, spkiName);
        this.jdkSigName = jdkName;
    }
    
    /**
     * @param jdkName
     * @param spkiName
     */
    protected DigestAlgoEnum(String jdkName, String spkiName, String jdkSigName) {
        super(jdkName, spkiName);
        this.jdkSigName = jdkSigName;
    }

    /**
     * The MD2 message digest algorithm as defined in RFC 1319.
     */
    public static final DigestAlgoEnum MD2 = new DigestAlgoEnum("MD2", "md2");
    
    /**
     * The MD5 message digest algorithm as defined in RFC 1321.
     */
    public static final DigestAlgoEnum MD5 = new DigestAlgoEnum("MD5", "md5");
    
    /**
     * The Secure Hash Algorithm, as defined in Secure Hash Standard, NIST FIPS 180-1.
     */
    public static final DigestAlgoEnum SHA1 = new DigestAlgoEnum("SHA-1", "sha1", "SHA1");
    
    /**
     * 256-bit hash function intended to provide 128 bits of security against collision attacks.
     */
    public static final DigestAlgoEnum SHA256 = new DigestAlgoEnum("SHA-256", "sha256", "SHA256");
    
    /**
     * 384 byte SHA
     */
    public static final DigestAlgoEnum SHA384 = new DigestAlgoEnum("SHA-384", "sha384", "SHA384");
    
    /**
     * 512-bit hash function intended to provide 256 bits of security.
     */
    public static final DigestAlgoEnum SHA512 = new DigestAlgoEnum("SHA-512", "sha512", "SHA512");
    
    private static final Map jdkMap = new HashMap();
    
    private static final Map jdkSigMap = new HashMap();
    
    private static final Map spkiMap = new HashMap();
    
    static {
        jdkMap.put("MD2", DigestAlgoEnum.MD2);
        jdkMap.put("MD5", DigestAlgoEnum.MD5);
        jdkMap.put("SHA-1", SHA1);
        jdkMap.put("SHA-256", SHA256);
        jdkMap.put("SHA-384", SHA384);
        jdkMap.put("SHA-512", SHA512);
        
        jdkSigMap.put("MD2", DigestAlgoEnum.MD2);
        jdkSigMap.put("MD5", DigestAlgoEnum.MD5);
        jdkSigMap.put("SHA1", SHA1);
        jdkSigMap.put("SHA256", SHA256);
        jdkSigMap.put("SHA384", SHA384);
        jdkSigMap.put("SHA512", SHA512);
        
        spkiMap.put("md2", MD2);
        spkiMap.put("md5", MD5);
        spkiMap.put("sha1", SHA1);
        spkiMap.put("sha256", SHA256);
        spkiMap.put("sha384", SHA384);
        spkiMap.put("sha512", SHA512);
    }
    
    /**
     * Given a JDK name for a MessageDigest algorythm, return its DigestAlgoEnum
     * @param jdkName JDK name
     * @return the DigestAlgoEnum
     */
    public static DigestAlgoEnum fromJdk(String jdkName) {
        DigestAlgoEnum e = (DigestAlgoEnum) jdkMap.get(jdkName);
        if (e==null) {
            throw new JsdsiRuntimeException("DigestAlgoEnum not found for JDK name: "+jdkName);
        }
        return e;
    }
    
    /**
     * Given a name for a MessageDigest algorythm from a JDK Signature, return its DigestAlgoEnum
     * @param jdkName JDK name
     * @return the DigestAlgoEnum
     * @throws JsdsiRuntimeException on error
     */
    public static DigestAlgoEnum fromJdkSignature(String jdkName) {
        DigestAlgoEnum e = (DigestAlgoEnum) jdkSigMap.get(jdkName);
        if (e==null) {
            throw new JsdsiRuntimeException("DigestAlgoEnum not found for JDK Signature name: "+jdkName);
        }
        return e;
    }
    
    /**
     * Given a SPKI name for a MessageDigest algorythm, return its DigestAlgoEnum
     * @param spkiName SPKI name
     * @return the DigestAlgoEnum
     * @throws JsdsiRuntimeException on error
     */
    public static DigestAlgoEnum fromSpki(String spkiName) {
        DigestAlgoEnum e = (DigestAlgoEnum) spkiMap.get(spkiName);
        if (e==null) {
            throw new JsdsiRuntimeException("DigestAlgoEnum not found for SPKI name: "+spkiName);
        }
        return e;
    }
    
    /**
     * Returns the name of this digest when part of a JDK Signature algorythm
     * @return the name
     */
    public String jdkSignatureName() {
        return this.jdkSigName;
    }
    
}
