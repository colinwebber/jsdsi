/*
 * Copyright ©, Aegeus Technology Limited. All rights reserved.
 */
package jsdsi.util;

import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import jsdsi.JsdsiRuntimeException;

/**
 * Convenience class for generating <code>java.security.KeyPair</code> 's. <p/>This class
 * maintains a cache of KeyPairGenerators retrieved from the JCE for performance. <p/><strong>Note:
 * </strong> Care should be taken when creating Keys without specifying a keysize, SecureRandom, or
 * any AlgorithmParameterSpec, as it has been noted that without, keys are predictable when using
 * some Providers.
 * 
 * @author Sean Radford
 * @version $Revision: 1.9 $ $Date: 2005/02/16 09:42:03 $
 */
public final class KeyPairFactory {

    private static final int KEYSIZE_NOT_SPECIFIED = -1;

    private static Map generators = new HashMap();

    private static KeyPairFactory factory = null;

    private KeyPairFactory() {
        super();
    }

    /**
     * Singleton constructor method.
     * 
     * @return
     */
    private static synchronized KeyPairFactory getInstance() {
        if (factory == null) {
            factory = new KeyPairFactory();
        }
        return factory;
    }

    /**
     * Generates a KeyPair of the given <code>keyEnum</code> type. The public key of the KeyPair
     * is a {@link jsdsi.Principal jsdsi.Principal}.
     * 
     * @param keyEnum the key algorithm.
     * @return the new KeyPair
     * @throws NoSuchAlgorithmException if the algorithm is not available in the environment.
     */
    public static KeyPair create(KeyEnum keyEnum) throws NoSuchAlgorithmException {
        return create(keyEnum, KEYSIZE_NOT_SPECIFIED, (URI[]) null);
    }

    /**
     * Generates a KeyPair of the given <code>keyEnum</code> type and key size. The public key of
     * the KeyPair is a {@link jsdsi.Principal jsdsi.Principal}.
     * 
     * @param keyEnum the key algorithm.
     * @param keysize the keysize. This is an algorithm-specific metric, such as modulus length,
     *                 specified in number of bits.
     * @return the new KeyPair
     * @throws NoSuchAlgorithmException if the algorithm is not available in the environment.
     */
    public static KeyPair create(KeyEnum keyEnum, int keysize) throws NoSuchAlgorithmException {
        return create(keyEnum, keysize, (URI[]) null);
    }

    /**
     * Generates a KeyPair of the given <code>keyEnum</code> type with the given <code>uris</code>.
     * The public key of the KeyPair is a {@link jsdsi.Principal jsdsi.Principal}.
     * 
     * @param keyEnum the key algorithm.
     * @param uris URI's where information about the Principal may be sought
     * @return the new KeyPair
     * @throws NoSuchAlgorithmException if the algorithm is not available in the environment.
     */
    public static KeyPair create(KeyEnum keyEnum, URI[] uris) throws NoSuchAlgorithmException {
        return create(keyEnum, KEYSIZE_NOT_SPECIFIED, uris);
    }

    /**
     * Generates a KeyPair of the given <code>keyEnum</code> type and <code>uris</code>. The
     * public key of the KeyPair is a {@link jsdsi.Principal jsdsi.Principal}.
     * 
     * @param keyEnum the key algorithm.
     * @param keysize the keysize. This is an algorithm-specific metric, such as modulus length,
     *                 specified in number of bits.
     * @param uris URI's where information about the Principal may be sought
     * @return the new KeyPair
     * @throws NoSuchAlgorithmException if the algorithm is not available in the environment.
     */
    public static KeyPair create(KeyEnum keyEnum, int keysize, URI[] uris)
            throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = getKeyPairGenerator(keyEnum, keysize, uris);
        return kpg.generateKeyPair();
    }

    /**
     * Retrieves a KeyPairGenerator.
     * 
     * @param keyEnum
     * @param keysize
     * @param uris
     * @return the KeyPairGenerator
     * @throws NoSuchAlgorithmException
     */
    public static synchronized KeyPairGenerator getKeyPairGenerator(KeyEnum keyEnum, int keysize,
            URI[] uris) throws NoSuchAlgorithmException {
        String key = calculateKey(keyEnum, keysize, uris);
        KeyPairGenerator kpg = (KeyPairGenerator) generators.get(key);
        if (kpg == null) {
            kpg = createKeyPairGenerator(keyEnum, keysize, uris);
            generators.put(key, kpg);
        }
        return kpg;
    }

    /**
     * Clears the cache of KeyPairGenerators (probably only useful for testing unique key generation).
     *
     */
    public static void clearGeneratorCache() {
        generators.clear();
    }
    
    /**
     * Calculates the key to use for the Map of <code>generators</code>.
     * 
     * @param keyEnum key algorithm
     * @param keysize length of key
     * @param uris array of URIs for key
     * @return the key
     */
    private static String calculateKey(KeyEnum keyEnum, int keysize, URI[] uris) {
        StringBuffer sb = new StringBuffer(keyEnum.spkiName());
        if (keysize != KEYSIZE_NOT_SPECIFIED) {
            sb.append('^');
            sb.append(keysize);
        }
        if (uris != null) {
            for (int i = 0; i < uris.length; i++) {
                sb.append('^');
                sb.append(uris[i].toString());
            }

        }
        return sb.toString();
    }

    private static KeyPairGenerator createKeyPairGenerator(KeyEnum keyEnum, int keysize, URI[] uris) {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("SPKI/" + keyEnum.jdkName());
        } catch (NoSuchAlgorithmException e) {
            throw new JsdsiRuntimeException(e.toString(), e);
        }
        if (keyEnum.equals(KeyEnum.RSA)) {
            if (uris == null) {
                if (keysize != KEYSIZE_NOT_SPECIFIED) {
                    kpg.initialize(keysize);
                }
            } else {
                RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(keysize,java.security.spec.RSAKeyGenParameterSpec.F4, uris);
                try {
                    kpg.initialize(spec);
                } catch (InvalidAlgorithmParameterException e) {
                    throw new JsdsiRuntimeException(e.toString(), e);
                }
            }
        } else {
            throw new IllegalArgumentException("Currently unsupported Key Algorithm");
        }
        return kpg;
    }

}