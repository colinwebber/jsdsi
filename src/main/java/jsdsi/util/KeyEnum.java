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
 * Enum class for Key algorithms.
 * 
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/11/12 09:53:49 $
 */
public class KeyEnum extends AlgorithmEnum {

    /**
     * @param jdkName
     * @param spkiName
     */
    protected KeyEnum(String jdkName, String spkiName) {
        super(jdkName, spkiName);
    }

    /**
     * The Digital Signature Algorithm as defined in FIPS PUB 186.
     */
    public static final KeyEnum DSA = new KeyEnum("DSA", "dsa");
    
    /**
     * The RSA encryption algorithm as defined in PKCS #1.
     */
    public static final KeyEnum RSA = new KeyEnum("RSA", "rsa-pkcs1");
    
    
    private static final Map jdkMap = new HashMap();
    
    private static final Map spkiMap = new HashMap();
    
    static {
        jdkMap.put("DSA", DSA);
        jdkMap.put("RSA", RSA);
        
        spkiMap.put("dsa", DSA);
        spkiMap.put("rsa-pkcs1", RSA);
    }
    
    /**
     * Given a JDK name for a Key algorythm, return its KeyEnum
     * @param jdkName JDK name
     * @return the KeyEnum
     */
    public static KeyEnum fromJdk(String jdkName) {
        KeyEnum e = (KeyEnum) jdkMap.get(jdkName);
        if (e==null) {
            throw new JsdsiRuntimeException("KeyEnum not found for JDK name: "+jdkName);
        }
        return e;
    }
    
    /**
     * Given a SPKI name for a Key algorithm, return its KeyEnum
     * @param spkiName SPKI name
     * @return the KeyEnum
     */
    public static KeyEnum fromSpki(String spkiName) {
        KeyEnum e = (KeyEnum) spkiMap.get(spkiName);
        if (e==null) {
            throw new JsdsiRuntimeException("KeyEnum not found for SPKI name: "+spkiName);
        }
        return e;
    }
    
}
