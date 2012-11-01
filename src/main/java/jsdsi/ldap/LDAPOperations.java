package jsdsi.ldap;

import java.io.*;
import jsdsi.*;
import jsdsi.sexp.*;
import jsdsi.util.DigestAlgoEnum;

import com.novell.ldap.*;

/**
 * LDAP operations. Insert, retrieve and delete
 * sdsi certificates from a LDAP server
 * 
 * @author Luís Pedro
 * @author Sean Radford
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 *
 **/

public class LDAPOperations extends LDAPOp {
	
	/**
	 * Create a new instance of LDAPOperations
	 * 
	 * @param params ldap parameters
	 */
	public LDAPOperations(LDAPParameters params) {
		super(params);
	}
	
	/**
	 * Delete all based SPKI certificates from an
	 * LDAP server - CAUTION: The certificates no longer be available.
	 * 
	 * @return true if the operation was executed with sucess
	 * @throws LDAPSearchException custom error for an empty search, not ldap server error
	 */
	public boolean deleteAllCertificates() throws LDAPSearchException {
		try {
			int searchScope = LDAPConnection.SCOPE_SUB;		
			LDAPBindConnection();		
			LDAPSearchResults searchResults = LDAPSearch("objectClass=sdsiCertificate", searchScope, new String[] {"cn"}); 
			
			if(!searchResults.hasMore())
				throw new LDAPSearchException();
			
			while(searchResults.hasMore()) {
				LDAPEntry nextEntry = null;
				nextEntry = searchResults.next();
				LDAPAttribute attribute =  nextEntry.getAttribute("cn");
				LDAPDelete(setCn(attribute.getStringValue()));
			}			
			LDAPDisconnection();	
		} catch(LDAPException e) {
			System.err.println(e);
			System.exit(0);
		} 
		return true;	
	}
	
	/**
	 * Delete a sdsi certificate from an LDAP server with a
	 * specified cn
	 * 
	 * @param cn cn ldap schema attribute that represents certificate name
	 * @return true if the operation was executed with sucess 
	 * 
	 * @todo delete certificates using NameCertSelector and CompatibleCertSelector
	 */	
	public boolean deleteCertificate(String cn) {
		try {		
			LDAPBindConnection();
			LDAPDelete(setCn(cn));
			LDAPDisconnection();
		} catch(LDAPException e) {
			System.err.println(e);
			System.exit(0);
		}
		return true;	
	}
	
	/**
	 * Retrieves a sdsi certificate from an LDAP server with a
	 * specified cn
	 * 
	 * @see jsdsi.Certificate#fromSequence(Sequence)
	 * 
	 * @param cn cn ldap schema attribute that represents certificate name
	 * @return certificate sdsi certificate
	 * 
	 * @todo retrieve certificates using other attributes and CertSelectors
	 */
	public jsdsi.Certificate retrieveCertificate(String cn) {
		try {
			int searchScope = LDAPConnection.SCOPE_BASE;
			jsdsi.Certificate cert = null;
			LDAPConnection();
			LDAPSearchResults searchResults = LDAPSearch("objectClass=sdsiCertificate", setCn(cn), searchScope, new String[] {"canonicalSexp"});
			
			LDAPEntry entry = null;
			entry = searchResults.next();
			LDAPAttribute attribute =  entry.getAttribute("canonicalSexp");
			ByteArrayInputStream bis = new ByteArrayInputStream(attribute.getByteValue());
			jsdsi.sexp.ObjInputStream ois = new jsdsi.sexp.ObjInputStream(bis);
			cert = jsdsi.Certificate.fromSequence((jsdsi.Sequence)ois.readObj());
			LDAPDisconnection();	
			
			return cert;
		} catch(Exception e) {
			System.err.println(e);
			System.exit(0);
		}
		return null;
	}
	
	/**
	 * Store a sdsi certificate into an LDAP server with 
	 * a specified cn, default hash algorithm "md5" is 
	 * assumed
	 * 
	 * @param cn cn ldap schema attribute that represents certificate name
	 * @param certificate sdsi certificate
	 */
	public void storeCertificate(String cn, jsdsi.Certificate certificate) {		
		storeCertificate(cn, certificate, "md5");
	}
	
