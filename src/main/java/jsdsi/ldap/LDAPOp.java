package jsdsi.ldap;

import com.novell.ldap.*;

/**
 *	LDAP composed operations used by jsdsi.ldap.LDAPOperations
 *
 *	@see jsdsi.ldap.LDAPOperations
 * 
 * @author Luís Pedro
 * @version $Revison: 1.3 $Date: 21/Mar/2004 15:11:05
 *
 **/

class LDAPOp extends LDAPAttributes {
	/**
	 * LDAP Schema
	 */
	private LDAPSchema dirSchema;

	/**
	 * LDAP Connection
	 */
	private LDAPConnection lc;
	
	/**
	 * LDAP Parameters
	 */
	private LDAPParameters params;
	
	/**
	 * Create a new instance of LDAPOp
	 * 
	 * @param params ldap parameters
	 */	
	LDAPOp(LDAPParameters params) {
		lc = new LDAPConnection();
		this.params = params;
	}
	
	/**
	 * Ldap connection
	 * 
	 * @throws LDAPException
	 */
	void LDAPConnection() throws LDAPException {
		lc.connect(params.getLDAPserver(), params.getLDAPport());
	}
	
	/**
	 * Ldap connection using user name and password
	 * 
	 * @throws LDAPException
	 */
	void LDAPBindConnection() throws LDAPException {
		lc.connect(params.getLDAPserver(), params.getLDAPport());
		lc.bind(LDAPConnection.LDAP_V3, params.getLDAPlogin(), (params.getLDAPpassword()).getBytes());
	}
	
	/**
	 * Ldap disconnection
	 * 
	 * @throws LDAPException
	 */
	void LDAPDisconnection() throws LDAPException {
		lc.disconnect();
	}
	
	/**
	 * Ldap delete
	 * 
	 * @param composedAttribute composed attribute to delete
	 * @throws LDAPException
	 */
	void LDAPDelete(String composedAttribute) throws LDAPException{
		lc.delete(composedAttribute + params.getLDAPbaseDN());
	}
	
	/**
	 * Adds elements to the ldap server
	 * 
	 * @param composedAttribute composed attribute that identifies the element
	 * @param attributeSet set of attributes to store
	 * @throws LDAPException
	 */
	void LDAPStore(String composedAttribute, LDAPAttributeSet attributeSet) throws LDAPException {
		LDAPEntry newEntry = new LDAPEntry(composedAttribute + params.getLDAPbaseDN(), attributeSet);
		lc.add(newEntry);
	}
	
	/**
	 * Make ldap searchs with a specified ldap schema composed attribute
	 * 
	 * @param filter ldap search filter
	 * @param composedAttribute ldap schema composed attribute
	 * @param searchScope search scope - BASE, SUB, ONE
	 * @param attributes attribute values to return
	 * @return result of search operation over the ldap server
	 * @throws LDAPException
	 */
	LDAPSearchResults LDAPSearch(String filter, String composedAttribute, int searchScope, String[] attributes) throws LDAPException {
		LDAPSearchResults searchResults = null;
		dirSchema = lc.fetchSchema(lc.getSchemaDN());
		if(composedAttribute != null) {
			searchResults = lc.search(composedAttribute + params.getLDAPbaseDN(), searchScope, filter,
					attributes, false);
		} else 
			searchResults = lc.search(params.getLDAPbaseDN(), searchScope, filter, attributes, false);

		return searchResults;		
	}
	
	/**
	 * Make generic ldap searchs
	 * 
	 * @param filter ldap search filter
	 * @param searchScope search scope - BASE, SUB, ONE
	 * @param attributes attribute values to return
	 * @return result of search operation over the ldap server
	 * @throws LDAPException
	 */
	LDAPSearchResults LDAPSearch(String filter, int searchScope, String[] attributes) throws LDAPException {
		return LDAPSearch(filter, null, searchScope, attributes);
	}
	
	/**
	 * Ldap parameters used to perform operations
	 * 
	 * @return ldap parameters
	 */
	public LDAPParameters getParameters() {
		return params;
	}
}
