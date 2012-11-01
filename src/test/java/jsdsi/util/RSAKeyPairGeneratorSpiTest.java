/*
 * Copyright ©, Aegeus Technology Limited. All rights reserved.
 */
package jsdsi.util;

import java.math.BigInteger;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import jsdsi.Principal;
import jsdsi.Provider;
import junit.framework.TestCase;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/11 19:31:47 $
 */
public class RSAKeyPairGeneratorSpiTest extends TestCase {



	private static final String ALGO_RSA = "SPKI/RSA";

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

    public void testDefaults() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGO_RSA);
        KeyPair kp = kpg.generateKeyPair();
        assertTrue(kp.getPublic() instanceof Principal);
        assertNull(((Principal) kp.getPublic()).getURIs());
    }

    public void testURI() throws Exception {
    	// colinw
        //RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, BigInteger.ONE,
        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, new BigInteger("3"),
                new URI[] { new URI("test://jsdsi.sf.net") });
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGO_RSA);
        kpg.initialize(spec);
        KeyPair kp = kpg.generateKeyPair();
        assertTrue(kp.getPublic() instanceof Principal);
        assertNotNull(((Principal) kp.getPublic()).getURIs());
    }

}