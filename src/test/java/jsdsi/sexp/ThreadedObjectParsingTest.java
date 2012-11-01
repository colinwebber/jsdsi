/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.sexp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsdsi.Provider;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/16 12:20:46 $
 */
public abstract class ThreadedObjectParsingTest extends TestCase {

    static {
        Provider.install();
    }
    
    private String type;
    private  int NUM_OBJECTS;
    private  int NUM_THREADS;
    private List byteList;
    private List runners;
    private Exception parsingException;
    
    ThreadedObjectParsingTest(String type, int numOfObjects, int numOfThreads) {
        super("testThreadParsing");
        this.type = type;
        this.NUM_OBJECTS = numOfObjects;
        this.NUM_THREADS = numOfThreads;
    }
    
    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.byteList = new LinkedList();
        this.runners = new LinkedList();
        System.out.println("Setting up test: "+this.type);
        for (int i = 0; i < NUM_OBJECTS; i++) {
            Object o = createObject();
            this.byteList.add(getBytes(o));
        }
        System.out.println("Validity creation complete.");
    }


    public static Test suite() {
        TestSuite s = new TestSuite("ThreadedObjectParsingTest");
        s.addTest(new ValidityThreadedObjectParsingTestCase(1000, 20));
        s.addTest(new CertificateThreadedObjectParsingTestCase(100, 20));
        return s;
    }
    
    
    /** Creates the object to be later parsed (for the test) **/
    protected abstract Object createObject() throws Exception;    
    /** Gets the bytes for an object for byteList */
    protected abstract byte[] getBytes(Object object);
    /** Generates an Object from a byte[] - used for the actual parsing test */
    abstract Object generateObject(byte[] bytes) throws Exception;
    public void testThreadParsing() throws Exception {
        for (int i = 0; i < NUM_THREADS; i++) {
            this.runners.add(new Runner(this, this.byteList));
        }
        Iterator it = this.runners.iterator();
        while (it.hasNext()) {
            ((Runner) it.next()).start();
        }
        boolean done = false;
        startagain:
        while (!done) {
            it = this.runners.iterator();
            while (it.hasNext()) {
                Runner runner = (Runner) it.next();
                if (runner.isAlive()) {
                    Thread.sleep(1000);
                    continue startagain;
                }
            }
            done = true;
        }
        if (parsingException != null) {
            fail(parsingException.toString());
        }
    }
    
    
    class Runner extends Thread {
        private ThreadedObjectParsingTest test;
        private List byteList;
        public Runner(ThreadedObjectParsingTest test, List byteList) {
            super();
            this.test = test;
            this.byteList = byteList;
        }
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            System.out.println("Running test for "+this.test.type);
            Iterator it = this.byteList.iterator();
            int index = -1;
            while (it.hasNext() && this.test.parsingException == null) {
                index++;
                byte[] bytes = (byte[]) it.next();
                try {
                    Object obj = this.test.generateObject(bytes);
                } catch (Exception e) {
                    this.test.parsingException = e;
                }

            }
            System.out.println("Test complete for "+this.test.type);
        }
    }

}
