/*
 * Copyright 2002 Massachusetts Institute of Technology
 *   
 * Permission to use, copy, modify, and distribute this program for any
 * purpose and without fee is hereby granted, provided that this
 * copyright and permission notice appear on all copies and supporting
 * documentation, the name of M.I.T. not be used in advertising or
 * publicity pertaining to distribution of the program without specific
 * prior permission, and notice be given in supporting documentation that
 * copying and distribution is by permission of M.I.T.  M.I.T. makes no
 * representations about the suitability of this software for any
 * purpose.  It is provided "as is" without express or implied warranty.
 */
package jsdsi;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.Security;
import java.security.cert.CertificateFactory;

/**
 * A cryptographic provider for SPKI/SDSI and S-expressions.  Fully
 * specified by the file jsdsi.properties.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.3 $ $Date: 2005/02/19 18:02:45 $
 */
public class Provider extends java.security.Provider {
    
    private static final long serialVersionUID = 7311250711851107843L;
    
	/**
	 * @see java.lang.Object#Object()
	 */
	public Provider() {
		super("JSDSI", 1, "JSDSI provider");
		try {
			load(
				Provider.class.getClassLoader().getResourceAsStream(
					"jsdsi/jsdsi.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new certificate and key factory.
	 * 
	 * @param  args command-line arguments (not considered).
	 * @throws Exception if an error occurs.
	 */
	public static void main(String[] args) throws Exception {
		Provider p = new Provider();
		CertificateFactory cf = CertificateFactory.getInstance("SPKI/SEXP", p);
		KeyFactory kf = KeyFactory.getInstance("SPKI/SEXP", p);
	}

	/**
	 * Installs the new cryptographic provider.
	 */
	public static void install() {
        java.security.Provider p = Security.getProvider("JSDSI");
        if (p == null) {
            Security.addProvider(new Provider());
        }
	}
}