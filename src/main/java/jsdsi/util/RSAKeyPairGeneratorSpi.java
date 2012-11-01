/*
 * Copyright ©, Aegeus Technology Limited. All rights reserved.
 */
package jsdsi.util;

import java.math.BigInteger;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyPairGeneratorSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import jsdsi.JsdsiRuntimeException;
import jsdsi.PublicKey;
import jsdsi.RSAPublicKey;

/**
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2005/02/16 09:42:03 $
 */
public class RSAKeyPairGeneratorSpi extends KeyPairGeneratorSpi {

    public static final String ALGORITHM = "RSA";
    
    public static final int DEFAULT_KEY_SIZE = 1024;

    private int keySize;

    private BigInteger publicExponent;

    private URI[] uris;

    private String provider;

    private SecureRandom secureRandom;

    private KeyPairGenerator keyPairGenerator;

    /**
     *  
     */
    public RSAKeyPairGeneratorSpi() {
        super();
    }

    /**
     * @see java.security.KeyPairGeneratorSpi#initialize(java.security.spec.AlgorithmParameterSpec,
     *          java.security.SecureRandom)
     */
    public void initialize(AlgorithmParameterSpec params, SecureRandom random)
            throws InvalidAlgorithmParameterException {
        if (params instanceof RSAKeyGenParameterSpec) {
            RSAKeyGenParameterSpec p = (RSAKeyGenParameterSpec) params;
            this.keySize = p.getKeysize();
            this.publicExponent = p.getPublicExponent();
            this.uris = p.getUris();
            this.provider = p.getProvider();
            this.secureRandom = random;
        } else {
            throw new IllegalArgumentException(
                    "Illegal AlgorithmParamterSpec class: "
                            + params.getClass().getName());
        }
    }

    /**
     * @see java.security.KeyPairGeneratorSpi#initialize(int,
     *          java.security.SecureRandom)
     */
    public void initialize(int keysize, SecureRandom random) {
        this.keySize = keysize;
        this.secureRandom = random;
    }

    /**
     * @see java.security.KeyPairGeneratorSpi#generateKeyPair()
     */
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator kpg = getKeyPairGenerator();
            KeyPair kp = kpg.generateKeyPair();
            PublicKey pubKey = null;
            if (this.uris != null && this.uris.length > 0) {
                pubKey = new RSAPublicKey(
                        (java.security.interfaces.RSAPublicKey) kp.getPublic(),
                        this.uris);
            } else {
                pubKey = new RSAPublicKey(
                        (java.security.interfaces.RSAPublicKey) kp.getPublic());
            }
            return new KeyPair(pubKey, kp.getPrivate());
        } catch (NoSuchAlgorithmException e) {
            throw new JsdsiRuntimeException(e.toString(), e);
        } catch (NoSuchProviderException e) {
            throw new JsdsiRuntimeException(e.toString(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new JsdsiRuntimeException(e.toString(), e);
        }
    }

    private KeyPairGenerator getKeyPairGenerator()
            throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        if (this.keyPairGenerator == null) {
            if (this.keySize < 1) {
                this.keySize = DEFAULT_KEY_SIZE;
            }
            String provider = null;
            if (this.provider != null && !this.provider.equals("")) {
                provider = this.provider;
            }
            this.keyPairGenerator = (provider == null) ? KeyPairGenerator
                    .getInstance(ALGORITHM) : KeyPairGenerator.getInstance(
                    ALGORITHM, provider);
            if (this.publicExponent == null) {
                this.publicExponent = RSAKeyGenParameterSpec.F4;
            }
            java.security.spec.RSAKeyGenParameterSpec spec = new java.security.spec.RSAKeyGenParameterSpec(
                    this.keySize, this.publicExponent);
            this.keyPairGenerator.initialize(spec);

            //            } else {
            //                this.keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            //                this.keyPairGenerator.initialize(this.keySize);
            //                }
        }
        return this.keyPairGenerator;
    }

}