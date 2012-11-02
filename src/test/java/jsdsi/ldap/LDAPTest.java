/**
 * 
 * Creates a CertStore with certificates from a flat file, cert.in.x.
 * For a specified flag that identifies CertSelectors returns files, ldapcerts.out.x,
 * with the result certificates returned from LDAPCertStore.
 * 
 * Flags:
 * 	'a' -> AuthCertSelector,
 * 	's' -> SubjectCertSelector
 * 	'c' -> CompatibleCertSelector
 * 	'n' -> NameCertSelector	
 * 
 * @see LDAPLoader
 * 
 * @author Luís Pedro
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2004/03/27 19:18:14 $
 */
package jsdsi.ldap;

import java.io.*;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import jsdsi.*;

public class LDAPTest extends TestCase {
	
    public LDAPTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
     * JVM property name to indicate to skip this test.
     */
    private static final String LDAP_SKIP_TESTS = "test.ldap.skiptests";
    
    /**
     * JVM property name for the LDAP server name 
     */
    private static final String SERVER_NAME ="test.ldap.servername";
    
    /**
     * JVM property name for the LDAP server port
     */
    private static final String SERVER_PORT ="test.ldap.port";
    
    /**
     * JVM property name for the LDAP server base distinguished name
     */
    private static final String SERVER_BASEDN ="test.ldap.baseDN";
    
    /**
     * JVM property name for the LDAP server login
     */
    private static final String LOGIN ="test.ldap.login";
    
    /**
     * JVM property name for the LDAP server login password
     */
    private static final String PASSWORD ="test.ldap.password";
    
    /**
     * JVM property name for the jsdsi selector to use
     */
    private static final String SELECTOR ="test.ldap.selector";
    
	LDAPParameters params;

	
	public LDAPTest(String name, LDAPParameters params) {
		super(name);
		assert(params != null);
		this.params = params;
	}
	
	public static Test suite() {
		if (skipTest()) {
			// skips the LDAP test (set using a JVM Property of LDAP_SKIP_TESTS )
			return new NamedTestSuite("LDAPTest skipped");
		}
		LDAPParameters params = getLDAPParameters();
		
		TestSuite s = new NamedTestSuite("LDAPTest");
		String flag = getSelectorType();
	
		s.addTest(ldapCertStore(params, flag));
		return s;
	}
	
