/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi;

import java.net.URI;

import java.net.URISyntaxException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;
import junit.framework.TestCase;


/**
 * Some simple tests.
 * 
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:08:08 $
 */
public class PublicKeyHashTest extends TestCase {


	private KeyPair keyPair;
    
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        Provider.install();
        keyPair = KeyPairFactory.create(KeyEnum.RSA, 512); 
    }
    
    /**
     * Test of a Hash with no URI[].
     * @throws Exception
     */
    public void testHashOnly() throws Exception {
        Hash hashExpected = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), null);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hashExpected);
        Hash hashActual = pubKeyHash.getHash();
        assertEquals("Hash, ", hashExpected, hashActual);
    }
    
    /**
     * Test of a Hash with a URI[].
     * @throws URISyntaxException
     */
    public void testHashAndURIs() throws URISyntaxException {
        URI[] urisExpected = new URI[] { new URI("test:/test") };
        Hash hashExpected = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), urisExpected);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hashExpected);
        Hash hashActual = pubKeyHash.getHash();
        assertEquals("Hash, ", hashExpected, hashActual);
        URI[] urisActual = pubKeyHash.getURIs();
        assertNotNull("URIs is NULL", urisActual);
        assertEquals("URI.length, ", urisExpected.length, urisActual.length);
    }
    
    /**
     * Test of samePrincipalAs with matching PublicKeyHash.
     */
    public void testSamePrincipalAsWithPublicKeyHash() {
        Hash hash = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), null);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hash);
        assertTrue(pubKeyHash.samePrincipalAs(pubKeyHash));
    }
    
    /**
     * Test of samePrincipalAs with matching PublicKey.
     */
    public void testSamePrincipalAsWithPublicKey() {
        Hash hash = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), null);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hash);
        assertTrue(pubKeyHash.samePrincipalAs( (Principal) keyPair.getPublic() ));
    }
    
    /**
     * Test of samePrincipalAs with non-matching PublicKeyHash.
     * @throws NoSuchAlgorithmException
     */
    public void testNotSamePrincipalAsWithPublicKeyHash() throws NoSuchAlgorithmException {
        Hash hash = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), null);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hash);
        KeyPair otherKeyPair = KeyPairFactory.create(KeyEnum.RSA, 512);
        Hash otherHash = new Hash(DigestAlgoEnum.MD5, (PublicKey)otherKeyPair.getPublic(), null);
        PublicKeyHash otherPubKeyHash = new PublicKeyHash(otherHash);
        assertFalse(pubKeyHash.samePrincipalAs(otherPubKeyHash));
    }
    
    /**
     * Test of samePrincipalAs with non-matching PublicKey.
     * @throws NoSuchAlgorithmException
     */
    public void testNotSamePrincipalAsWithPublicKey() throws NoSuchAlgorithmException {
        Hash hash = new Hash(DigestAlgoEnum.MD5, (PublicKey)keyPair.getPublic(), null);
        PublicKeyHash pubKeyHash = new PublicKeyHash(hash);
        KeyPair otherKeyPair = KeyPairFactory.create(KeyEnum.RSA, 512);
        assertFalse(pubKeyHash.samePrincipalAs((Principal) otherKeyPair.getPublic()));
    }
    
}
