/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyPair;

import jsdsi.sexp.ObjInputStream;
import jsdsi.sexp.ObjOutputStream;
import jsdsi.sexp.SexpException;
import jsdsi.sexp.SexpParseException;
import jsdsi.util.DateUtil;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.KeyEnum;
import jsdsi.util.KeyPairFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Sean Radford
 * @version $Revision: 1.6 $ $Date: 2004/11/16 12:23:41 $
 */
public class SexpMarshallingTest extends TestCase {

    SexpMarshallingTest() {
		super("SexpMarshallingTest");
		// TODO Auto-generated constructor stub
	}

	static {
        jsdsi.Provider.install();
    }
    
    static Obj[] objArray = null;
    static {
        try {
            objArray = new Obj[] {
                    (Obj) RSAPublicKey.create().getPublic(),
                    new RSAPublicKey((java.security.interfaces.RSAPublicKey) RSAPublicKey.create().getPublic(),
                            new URL[] { new URL("http://localhost/")}),
                            createAuthCertificate().toSequence(),
                            createNameCertificate().toSequence()
                   	};
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Certificate createAuthCertificate() throws Exception {
        KeyPair issuerKP = KeyPairFactory.create(KeyEnum.RSA, 512);
        KeyPair subjectKP = KeyPairFactory.create(KeyEnum.RSA, 512);
        Validity validity = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
        Tag tag = new StringTag("test tag");
        boolean propagate = true;
        AuthCert cert = new AuthCert((Principal) issuerKP.getPublic(), (Principal) subjectKP.getPublic(), validity,
                "test diplay hint", "a test certificate", tag, propagate);
        Signature sig = Signature.create(issuerKP, cert, DigestAlgoEnum.MD5);
        return new Certificate(cert, sig);
    }

    static Certificate createNameCertificate() throws Exception {
        KeyPair issuerKP = KeyPairFactory.create(KeyEnum.RSA, 512);
        KeyPair subjectKP = KeyPairFactory.create(KeyEnum.RSA, 512);
        Validity validity = new Validity(DateUtil.newDate(), DateUtil.newDate(1));
        
        NameCert cert = new NameCert((Principal) issuerKP.getPublic(), (Subject)subjectKP.getPublic(), validity, "test diplay hint", "a test certificate", "alice");
        Signature sig = Signature.create(issuerKP, cert, DigestAlgoEnum.MD5);
        return new Certificate(cert, sig);
    }
    

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    static abstract class AbstractMarshallingTest extends TestCase {

        static final int OFFSET = 2;

        static final int WIDTH = 80;

        static final int LAST = 2;

        Obj obj;

        public AbstractMarshallingTest(String type, Obj obj) {
            super(type);
            this.obj = obj;
        }

        public void runTest() throws Exception {
            byte[] bytes = toByteArray(obj);
            Obj out = fromByteArray(bytes);
            assertEquals(this.getName(), obj, out);
        }

        protected abstract byte[] toByteArray(Obj obj) throws IOException;

        protected Obj fromByteArray(byte[] bytes) throws SexpParseException, SexpException, IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            BufferedInputStream bis = new BufferedInputStream(bais);
            ObjInputStream ois = new ObjInputStream(bis);
            return ois.readObj();
        }
    }

    static class CanonicalMarshallingTest extends AbstractMarshallingTest {

        public CanonicalMarshallingTest(Obj obj) {
            super("Canonical", obj);
        }

        protected byte[] toByteArray(Obj obj) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(baos);
            ObjOutputStream oos = new ObjOutputStream(bos);
            oos.writeCanonical(obj);
            oos.close();
            return baos.toByteArray();
        }
    }

    static class ReadableMarshallingTest extends AbstractMarshallingTest {

        static final int OFFSET = 2;

        static final int WIDTH = 80;

        static final int LAST = 2;

        public ReadableMarshallingTest(Obj obj) {
            super("Readable", obj);
        }

        protected byte[] toByteArray(Obj obj) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(baos);
            ObjOutputStream oos = new ObjOutputStream(bos);
            oos.writeReadable(obj, OFFSET, WIDTH, LAST);
            oos.close();
            return baos.toByteArray();
        }
    }

    static class TransportMarshallingTest extends AbstractMarshallingTest {

        public TransportMarshallingTest(Obj obj) {
            super("Transport", obj);
        }

        protected byte[] toByteArray(Obj obj) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(baos);
            ObjOutputStream oos = new ObjOutputStream(bos);
            oos.writeTransport(obj);
            oos.close();
            return baos.toByteArray();
        }
    }

    public static Test suite() {
        // just check objArray was initialised correctly
        if (objArray == null) { throw new IllegalStateException("jsdsi.Obj[] is null"); }
        TestSuite s = new NamedTestSuite("SexpMarshallingTest");
        // check tag self-equality, intersection, implication
        for (int i = 0; i < objArray.length; i++) {
            s.addTest(new CanonicalMarshallingTest(objArray[i]));
            s.addTest(new ReadableMarshallingTest(objArray[i]));
            s.addTest(new TransportMarshallingTest(objArray[i]));
        }
        return s;
    }

}