	private static Test ldapCertStore(LDAPParameters params, String flag) {
		try {
			TestSuite s = new TestSuite();			
			Provider.install();
            LDAPOperations ldap = new LDAPOperations(params);
            LDAPCertStoreParameters storeParams = new LDAPCertStoreParameters(params.getLDAPserver(), params.getLDAPport(), params.getLDAPbaseDN());
            java.security.cert.CertStore store = java.security.cert.CertStore.getInstance("SPKI/LDAP", storeParams);
            File cwd = new File("src/test/java/jsdsi");
            String[] in = cwd.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("certs.in")
                            && !name.endsWith("~");}});
            
            for (int i = 0; i < in.length; i++) {
                try {
                	ldap.deleteAllCertificates(); // delete previous stored certs                	
                } catch(LDAPSearchException e) { 
					 	// used to save time and prevent thread deadlocks
						// test will continue
				}              	
                finally {
					String out = cwd + File.separator + "ldapcerts.out" + in[i].substring("certs.in".length());
                	LDAPLoader inLoad = new LDAPLoader(cwd + File.separator + in[i], params);
                	s.addTest(getLDAPCerts(store, inLoad, out, flag));
				}	
            }
            return s;
        }catch(java.security.NoSuchAlgorithmException e) {
            throw new Error(e);
        }catch(java.security.InvalidAlgorithmParameterException e) {
        	throw new Error(e);
	    }catch(IOException e) {
            throw new Error(e);
        }
	}
	
	private static Test getLDAPCerts(java.security.cert.CertStore store, LDAPLoader in, String out, String flag) {
		try {
			TestSuite s = new TestSuite();
			List certsList = new ArrayList();
			if(flag == "AuthCertSelector") {
				Iterator auth = in.getKeys().iterator();
				while(auth.hasNext()) {
					AuthCertSelector auths = new AuthCertSelector((Principal)auth.next());
					Iterator certs = store.getCertificates(auths).iterator();
					while(certs.hasNext()) {
						Certificate cert = (Certificate)certs.next();
						if(cert.getCert() instanceof AuthCert) 
							certsList.add(cert);
					}
				}
			}	
			if(flag == "NameCertSelector") {
				Iterator name = in.getNames().iterator();
				while(name.hasNext()) {
					Name n = (Name)name.next();
					NameCertSelector names = new NameCertSelector(n.getIssuer(), n.getNames()[0]);
					Iterator certs = store.getCertificates(names).iterator();
					while(certs.hasNext())
						certsList.add(certs.next());
				}
			}	
			if(flag == "SubjectCertSelector") {
				Iterator subject = in.getKeys().iterator();
				while(subject.hasNext()) {
					SubjectCertSelector subjects = new SubjectCertSelector((Subject)subject.next());
					Iterator certs = store.getCertificates(subjects).iterator();
					while(certs.hasNext())
						certsList.add(certs.next());
				}
			}	
			if(flag == "CompatibleCertSelector") {
				Iterator compatible = in.getNames().iterator();
				while(compatible.hasNext()) {
					Name c = (Name)compatible.next();
					CompatibleCertSelector compatibles = new CompatibleCertSelector(c.getIssuer(), c.getNames()[0]);
					Iterator certs = store.getCertificates(compatibles).iterator();
						while(certs.hasNext())
							certsList.add(certs.next());
				}
			}	
			in.loaderOut(out, certsList);
			return s;
		}catch(java.security.cert.CertStoreException e) {
			throw new Error(e);
		}
	}
	
	private static LDAPParameters getLDAPParameters() {
		String servername = getServerName();
		if (servername==null || servername.equals("")) {
		        return new LDAPParameters( getServerBaseDN(), getLogin(), getPassword());
		} else {
		    String port = getServerPort();
		    if (port==null || port.equals("")) { 
		        return new LDAPParameters( servername, getServerBaseDN(), getLogin(), getPassword());
		    } else {
		        int p = Integer.parseInt(port);
		        return new LDAPParameters( servername, p, getServerBaseDN(), getLogin(), getPassword());
		    }
		}
	}
	
	/**
	 * Informs whether to skip this LDAP test by examing the <code>LDAP_SKIP_TESTS</code> JVM property
	 * @return
	 */
	private static boolean skipTest() {
	    return Boolean.getBoolean(LDAP_SKIP_TESTS);
	}
	
	/**
	 * Retrieves LDAP server name to connect to from the <code>SERVER_NAME</code> JVM property
	 * @return
	 */
	private static String getServerName() {
	    return System.getProperty(SERVER_NAME);
	}
	
	/**
	 * Retrieves the LDAP server port to use from the <code>SERVER_PORT</code> JVM property
	 * @return
	 */
	private static String getServerPort() {
	    return System.getProperty(SERVER_PORT);
	}
	
	/**
	 * Retrieves the LDAP server base distinguished name from the <code>SERVER_BASEDN</code> JVM property
	 * @return the base server disinguished name
	 */
	private static String getServerBaseDN() {
	    String v = System.getProperty(SERVER_BASEDN);
	    if (v==null || v.equals("")) {
	        System.out.println("***** PLEASE SPECIFY A SERVER BASE DISTINGUISHED NAME ******");
	        System.out.println("***** SET IT USING JVM PROPERTY NAMED: "+SERVER_BASEDN+" *****");
	        System.out.println("");
	        throw new Error("No LDAP Server Base Distinguished Name specified");
	    } else {
	        return v;
	    }
	}
	
	/**
	 * Retrieves the LDAP login 'name' from the <code>LOGIN</code> JVM property
	 * @return
	 */
	private static String getLogin() {
	    String v = System.getProperty(LOGIN);
	    if (v==null || v.equals("")) {
	        System.out.println("***** PLEASE SPECIFY A LOGIN NAME ******");
	        System.out.println("***** SET IT USING JVM PROPERTY NAMED: "+LOGIN+" *****");
	        System.out.println("");
	        throw new Error("No LDAP Server Login Name specified");
	    } else {
	        return v;
	    }
	}
	
	/**
	 * Retrieves the LDAP login password from the <code>PASSWORD</code> JVM property
	 * @return
	 */
	private static String getPassword() {
	    String v = System.getProperty(PASSWORD);
	    if (v==null || v.equals("")) {
	        System.out.println("***** PLEASE SPECIFY A PASSWORD ******");
	        System.out.println("***** SET IT USING JVM PROPERTY NAMED: "+PASSWORD+" *****");
	        System.out.println("");
	        throw new Error("No LDAP Server Password specified");
	    } else {
	        return v;
	    }
	}
	
	/**
	 * Retrieves the jsdsi selector type to use from the <code>SELECTOR</code> JVM property
	 * @return
	 */
	private static String getSelectorType() {
	    String v = System.getProperty(SELECTOR);
	    if (v==null || v.equals("")) {
	        System.out.println("***** PLEASE SPECIFY A SELECTOR TYPE ******");
	        System.out.println("***** SET IT USING JVM PROPERTY NAMED: "+SELECTOR+" *****");
	        System.out.println("");
	        throw new Error("No jsdsi.CertSelector specified");
	    } else {
	        return v;
	    }
	}
	
    public void testNothingToPreventNoTestsWarning() {
    	
    }
}
