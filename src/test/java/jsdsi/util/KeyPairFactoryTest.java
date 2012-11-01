/*
 * Copyright ©, Aegeus Technology Limited. All rights reserved.
 */
package jsdsi.util;

import java.net.URI;
import java.security.KeyPair;
import java.security.PublicKey;

import jsdsi.Provider;
import jsdsi.RSAPublicKey;
import junit.framework.TestCase;

/**
 * @author Sean Radford
 */
public class KeyPairFactoryTest extends TestCase {

 

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Provider.install();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test generation of jsdsi compatible RSA key
     * 
     * @throws Exception
     */
    public void testRSA() throws Exception {
        KeyPair kp = KeyPairFactory.create(KeyEnum.RSA);
        java.security.PublicKey javaPubKey = kp.getPublic();
        assertTrue((javaPubKey instanceof RSAPublicKey));
    }

    /**
     * Test generation of jsdsi compatible RSA key, specifying a keySize
     * 
     * @throws Exception
     */
    public void testRSA_KeySize() throws Exception {
        KeyPair kp = KeyPairFactory.create(KeyEnum.RSA, 512);
        java.security.PublicKey pubKey = kp.getPublic();
        assertTrue((pubKey instanceof RSAPublicKey));
    }
    
    /**
     * Test generation of jsdsi compatible RSA key, specifying a URI
     * 
     * @throws Exception
     */
    public void testRSA_URI() throws Exception {
        URI uri = new URI("test://jsdsi.sf.net");
        KeyPair kp = KeyPairFactory.create(KeyEnum.RSA,new URI[] {uri});
        java.security.PublicKey pubKey = kp.getPublic();
        assertTrue((pubKey instanceof RSAPublicKey));
        assertNotNull( ((RSAPublicKey)pubKey).getURIs());
    }

    /**
     * Test generation of jsdsi compatible RSA key, specifying a keySize and URI
     * 
     * @throws Exception
     */
    public void testRSA_KeySizeAndURI() throws Exception {
        URI uri = new URI("test://jsdsi.sf.net");
        KeyPair kp = KeyPairFactory.create(KeyEnum.RSA, 512, new URI[] {uri});
        java.security.PublicKey pubKey = kp.getPublic();
        assertTrue((pubKey instanceof RSAPublicKey));
        assertNotNull( ((RSAPublicKey)pubKey).getURIs());
    }
    
    /**
     * Test generation of unique RSA keys
     * 
     * @throws Exception
     */
    public void testRSA_UniqueKeyGeneration() throws Exception {
        int NUM = 10;
        PublicKey[] keys = new PublicKey[NUM];
        KeyPairFactory.clearGeneratorCache();
        for (int i=0; i<NUM; i++) {
            keys[i] = KeyPairFactory.create(KeyEnum.RSA,512).getPublic();
            KeyPairFactory.clearGeneratorCache();
        }
        for (int i=0; i<NUM; i++) {
            PublicKey iKey = keys[i];
            for (int j=0; j<NUM; j++) {
                if (i!=j) {
                    PublicKey jKey= keys[j];
                    if (iKey.equals(jKey)) {
                        fail("Duplicate key generated");
                    }
                }
            }
        }
    }
}