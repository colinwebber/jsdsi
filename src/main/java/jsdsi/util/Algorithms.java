/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;


/**
 * <p>Experimental class and as such may be removed without warning.</p>
 * Utility class for Algorythm constants and names for both the JDK and SPKI.
 * 
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 */
public class Algorithms {     
    
    /**
     * The JDK type name for a SPKI Certificate 
     */
    public static final String JDK_CERTIFICATE_TYPE_SPKI = "SPKI";
    
    /**
     * The JDK name for a CertificateFactory for generating SPKI Certificates from SExpressions
     **/
    public static final String JDK_CERTIFICATEFACTORY_SPKI_SEXP = "SPKI/SEXP";
    
    
    /**
     * The JDK name for a KeyFactory for generating Keys from SExpressions
     **/
    public static final String JDK_KEYFACTORY_SEXP = "SPKI/SEXP";
    
    /**
     * The JDK name for the standard SPKI CertPathBuilder
     */
    public static final String JDK_CERTPATHBUILDER_SPKI ="SPKI";
    
    /**
     * The JDK name for the standard SPKI CertPathValidatorr
     */
    public static final String JDK_CERTPATHVALIDATOR_SPKI ="SPKI";
    
    /**
     * The JDK name for the standard SPKI CertStore
     */
    public static final String JDK_CERTSTORE_SPKI = "SPKI";
    
    /**
     * The JDK name for the LDAP SPKI CertStore
     */
    public static final String JDK_CERTSTORE_SPKI_LDAP = "SPKI/LDAP";

    
    /**
     * 
     */
    private Algorithms() {
        super();
    }
    
    /**
     * Generates a Signature algorythm in JDK format 
     * @param digest a DigestAlgoEnum
     * @param key a KeyEnum
     * @return
     */
    public static String jdkSignatureName(DigestAlgoEnum digest, KeyEnum key) {
        StringBuffer sb = new StringBuffer(digest.jdkSignatureName());
        sb.append("with");
        sb.append(key.jdkName());
        return sb.toString();
    }

    /**
     * Generates a Signature algorythm in SPKI format 
     * @param digest a DigestAlgoEnum
     * @param key a KeyEnum
     * @return
     */
    public static String spkiSignatureName(DigestAlgoEnum digest, KeyEnum key) {
        StringBuffer sb = new StringBuffer(key.spkiName());
        sb.append('-');
        sb.append(digest.spkiName());
        return sb.toString();
    }
    
}
