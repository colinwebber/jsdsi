/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import junit.framework.TestCase;


/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 */
public class SignatureEnumTest extends TestCase {


	public void testMD5withRSA() {
        DigestAlgoEnum d = DigestAlgoEnum.MD5;
        KeyEnum k = KeyEnum.RSA;
        SignatureAlgoEnum s = SignatureAlgoEnum.create(d,k);
        assertNotNull(s);
    }
    
    public void testSHA1withRSA() {
        DigestAlgoEnum d = DigestAlgoEnum.SHA1;
        KeyEnum k = KeyEnum.RSA;
        SignatureAlgoEnum s = SignatureAlgoEnum.create(d,k);
        assertNotNull(s);
    }
    
    public void testSHA256withRSA() {
        DigestAlgoEnum d = DigestAlgoEnum.SHA256;
        KeyEnum k = KeyEnum.RSA;
        SignatureAlgoEnum s = SignatureAlgoEnum.create(d,k);
        assertNotNull(s);
    }
    
}
