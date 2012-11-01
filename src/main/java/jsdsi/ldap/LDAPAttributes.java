package jsdsi.ldap;

import jsdsi.*;
import jsdsi.sexp.*;
import jsdsi.util.DigestAlgoEnum;

import java.security.cert.CertStoreException;

/**
 * Jsdsi schema and CertSelectors filters 
 * 
 * Schema attributes: cn, canonicalSexp, subject, issuerName,
 * subjectName and notAfter
 * 
 * @author Luís Pedro
 * @author Sean Radford
 * @version $Revision: 1.6 $ $Date: 2004/11/08 12:08:08 $
 *
 **/

class LDAPAttributes {

	/**
	 * cn attribute
	 */
	private static String cn = "cn";
	
	/**
	 * canonicalSexp attribute
	 */
	private static String canonicalSexp = "canonicalSexp";
	
	/**
	 * issuer attribute
	 */
	private static String issuer = "issuer";
	
	/**
	 * subject attribute
	 */
	private static String subject = "subject";
	
	/**
	 * issuerName attribute
	 */
	private static String issuerName = "issuerName";
	
	/**
	 * subjectName attribute
	 */
	private static String subjectName = "subjectName";
		
	/**
	 * notAfter attribute
	 */
	private static String notAfter = "notAfter";
	
	/**
	 * ampersand
	 */
	private static String ampersand = "&";
	
	/**
	 * comma
	 */
	private static char comma = ',';
	
	/**
	 * equals
	 */
	private static char equals = '=';
	
	/**
	 * left parenthensis
	 */
	private static char leftpar = '(';
	
	/**
	 * right parenthesis
	 */
	private static char rightpar = ')';
	
	/**
	 * Set a composed string with the cn attribute
	 * 
	 * @param commonName cn ldap schema attribute that identifies the certificate
	 * @return String that represents composed commonName
	 */
	static String setCn(String commonName) {
		return(cn + equals + commonName + comma);
	}
	
	/**
	 * Set a composed string with issuerName attribute
	 * 
	 * @param name issuerName ldap schema attribute that represents an issuer name
	 * @return String that represents composed issuer name
	 */
	private static String setIssuerName(String name) {
		return(issuerName + equals + name);
	}
	
	/**
	 * Set a composed string with subjectName attribute
	 * 
	 * @param name subjectName ldap schema attribute that represents an subject name
	 * @return String that represents composed subject name
	 */
	private static String setSubjectName(String name) {
		return(subjectName + equals + name);
	}
	
	/**
	 * Create a filter with a specified attribute
	 * 
	 * @param attribute ldap schema attribute defined on jsdsi schema 
	 * @param obj sdsi object to be written 
	 * @return String that represents a generic filter
	 */
	private static String filter(String attribute, jsdsi.Obj obj) {
		return(attribute + equals + Sexp.decodeString(obj.toTransport()));
	}
	
	/**
	 * Create a subject filter from subject issuer and an
	 * hash algorithm
	 * 
	 * @param principal principal of a subject
	 * @param hashAlg hash algorithm
	 * @return String of a subject filter
	 */
	static String setSubjectFilter(Subject principal, String hashAlg) {
		if(principal instanceof PublicKeyHash)
			return filter(subject, (PublicKeyHash)principal);
		else {
			Hash sHash = new Hash(DigestAlgoEnum.fromJdk(hashAlg), (PublicKey)principal, null);
			return filter(subject, sHash);
		}
	}
	
	/**
	 * Create a auth filter from a principal issuer and an
	 * hash algorithm
	 * 
	 * @param principal principal of a issuer
	 * @param hashAlg hash algorithm
	 * @return String representing an auth filter
	 */
	static String setAuthFilter(Principal principal, String hashAlg) {
		if(principal instanceof PublicKeyHash)
			return filter(issuer, principal);
		else {
			Hash iHash = new Hash(DigestAlgoEnum.fromJdk(hashAlg), principal, null);
			return filter(issuer, iHash);
		}
	}
	
	/**
	 * Create a name filter from a principal issuer a name and
	 * an hash algorithm
	 * 
	 * @param principal principal of a issuer
	 * @param name issuer name
	 * @param hashAlg hash algorithm
	 * @return String representing a name filter
	 */
	static String setNameFilter(Principal principal, String name, String hashAlg) {
		if(principal instanceof PublicKeyHash)
			return(leftpar + ampersand + leftpar + filter(issuer, principal) + rightpar + leftpar + setIssuerName(name) + rightpar + rightpar);
		else {
			Hash iHash = new Hash(DigestAlgoEnum.fromJdk(hashAlg), principal, null);
			return(leftpar + ampersand + leftpar + filter(issuer, iHash) + rightpar + leftpar + setIssuerName(name) + rightpar + rightpar);
		}
	}
	
	/**
	 * Create a compatible filter from a subject issuer a name and
	 * an hash algorithm
	 * 
	 * @param principal principal of a subject
	 * @param name subject name
	 * @param hashAlg hash algorithm
	 * @return String representing a compatible filter
	 * @throws CertStoreException
	 */
	static String setCompatibleFilter(Subject principal, String name, String hashAlg) throws CertStoreException {
		if(principal instanceof PublicKeyHash)
			return(leftpar + ampersand + leftpar + filter(subject, (PublicKeyHash)principal) + rightpar + leftpar + setSubjectName(name) + rightpar + rightpar);	
		else {
			Hash sHash = new Hash(DigestAlgoEnum.fromJdk(hashAlg), (PublicKey)principal, null);
			return(leftpar + ampersand + leftpar + filter(subject, sHash) + rightpar + leftpar + setSubjectName(name) + rightpar + rightpar);
		}		
	}
	
	/**
	 * cn attribute
	 * 
	 * @return cn attribute
	 */
	static String getCn() {
		return cn;
	}
	
	/**
	 * canonicalSexp attribute
	 * 
	 * @return canonicalSexp attribute
	 */
	static String getCanonicalSexp() {
		return canonicalSexp;
	}
	
	/**
	 * issuer attribute
	 * 
	 * @return issuer attribute
	 */
	static String getIssuer() {
		return issuer;
	}
	
	/**
	 * subject attribute
	 * 
	 * @return subject attribute
	 */
	static String getSubject() {
		return subject;
	}
	
	/**
	 * issuerName attribute
	 * 
	 * @return issuerName attribute 
	 */
	static String getIssuerName() {
		return issuerName;
	}
	
	/**
	 * subjectName attribute
	 * 
	 * @return subjectName attribute
	 */
	static String getSubjectName() {
		return subjectName;
	}
	
	/**
	 * notAfter attribute
	 * 
	 * @return notAfter attribute
	 */
	static String getNotAfter() {
		return notAfter;
	}
}
