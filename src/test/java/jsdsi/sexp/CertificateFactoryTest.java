/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.sexp;

import java.security.KeyPair;
import java.util.Date;

import jsdsi.NameCert;
import jsdsi.Principal;
import jsdsi.Provider;
import jsdsi.Validity;
import jsdsi.util.Algorithms;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;
import junit.framework.TestCase;


/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/11/08 12:13:21 $
 */
public class CertificateFactoryTest extends TestCase {



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

    public void testFactory() throws Exception {
        java.security.cert.CertificateFactory fac = java.security.cert.CertificateFactory.getInstance(Algorithms.JDK_CERTIFICATEFACTORY_SPKI_SEXP);
        KeyPair kp1 = KeyPairFactory.create(KeyEnum.RSA);
        KeyPair kp2 = KeyPairFactory.create(KeyEnum.RSA);
        Validity v = new Validity(new Date(), new Date());
        NameCert cert = new NameCert( (Principal)kp1.getPublic(), (Principal)kp2.getPublic(), v, "text", "test", "alice");
        assertNotNull(cert);
    }
    
}
