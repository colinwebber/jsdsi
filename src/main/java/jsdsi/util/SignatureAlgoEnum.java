/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;


import jsdsi.JsdsiRuntimeException;


/**
 * <p>Experimental class and as such may be removed without warning.</p>
 * Enum class for Signature Algorythms.
 * 
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 */
public class SignatureAlgoEnum extends AlgorithmEnum {

    private DigestAlgoEnum digest;
    
    private KeyEnum key;
    
    /**
     * @param jdkName
     * @param spkiName
     */
    protected SignatureAlgoEnum(DigestAlgoEnum digest, KeyEnum key) {
        super(Algorithms.jdkSignatureName(digest, key), Algorithms.spkiSignatureName(digest, key));
        this.digest = digest;
        this.key = key;
    }

    /**
     * Return the DigestAlgoEnum for this SignatureAlgoEnum
     * @return the DigestAlgoEnum
     */
    public DigestAlgoEnum getDigestEnum() {
        return this.digest;
    }
    
    /**
     * Return the KeyEnum for this SignatureAlgoEnum
     * @return the KeyEnum
     */
    public KeyEnum getKeyEnum() {
        return this.key;
    }
    
    /**
     * Create a SignatureAlgoEnum
     * @param digest the DigestAlgoEnum
     * @param key the KeyEnum
     * @return the SignatureAlgoEnum
     */
    public static SignatureAlgoEnum create(DigestAlgoEnum digest, KeyEnum key) {
        return new SignatureAlgoEnum(digest, key);
    }
    
    /**
     * Given a JDK name for a Signature algorythm, return its SignatureAlgoEnum
     * @param jdkName JDK name
     * @return the SignatureAlgoEnum
     * @throws JsdsiRuntimeException on error
     */
    public static SignatureAlgoEnum fromJdk(String jdkName) {
        String[] bits = jdkName.split("with");
        if (bits.length!=2) {
            throw new JsdsiRuntimeException("Illegal JDK SignatureAlgo: "+jdkName);
        }
        DigestAlgoEnum digest = DigestAlgoEnum.fromJdkSignature( bits[0] );
        KeyEnum key = KeyEnum.fromJdk( bits[1] );
        return new SignatureAlgoEnum(digest, key);
    }
    
    /**
     * Given a SPKI name for a Signature algorythm, return its SignatureAlgoEnum
     * @param spkiName SPKI name
     * @return the SignatureAlgoEnum
     * @throws JsdsiRuntimeException on error
     */
    public static SignatureAlgoEnum fromSpki(String spkiName) {
        int pos = spkiName.lastIndexOf("-");
        if (pos==-1) {
            throw new JsdsiRuntimeException("Illegal SPKI SignatureAlgo: "+spkiName);
        }
        String s = spkiName.substring(pos+1);
        DigestAlgoEnum digest = DigestAlgoEnum.fromSpki( s );
        s = spkiName.substring(0, pos);
        KeyEnum key = KeyEnum.fromSpki( s );
        return new SignatureAlgoEnum(digest, key);
    }
    
}
