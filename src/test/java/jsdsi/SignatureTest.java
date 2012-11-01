/*
 * Copyright 2002 Massachusetts Institute of Technology Permission to use, copy, modify, and
 * distribute this program for any purpose and without fee is hereby granted, provided that this
 * copyright and permission notice appear on all copies and supporting documentation, the name of
 * M.I.T. not be used in advertising or publicity pertaining to distribution of the program without
 * specific prior permission, and notice be given in supporting documentation that copying and
 * distribution is by permission of M.I.T. M.I.T. makes no representations about the suitability of
 * this software for any purpose. It is provided "as is" without express or implied warranty.
 */
package jsdsi;

import java.security.KeyPair;

import jsdsi.Obj;
import jsdsi.util.DateUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;
import junit.framework.TestCase;

/**
 * Tests RSA key generation, signing, and verification.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 */
public class SignatureTest extends TestCase {
    static {
        Provider.install();
    }
    
    public SignatureTest(String name) {
        super(name);
    }

    public void testKeySignAndVerify() throws Exception {
        KeyPair signer = jsdsi.RSAPublicKey.create();
        Obj signee = (jsdsi.RSAPublicKey) signer.getPublic();
        jsdsi.Signature sig = jsdsi.Signature.create(signer, signee, DigestAlgoEnum.MD5);
        assertTrue("verification", sig.verify(signer.getPublic(), signee));
    }
    
    public void testCertificateSignAndVerify() throws Exception {
        KeyPair signer = KeyPairFactory.create(KeyEnum.RSA);
        KeyPair signee = KeyPairFactory.create(KeyEnum.RSA);
        
        Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate(0,0,1));
        String dh = "plain";
        String c = "test certificate";
        Tag t = new StringTag("test");
        boolean p = false;
        
        AuthCert cert = new AuthCert((Principal) signer.getPublic(), (Subject)signee.getPublic(), v, dh, c, t, p);
        
        jsdsi.Signature sig = jsdsi.Signature.create(signer, cert, DigestAlgoEnum.SHA1);
        Certificate certificate = new Certificate(cert, sig);
        
        try {
            certificate.verify(signer.getPublic());
        } catch (Exception e) {
            fail(e.fillInStackTrace().toString());
        }
    }

    public void testEquality() throws Exception {
        KeyPair signer = jsdsi.RSAPublicKey.create();
        Obj signee = (jsdsi.RSAPublicKey) signer.getPublic();
        jsdsi.Signature sig = jsdsi.Signature.create(signer, signee, DigestAlgoEnum.MD5);
        jsdsi.Signature sig2 = jsdsi.Signature.create(signer, signee, DigestAlgoEnum.MD5);
        assertEquals("self equality", sig, sig);
        assertEquals("equality", sig, sig2);
        assertEquals("self hashcode equality", sig.hashCode(), sig.hashCode());
        assertEquals("hashcode equality", sig.hashCode(), sig2.hashCode());
    }

}