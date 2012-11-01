/**
 * @author Luís Pedro
 * @version $Revison: 1 $Date: 27/Mar/2004 17:41:48
 *
 **/
package jsdsi.ldap;
/**
 * This exception specifies that an special error ocurred on the search, mostly used to 
 * verify if the search has results
 * 
 * @author Luís Pedro
 * @author Sean Radford
 * @version $Revison: 1.1 $Date: 27/Mar/2004 18:13:15
 *
 */

public class LDAPSearchException extends Exception {
	
    private static final long serialVersionUID = -1120303142290208448L;
    
	/**
	 * New default instance of the exception
	 *
	 */
	public LDAPSearchException() {
		super("No Such Objects (80) No Such Objects");
	}
	
	/**
	 * New custom instance of the exception
	 * 
	 * @param msg a custom message to return when exception occurr
	 * @param code a custom code to return when exception occurr
	 */		
	public LDAPSearchException(String msg, int code) {
		super(msg + " (" + code + ") " + msg);
	}

}