	/**
	 * Store a sdsi certificate into an LDAP server with a 
	 * specified cn and a hash algorithm  
	 * 
	 * @see jsdsi.Certificate#getEncoded()
	 * 
	 * @param cn cn ldap schema attribute that represents certificate name
	 * @param certificate sdsi certificate
	 * @param hashAlg hash algorithm to use with the public keys, "md5" or "sha1"
	 */
	public void storeCertificate(String cn, jsdsi.Certificate certificate, String hashAlg) {
		LDAPAttributeSet attributeSet = new LDAPAttributeSet();
		LDAPAttribute attribute = null; 	
		Cert cert = certificate.getCert();
		
		attributeSet.add(new LDAPAttribute("objectclass", new String[] {"top", "sdsiCertificate"}));		
		attributeSet.add(new LDAPAttribute(getCn(), cn)); 
		attributeSet.add(new LDAPAttribute(getCanonicalSexp(), Sexp.decodeString(certificate.toSequence().toTransport())));
		
		if(cert instanceof NameCert)
			attributeSet.add(new LDAPAttribute(getIssuerName(), ((NameCert)cert).getName()));

		if(cert.getIssuer() instanceof PublicKeyHash)   
			attributeSet.add(new LDAPAttribute(getIssuer(), Sexp.decodeString(cert.getIssuer().toTransport())));	
		else { 
			Hash issuerHash = new Hash(DigestAlgoEnum.fromSpki(hashAlg), cert.getIssuer(), null);
			attributeSet.add(new LDAPAttribute(getIssuer(), Sexp.decodeString(issuerHash.toTransport())));
		}
		
		if(cert.getSubject() instanceof Name) { 
			Name n = (Name)cert.getSubject();
			if(n.getIssuer() instanceof PublicKeyHash) 
				attributeSet.add(new LDAPAttribute(getSubject(), Sexp.decodeString(n.getIssuer().toTransport())));
			else {
				Hash subjectHash = new Hash(DigestAlgoEnum.fromSpki(hashAlg), n.getIssuer(), null);
				attributeSet.add(new LDAPAttribute(getSubject(), Sexp.decodeString(subjectHash.toTransport())));
			}		
			attributeSet.add(new LDAPAttribute(getSubjectName(), n.getNames()[0]));
		} else if(cert.getSubject() instanceof PublicKeyHash) {
			attributeSet.add(new LDAPAttribute(getSubject(), Sexp.decodeString(((PublicKeyHash)cert.getSubject()).toTransport())));
		} else if(cert.getSubject() instanceof PublicKey) {
			Hash subjectHash = new Hash(DigestAlgoEnum.fromSpki(hashAlg), (PublicKey)cert.getSubject(), null);
			attributeSet.add(new LDAPAttribute(getSubject(), Sexp.decodeString(subjectHash.toTransport())));
		} else
			throw new IllegalArgumentException("Unsupported subject type");
		
		if(cert.getValidity() == null || cert.getValidity().getNotAfter() == null) 
			throw new IllegalArgumentException("notAfter is required");
		else
			attributeSet.add(new LDAPAttribute(getNotAfter(), cert.getValidity().getNotAfter().toString()));
		
		try {
			LDAPBindConnection();
			LDAPStore(setCn(cn), attributeSet);
			LDAPDisconnection();
		} catch(LDAPException e) {
			System.err.println(e);
			System.exit(0);
		}
	}
	
	/**
	 * Writes all based SPKI certificates on a LDAP server to a file.
	 * This file MUST not be used to store certificates on the ldap server
	 * using ldap command line commands 
	 * 
	 * @param filename filename to write the certificates
	 */
	public void toFile(String filename) {
		try {
			int searchScope = LDAPConnection.SCOPE_SUB;		
			LDAPBindConnection();
			LDAPSearchResults searchResults = LDAPSearch("objectClass=sdsiCertificate", searchScope, new String[] {"canonicalSexp"});
		
			FileOutputStream fos = new FileOutputStream(filename);
			ObjOutputStream oos = new ObjOutputStream(fos);
			
			while(searchResults.hasMore()) {
				LDAPEntry nextEntry = null;
				nextEntry = searchResults.next();
				LDAPAttribute attribute =  nextEntry.getAttribute("canonicalSexp");
				ByteArrayInputStream bis = new ByteArrayInputStream(attribute.getByteValue());
				jsdsi.sexp.ObjInputStream ois = new jsdsi.sexp.ObjInputStream(bis);
				jsdsi.Certificate cert = jsdsi.Certificate.fromSequence((jsdsi.Sequence)ois.readObj());
				oos.writeReadable(Obj.parseObj(cert.toSequence().toSexp()), 3, 110, 5);
			}
			
			oos.close();
			fos.close();
			LDAPDisconnection();
		} catch(Exception e) {
			System.err.println(e);
			System.exit(0);
		}
	}
}
