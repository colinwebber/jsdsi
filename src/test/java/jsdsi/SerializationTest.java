/*
 * Copyright ©, Aegeus Technology Limited. All rights reserved.
 */
package jsdsi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Sean Radford
 * @version $Revision: 1.1 $ $Date: 2004/12/15 19:18:11 $
 */
public class SerializationTest extends TestCase {

    private Object obj;

    private static Object[] toTest = new Object[] { new Auth(new StringTag("test"), false),
            new Auth(Tag.NULL_TAG, false), new Auth(Tag.ALL_TAG, false) };

    /**
     * @param arg0
     */
    public SerializationTest(Object obj) {
        super("testRun");
        this.obj = obj;
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite() {
        TestSuite ts = new TestSuite();

        for (int index = 0; index < toTest.length; index++) {
            ts.addTest(new SerializationTest(toTest[index]));
        }

        return ts;

    }

    public void testRun() throws Exception {
        System.out.println("testRun...");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this.obj);
        oos.close();
        byte[] bytes = baos.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object out = ois.readObject();
        assertEquals(this.obj.getClass().getName(), this.obj, out);
    }

}