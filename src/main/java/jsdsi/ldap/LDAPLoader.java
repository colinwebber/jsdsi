package jsdsi.ldap;

import java.io.*;
import java.math.BigInteger;
import java.security.cert.*;
import java.util.*;
import java.net.*;
import jsdsi.*;
import jsdsi.util.DigestAlgoEnum;
import jsdsi.util.SignatureAlgoEnum;

/**
 * @see jsdsi.util.Loader
 * 
 * @author Luís Pedro
 * @version $Revision: 1.7 $ $Date: 2004/11/08 12:08:08 $
 *
 **/

public class LDAPLoader {
	
	/**
	 * id to key
	 */
	private Map keys = new HashMap();
	
	/**
	 * key to id
	 */
	private HashMap key2id = new HashMap();
	
	/**
	 *  names
	 */
	private Set nameSet = new HashSet();
	
	/**
	 * Creates a new LDAPLoader from a given filename and LDAPParameters
	 * 
	 * @param filename filename to read the certificates from
	 * @param params ldap parameters
	 * @throws IOException if an error occurs reading the file filename
	 */
	public LDAPLoader(String filename, LDAPParameters params) throws IOException {
		String certCn = "sdsi.";
		LineNumberReader in = new LineNumberReader(new FileReader(filename));
		in.setLineNumber(1);
		for (String line = in.readLine(); line != null; line = in.readLine()) {
			StringTokenizer t = new StringTokenizer(line);
			if (t.countTokens() == 0) {
				continue; // skip empty lines
			}
			if (t.countTokens() < 4) {
				throw new IOException(
					"bad input on line " + in.getLineNumber() + ": " + line);
			}
			jsdsi.Principal issuer = getPrincipal(t.nextToken());
			String name = t.nextToken();
			String arrow = t.nextToken();
			if (!arrow.equals("->")) {
				throw new IOException(
					"bad arrow on line " + in.getLineNumber() + ": " + line);
			}
			jsdsi.Principal sub = getPrincipal(t.nextToken());
			String[] names = new String[t.countTokens()];
			for (int i = 0; i < names.length; i++) {
				names[i] = t.nextToken();
			}
			Subject subject = (names.length > 0)
					? (Subject)new Name(sub, names)
					: (Subject)sub;
			
			// create a fake certificate with a fake signature and fake validity
			Cert c;
			Calendar date = Calendar.getInstance();
			date.set(1, date.get(1) + 1); // adds an year to the current year			
			
			if (name.startsWith("!") || name.startsWith("+")) {
				// this is an auth cert
				Tag tag = new StringTag(name.substring(1));
				c = new AuthCert(issuer, subject, new Validity(null, date.getTime()), null, null, tag, name.startsWith("+"));
				// propagate?
			} else {
				// this is a name cert
				nameSet.add(new Name(issuer, name));
				c = new NameCert(issuer, subject, new Validity(null, date.getTime()), null, null, name);
			}
			jsdsi.Signature s = new jsdsi.Signature(issuer, new Hash("md5", "HASH-VALUE".getBytes(), null), "rsa-pkcs1-md5",
																			 "SIGNATURE-VALUE".getBytes());
			try {
				LDAPOperations util = new LDAPOperations(params);
				util.storeCertificate(certCn + (in.getLineNumber() - 1), new jsdsi.Certificate(c, s));
			} catch(CertificateException e) {
				throw new Error(e);
			}
		}
	}
	
	/**
	 * Writes to file a collection of spki based certificates
	 * 
	 * @param filename filename to write the certificates
	 * @param certs collection of certificates
	 */
	public void loaderOut(String filename, Collection certs) {
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			PrintStream ps = new PrintStream(fos);
			Iterator allCerts = certs.iterator();
			while(allCerts.hasNext()) {
				Cert cert = ((jsdsi.Certificate)allCerts.next()).getCert();
				String issuer;
				if(cert instanceof NameCert) {
					Name fullName = ((NameCert)cert).getFullName();
					issuer = fullName.getIssuer().toString();
					String issuerName = fullName.getNames()[0];
					ps.print(getKey2Id(issuer) + " " + issuerName + " -> ");
				}
				if(cert instanceof AuthCert) {
					String tag = ((StringTag)((AuthCert)cert).getTag()).getValue();
					boolean delegate = ((AuthCert)cert).getPropagate();
					issuer = ((AuthCert)cert).getIssuer().toString();
					if(delegate == true)
						ps.print(getKey2Id(issuer) + " +" + tag + " -> ");
					else
						ps.print(getKey2Id(issuer) + " !" + tag + " -> ");
				}
				if(cert.getSubject() instanceof Name) {
					Name subject = (Name)cert.getSubject();
					String[] names = subject.getNames();
					String sub = subject.getIssuer().toString();
					ps.print(getKey2Id(sub.toString()) + " ");
					for(int i = 0; i < names.length; i++)
						ps.print(names[i] + " ");
					ps.print("\n");
				} else {
					PublicKey subject = (PublicKey)cert.getSubject();
					ps.println(getKey2Id(subject.toString()));
				}
					
			}
			ps.flush();
			fos.flush();
			ps.close();
			fos.close();
		}catch(IOException e) {
			throw new Error(e);
		}
	}
	
	/**
	 * @see jsdsi.util.Loader#getKeys()
	 * 
	 */
	public Collection getKeys() {
		return keys.values();
	}
	
	/**
	 * @see jsdsi.util.Loader#getNames()
	 * 
	 */
	public Collection getNames() {
		return nameSet;
	}
	
	/**
	 * RSAPublicKey id 
	 * 
	 * @param RSAPublicKey string representation
	 * @return id of the RSAPublicKey
	 */
	private String getKey2Id(String RSAPublicKey) {
		return (String)key2id.get(RSAPublicKey);
	}
	
	private jsdsi.Principal getPrincipal(String id) {
		RSAPublicKey k = (RSAPublicKey)keys.get(id);
		if (k == null) {
			k = new RSAPublicKey(new BigInteger(id.getBytes()), new BigInteger(new byte[] { 0x03 }),
					                              "MD5/RSA/PKCS#1", (URI[])null);
			keys.put(id, k);
			key2id.put(k.toString(), id);			
		}
		return k;
	}
}
