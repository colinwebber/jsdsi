/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.sexp;

import jsdsi.Validity;
import jsdsi.util.DateUtil;

/**
 * @author Sean Radford
 * @version $Revision: 1.2 $ $Date: 2004/08/25 23:18:11 $
 */
public class ValidityThreadedObjectParsingTestCase extends ObjThreadedObjectParsingTestCase {
    
    ValidityThreadedObjectParsingTestCase(int numOfObjects, int numOfThreads) {
        super("jsdsi.Validity", numOfObjects, numOfThreads);
    }
    
    protected Object createObject() {
        return new Validity(DateUtil.newDate(), DateUtil.newDate(1));
    }

}
