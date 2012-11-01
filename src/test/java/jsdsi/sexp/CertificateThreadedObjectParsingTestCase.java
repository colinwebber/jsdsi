/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.sexp;

import java.security.KeyPair;

import jsdsi.AuthCert;
import jsdsi.Cert;
import jsdsi.Certificate;
import jsdsi.Principal;
import jsdsi.Sequence;
import jsdsi.Signature;
import jsdsi.StringTag;
import jsdsi.Subject;
import jsdsi.Tag;
import jsdsi.Validity;
import jsdsi.util.DateUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;

/**
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public class CertificateThreadedObjectParsingTestCase extends ObjThreadedObjectParsingTestCase {
    
    CertificateThreadedObjectParsingTestCase(int numOfObjects, int numOfThreads) {
        super("jsdsi.Certificte", numOfObjects, numOfThreads);
    }
    
    protected Object createObject() throws Exception {
        KeyPair kp = KeyPairFactory.create(KeyEnum.RSA, 512);
      Validity v = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
      Tag tag = new StringTag("thread test");
      Cert cert = new AuthCert((Principal) kp.getPublic(), (Subject) kp.getPublic(), v,
              "description", "comment", tag, false);
      Signature sig = Signature.create(kp, cert, DigestAlgoEnum.MD5);
      Certificate c = new Certificate(kp.getPublic(), cert, sig);
      return c;
    }

    protected byte[] getBytes(Object object) {
        return ((Certificate)object).getEncoded();
    }

    Object generateObject(byte[] bytes) throws Exception {
        Sequence seq = (Sequence) super.generateObject(bytes);
        return Certificate.fromSequence(seq);
    }
}
