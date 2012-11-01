package jsdsi.ldap;

import java.util.*;
import java.io.*;
import jsdsi.*;
import java.security.*;
import java.security.cert.*;
import com.novell.ldap.*;

/**
 * LDAP based certificate store for SPKI/SDSI certificates
 * 
 * @author Luís Pedro
 * @version $Revision: 1.5 $ $Date: 2004/03/29 21:27:58 $
 *
 **/

public class LDAPCertStore extends CertStoreSpi {
	
	/**
	 * LDAP Connection instance
	 */ 
	private LDAPConnection lc;
	
	/**
	 * LDAP scope used to perform searchs.
	 * SubTree scope adopted
	 */
	private int searchScope = LDAPConnection.SCOPE_SUB;
	
	/**
	 * LDAPport parameter
	 */
	private int LDAPport;
	
	/**
	 * LDAPSchema parameter
	 */
	private LDAPSchema dirSchema;
	
	/**
	 * LDAPbaseDN parameter
	 */
	private String LDAPbaseDN;
	
	/**
	 * LDAPserver parameter
	 */
	private String LDAPserver;
		
	/**
	 * Instanciate LDAPCertStore
	 * 
	 * @param params cert store parameters
	 * @throws CertStoreException
	 */
	private void init(LDAPCertStoreParameters params) throws CertStoreException {
		lc = new LDAPConnection();
		LDAPserver = params.getLDAPserver();
		LDAPport = params.getLDAPport();
		LDAPbaseDN = params.getLDAPbaseDN();
		dirSchema = null;			
	}
	
	/**
	 * Performe searchs on LDAPCertStore retrieving the
	 * a collection of sdsi certificates
	 * 
	 * @param filterMD5 MD5 filter
	 * @param filterSHA1 SHA1 filter
	 * @return collection of sdsi certificates
	 * @throws CertStoreException
	 */	
	private Set get(String filterMD5, String filterSHA1) throws CertStoreException {
		LDAPSearchResults searchResults = null;
		Set certs = new HashSet();		
		try {
			// start default search MD5, if it fails try to search SHA1 
			searchResults = lc.search(LDAPbaseDN, searchScope, filterMD5, new String[] {"canonicalSexp"}, false);
			if(!searchResults.hasMore())
				searchResults = lc.search(LDAPbaseDN, searchScope, filterSHA1, new String[] {"canonicalSexp"}, false);
			
			while(searchResults.hasMore()) {													                       
				LDAPEntry nextEntry = null;
				nextEntry = searchResults.next();
				ByteArrayInputStream bis = new ByteArrayInputStream(nextEntry.getAttribute("canonicalSexp").getByteValue());
				jsdsi.sexp.ObjInputStream ois = new jsdsi.sexp.ObjInputStream(bis);
				jsdsi.Certificate cert = jsdsi.Certificate.fromSequence((jsdsi.Sequence)ois.readObj());
				certs.add(cert);
			}
			lc.disconnect();
		} catch(LDAPException e) {	
			throw new CertStoreException("Ldap internal error");
		} catch(java.security.cert.CertificateException e) {
			throw new CertStoreException("Unable to read certificates");
		} catch(Exception e) {	
			throw new CertStoreException("Corrupted certificates");
		} 
		return certs;
	}
	
	/**
	 * Creates a new instance of LDAPCertStore with specified ldap parameters
	 * 
	 * @see LDAPCertStoreParameters
	 * 
	 * @param params ldap cert store parameters
	 * @throws InvalidAlgorithmParameterException, CertStoreException
	 */
	public LDAPCertStore(LDAPCertStoreParameters params) throws InvalidAlgorithmParameterException, CertStoreException {
		super(params);
		init(params);
	}
	
	/**
	 * @see java.security.cert.CertStoreSpi#CertStoreSpi(CertStoreParameters)
	 */
	public LDAPCertStore(CertStoreParameters params) throws Exception {
		super(params);
		try {
			init((LDAPCertStoreParameters)params);
		} catch (ClassCastException e) {
			throw (InvalidAlgorithmParameterException) new InvalidAlgorithmParameterException().initCause(e);
		}
	}
	
	/**
     * @see java.security.cert.CertStoreSpi#engineGetCertificates(java.security.cert.CertSelector)
	 */
	public Collection engineGetCertificates(java.security.cert.CertSelector s) throws CertStoreException {
		if (!(s instanceof jsdsi.CertSelector)) {
			throw new CertStoreException("requires jsdsi.CertSelector");
		}
		return engineGetCertificates((jsdsi.CertSelector)s);
	}
	
	/**
	 * @see java.security.cert.CertStoreSpi#engineGetCertificates(java.security.cert.CertSelector)
	 */
	public Collection engineGetCertificates(jsdsi.CertSelector s) throws CertStoreException {
		try {
			lc.connect(LDAPserver, LDAPport);
		} catch(LDAPException e) {	
			throw new CertStoreException("Server down or wrong connection parameters");
		}
		if (s instanceof SubjectCertSelector) {
			jsdsi.Subject subject = ((SubjectCertSelector) s).getSubject();
			return get(LDAPAttributes.setSubjectFilter(subject, "md5"), LDAPAttributes.setSubjectFilter(subject, "sha1"));				
		}
		if (s instanceof CompatibleCertSelector) {
			jsdsi.Principal subject = ((CompatibleCertSelector) s).getIssuer();
			String name = ((CompatibleCertSelector) s).getName();		
			return get(LDAPAttributes.setCompatibleFilter(subject, name, "md5"), LDAPAttributes.setCompatibleFilter(subject, name, "sha1"));
		}
		if (s instanceof NameCertSelector) {
			jsdsi.Principal issuer = ((NameCertSelector) s).getIssuer();
			String name = ((NameCertSelector) s).getName(); 
			return get(LDAPAttributes.setNameFilter(issuer, name, "md5"), LDAPAttributes.setNameFilter(issuer, name, "sha1"));
		}
		if (s instanceof AuthCertSelector) {
			jsdsi.Principal issuer = ((AuthCertSelector) s).getIssuer();
			return get(LDAPAttributes.setAuthFilter(issuer, "md5"), LDAPAttributes.setAuthFilter(issuer, "sha1"));
		}
		throw new CertStoreException("unrecognized selector: " + s.getClass().getName());				
	}
	
	/**
	 * @see java.security.cert.CertStoreSpi#engineGetCRLs(CRLSelector)
	 */
	public Collection engineGetCRLs(java.security.cert.CRLSelector s) throws CertStoreException {
		throw new UnsupportedOperationException();
	}
}