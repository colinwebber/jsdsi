/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.math.BigInteger;
import java.net.URI;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/11 19:31:46 $
 */
public class RSAKeyGenParameterSpec extends java.security.spec.RSAKeyGenParameterSpec {

    private String provider;
    
    private URI[] uris;
    
    /**
     * @param keysize
     * @param publicExponent
     */
    public RSAKeyGenParameterSpec(int keysize, BigInteger publicExponent) {
        super(keysize, publicExponent);
    }

    /**
     * @param keysize
     * @param publicExponent
     * @param provider name of <code>java.security.Provider</code> to use to create the keys.
     */
    public RSAKeyGenParameterSpec(int keysize, BigInteger publicExponent, String provider) {
        super(keysize, publicExponent);
        this.provider = provider;
    }
    
    /**
     * @param keysize
     * @param publicExponent
     * @param uris array of uris to incorporate in PublicKeys generated
     */
    public RSAKeyGenParameterSpec(int keysize, BigInteger publicExponent, URI[] uris) {
        super(keysize, publicExponent);
        this.uris = uris;
    }
    
    /**
     * @param keysize
     * @param publicExponent
     * @param provider name of <code>java.security.Provider</code> to use to create the keys.
     * @param uris array of uris to incorporate in PublicKeys generated
     */
    public RSAKeyGenParameterSpec(int keysize, BigInteger publicExponent, String provider, URI[] uris) {
        super(keysize, publicExponent);
        this.uris = uris;
    }
    
    /**
     * @return Returns the provider.
     */
    public String getProvider() {
        return provider;
    }
    
    /**
     * @return Returns the uris.
     */
    public URI[] getUris() {
        return uris;
    }
    
}
