/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.sexp;

import java.io.ByteArrayInputStream;

import jsdsi.Obj;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/08/25 23:18:11 $
 */
public abstract class ObjThreadedObjectParsingTestCase extends ThreadedObjectParsingTest {

    ObjThreadedObjectParsingTestCase(String type, int numOfObjects, int numOfThreads) {
        super(type, numOfObjects, numOfThreads);
    }
    
    protected byte[] getBytes(Object object) {
        return ((Obj)object).toByteArray();
    }
    
    Object generateObject(byte[] bytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjInputStream ois = new ObjInputStream(bais);
        return ois.readObj();
    }

}
