package jsdsi.ldap;

import java.security.cert.CertStoreParameters;

/**
 * LDAP certificate store parameters. Parameters needed
 * to performe certificate store operations
 * 
 * @author Luís Pedro
 * @version $Revision: 1.4 $ $Date: 2004/03/27 19:13:56 $
 *
 **/

public class LDAPCertStoreParameters extends LDAPParameters implements CertStoreParameters {

	/**
	 * Creates a new instance of LDAPCertStoreParameters with a specified 
	 * server name, port and base dn
	 * 
	 * @param LDAPserver ldap server name 
	 * @param LDAPport ldap server port
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPCertStoreParameters(String LDAPserver, int LDAPport, String LDAPbaseDN) {
		super(LDAPserver, LDAPport, LDAPbaseDN);	
	}
	
	/**
	 * Creates a new instance of LDAPCertStoreParameters with the 
	 * specified server name and base dn
	 * 
	 * @param LDAPserver ldap server name
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPCertStoreParameters(String LDAPserver, String LDAPbaseDN) {
		super(LDAPserver, LDAPbaseDN);
	}
	
	/**
	 * Creates a new instance of LDAPCertStoreParameters with the 
	 * specified base dn
	 * 
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPCertStoreParameters(String LDAPbaseDN) {
		super(LDAPbaseDN);
	}
	
	/**
	 * Ldap server name 
	 * 
	 * @return ldap server name
	 */
	public String getLDAPserver() {
		return super.getLDAPserver();
	}
	
	/**
	 * Ldap server port
	 * 
	 * @return ldap server port
	 */
	public int getLDAPport() {
		return super.getLDAPport();
	}
	
	/**
	 * Ldap base dn 
	 * 
	 * @return ldap distinguish base name 
	 */
	public String getLDAPbaseDN() {
		return super.getLDAPbaseDN();
	}
	
	/**
	 * Copy of LDAPCertStoreParameters 
	 *
	 * @return LDAPCertStoreParameters 
	 */
	public Object clone() {
		return super.clone();		
	}
	
	/**
	 * String representation of LDAPCertStoreParameters
	 *
	 * @return String representation of cert store parameters
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LDAPCertStoreParameters: [\n");
		sb.append("	serverName: " + super.getLDAPserver() + "\n");
		sb.append("	port: " + super.getLDAPport() + "\n");
		sb.append("	baseDN: " + super.getLDAPbaseDN() + " ]");
		return sb.toString();
	}
}