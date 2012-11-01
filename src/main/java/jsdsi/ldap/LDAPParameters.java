package jsdsi.ldap;

/**
 * LDAP server parameters. Server name(default: "localhost"), 
 * server port(default: 389), login(default: "anonymous"), 
 * password(default: null), base dn
 * 
 * 
 * @author Luís Pedro
 * @version $Revision: 1.5 $ $Date: 2004/03/27 19:15:39 $
 *
 **/

public class LDAPParameters {

	/**
	 * Default ldap port 389
	 */
	private static final int LDAP_DEFAULT_PORT = 389;
	
	/**
	 * LDAPport parameter
	 */
	private int LDAPport;
	
	/**
	 * LDAPserver parameter
	 */
	private String LDAPserver;
	
	/**
	 * LDAPlogin parameter
	 */
	private String LDAPlogin;
	
	/**
	 * LDAPbaseDN parameter
	 */
	private String LDAPbaseDN;
	
	/**
	 * LDAPpassword parameter
	 */
	private String LDAPpassword;
	
	/**
	 * Creates a new LDAPParameters instance with specified
	 * LDAPserver, LDAPport, LDAPbaseDN, LDAPlogin andLDAPpassword
	 * 
	 * @param LDAPserver ldap server name 
	 * @param LDAPport ldap server port
	 * @param LDAPbaseDN ldap server distinguish base name
	 * @param LDAPlogin user name
	 * @param LDAPpassword user password
	 */
	public LDAPParameters(String LDAPserver, int LDAPport, String LDAPbaseDN, String LDAPlogin, String LDAPpassword) {
		assert(LDAPserver != null) : "A LDAP server is required";
		assert(LDAPbaseDN != null) : "A LDAP base DN is required";
		
		this.LDAPserver = LDAPserver;
		this.LDAPport = LDAPport;
		this.LDAPbaseDN = LDAPbaseDN;
		this.LDAPlogin = LDAPlogin;
		this.LDAPpassword = LDAPpassword;
	}
	
	/**
	 * Creates a new instance of LDAPParameters instance with specified
	 * LDAPserver, LDAPport and LDAPbaseDN
	 * 
	 * @param LDAPserver ldap server name 
	 * @param LDAPport ldap server port
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPParameters(String LDAPserver, int LDAPport, String LDAPbaseDN) {
		this(LDAPserver, LDAPport, LDAPbaseDN, new String(), new String());
	}
	
	/**
	 * Creates a new instance of LDAPParameters instance with specified
	 * LDAPserver and LDAPbaseDN
	 * 
	 * @param LDAPserver ldap server name 
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPParameters(String LDAPserver, String LDAPbaseDN) {
		this(LDAPserver, LDAP_DEFAULT_PORT, LDAPbaseDN, new String(), new String());
	}
	
	/**
	 * Creates a new instance of LDAPParameters with specified
	 * LDAPbaseDN
	 * 
	 * @param LDAPbaseDN ldap server distinguish base name
	 */
	public LDAPParameters(String LDAPbaseDN) {
		this("localhost", LDAP_DEFAULT_PORT, LDAPbaseDN, new String(), new String());
	}
	
	/**
	 * Creates a new instance of LDAPParameters with specified
	 * LDAPserver, LDAPserverDN, LDAPlogin and LDAPpassword 
	 * 
	 * @param LDAPserver ldap server name 
	 * @param LDAPbaseDN ldap server distinguish base name
	 * @param LDAPlogin user name
	 * @param LDAPpassword user password
	 */
	public LDAPParameters(String LDAPserver, String LDAPbaseDN,String LDAPlogin, String LDAPpassword) {
		this(LDAPserver, LDAP_DEFAULT_PORT, LDAPbaseDN, LDAPlogin, LDAPpassword);
	}
	
	/**
	 * Creates a new instance of LDAPParameters with specified
	 * LDAPbaseDN, LDAPlogin and LDAPpassword
	 * 
	 * @param LDAPbaseDN ldap server distinguish base name
	 * @param LDAPlogin user name
	 * @param LDAPpassword user password
	 */
	public LDAPParameters(String LDAPbaseDN,String LDAPlogin, String LDAPpassword) {
		this("localhost", LDAP_DEFAULT_PORT, LDAPbaseDN, LDAPlogin, LDAPpassword);
	}
	
	/**
	 * Ldap server name
	 * 
	 * @return server name
	 */
	public String getLDAPserver() {
		return LDAPserver;
	}
	
	/**
	 * Ldap server port
	 * 
	 * @return server port
	 */
	public int getLDAPport() {
		return LDAPport;
	}
	
	/**
	 * Ldap user name
	 * 
	 * @return user name
	 */
	public String getLDAPlogin() {
		return LDAPlogin;
	}
	
	/**
	 * Ldap user password
	 * 
	 * @return user password
	 */
	public String getLDAPpassword() {
		return LDAPpassword;
	}
	
	/**
	 * Ldap base dn 
	 * 
	 * @return ldap distinguish base name 
	 */
	public String getLDAPbaseDN() {
		return LDAPbaseDN;
	}
	
	/**
	 * Copy of LDAPParameters 
	 * 
	 * @return LDAPParameters
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch(CloneNotSupportedException e) {
			/* If this happens something is very wrong */
			throw new InternalError(e.toString());
		}
	}
	
	/**
	 * String representation of LDAPParameters
	 * 
	 * @return String representation of ldap parameters
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("LDAPParameters: [\n");
		sb.append("	serverName: " + LDAPserver + "\n");
		sb.append("	port: " + LDAPport + "\n");
		sb.append("	login: " + LDAPlogin + "\n");
		sb.append("	password: " + LDAPpassword + "\n");
		sb.append("	baseDN: " + LDAPbaseDN + "\n");
		sb.append("]");
		return sb.toString();
	}
}
