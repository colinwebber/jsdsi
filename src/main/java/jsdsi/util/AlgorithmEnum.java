/*
 * Copyright ©, Aegeus Technology Limited.
 * All rights reserved.
 */
package jsdsi.util;

import java.io.Serializable;


/**
 * <p>Experimental class and as such may be removed without warning.</p>
 * 
 * Abstract Enum class for algorithms
 *  
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/11/08 12:08:08 $
 */
public abstract class AlgorithmEnum implements Serializable {

    /**
     * The JDK name for this algorythm
     */
    private String jdkName;
    
    /**
     * The SPKI (SEXP) name for this algorythm
     */
    private String spkiName;
    
    /**
     * 
     */
    protected AlgorithmEnum(String jdkName, String spkiName) {
        super();
        this.jdkName = jdkName;
        this.spkiName = spkiName;
    }

    public String jdkName() {
        return this.jdkName;
    }
    
    public String spkiName() {
        return this.spkiName;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof AlgorithmEnum) {
            AlgorithmEnum other = (AlgorithmEnum) obj;
            return (this.spkiName.equals(other.spkiName));
        }
        return false;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.spkiName.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.spkiName;
    }
    
}
