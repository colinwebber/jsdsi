/*
  Copyright 2002 Massachusetts Institute of Technology
    
  Permission to use, copy, modify, and distribute this program for any
  purpose and without fee is hereby granted, provided that this
  copyright and permission notice appear on all copies and supporting
  documentation, the name of M.I.T. not be used in advertising or
  publicity pertaining to distribution of the program without specific
  prior permission, and notice be given in supporting documentation that
  copying and distribution is by permission of M.I.T.  M.I.T. makes no
  representations about the suitability of this software for any
  purpose.  It is provided "as is" without express or implied warranty.
*/
package jsdsi;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.util.Iterator;
import java.util.Set;

import jsdsi.AuthCert;
import jsdsi.Cert;
import jsdsi.IssuerCertPathParameters;
import jsdsi.Name;
import jsdsi.NameCert;
import jsdsi.PublicKey;
import jsdsi.SubjectCertPathParameters;
import jsdsi.Tag;
import jsdsi.util.Loader;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
   Tests the cert path builder and validator.  For each pair of files
   called "certs.inX" and "certs.outX" (for any string X, including the
   empty string) in the local directory, this test runs the following
   suite:

   <p>First, this test loads the certificates in certs.inX into a
   CertStore using a Loader.  Then, for each local name N and each key K
   defined in certs.inX, this test attempts to build a cert path that
   proves (N -> K) using FProver (i.e. IssuerCertPathParameters).  If
   such a path exists, it must appear in certs.outX; if a path does not
   exist, it must not appear in certs.outX.

   <p>Then, for each pair of keys K and K' and each tag T in certs.inX,
   this test attempts to build a cert path using FProver that proves (K
   !T -> K') and (K +T -> K') (i.e., K grants the authorization T to K',
   with and without permission to propagate it).  As above, certs.outX
   must contain (or not contain) the resulting path.

   <p>Then, the above two suites are repeated using RProver
   (SubjectCertPathParameters).

   @see jsdsi.util.Loader

   @author Sameer Ajmani
**/
public class CertPathTest extends TestCase
{
    java.security.cert.CertPathBuilder builder;
    java.security.cert.CertPathValidator validator;
    jsdsi.CertPathParameters params;

  
    public CertPathTest(String name,
                        java.security.cert.CertPathBuilder b,
                        java.security.cert.CertPathValidator v,
                        jsdsi.CertPathParameters p)
    {
        super(name);
        assert (b!=null) ;
        assert (p!=null) ;
        builder = b;
        params = p;
        validator = v; // null => expect builder to fail
    }

    public void testCycle()
        throws java.security.InvalidAlgorithmParameterException
    {
        jsdsi.CertPathBuilderResult bres = null;
        try {
            bres = (jsdsi.CertPathBuilderResult)builder.build(params);
            // Report how many certs were fetched to make this proof:
            // System.out.print
            //     (((params instanceof IssuerCertPathParameters)
            //       ? "fw" : "rv") + bres.getStats().getNumFetched());
            if (validator == null) {
                // we expected to fail
                fail("Unexpected cert path found for : "
                     +params.getCert()+"\nPath:\n"+bres.getCertPath());
            }
            jsdsi.CertPathValidatorResult vres =
                (jsdsi.CertPathValidatorResult)
                validator.validate(bres.getCertPath(), params);
            // because of the duff/dummy certificate, vres.isOk() is false but ignore that in this test
        } catch (CertPathBuilderException e) {
            if (validator != null) {
                // we expected to succeed
                fail("No cert path found for :"+params.getCert());
            }
        } catch (CertPathValidatorException e) {
            fail("Cert path validation failed: "+bres.getCertPath());
        }
    }

    public static void main(String[] args)
	{
    	Test t = suite();
    	assert (t != null);
    }
    
    public static Test suite()
    {
        try {
            jsdsi.Provider.install();
            TestSuite s = new NamedTestSuite("CertPathTest");
            java.security.cert.CertPathBuilder builder =
                java.security.cert.CertPathBuilder.getInstance("SPKI");
            java.security.cert.CertPathValidator validator =
                java.security.cert.CertPathValidator.getInstance("SPKI");
            File cwd = new File("src/test/java/jsdsi");
            System.out.println(cwd.getAbsolutePath());
            String[] in = cwd.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("certs.in")
                            && !name.endsWith("~");
                    }
                });
            for (int i = 0; i < in.length; i++) {
                String out = "certs.out"
                    + in[i].substring("certs.in".length());
                Loader inLoad = new Loader(cwd+File.separator+in[i]);
                Loader outLoad = new Loader(cwd+File.separator+out);
                s.addTest(suite(builder, validator, inLoad, outLoad, true));
                s.addTest(suite(builder, validator, inLoad, outLoad, false));
            }
            return s;
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new Error(e);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static Test suite(java.security.cert.CertPathBuilder builder,
                              java.security.cert.CertPathValidator validator,
                              Loader in, Loader out, boolean forward)
    {
        TestSuite s = new TestSuite();    
        // try to find proofs from name to each key
        Iterator ni = in.getNames().iterator();
        while (ni.hasNext()) {
            Name n = (Name)ni.next();
            Iterator ki = in.getKeys().iterator();
            while (ki.hasNext()) {
                PublicKey k = (PublicKey)ki.next();
                NameCert c = new NameCert
                    (n.getIssuer(), k, null, null, null, n.getNames()[0]);
                addCertPathTest(builder, validator, in, out, forward, c, s);
            }
        }

        // try to find proofs with each tag, with and without propagate
        Iterator ii = in.getKeys().iterator();
        while (ii.hasNext()) {
            PublicKey issuer = (PublicKey)ii.next();
            Iterator ki = in.getKeys().iterator();
            while (ki.hasNext()) {
                PublicKey k = (PublicKey)ki.next();
                Iterator ti = in.getTags().iterator();
                while (ti.hasNext()) {
                    Tag t = (Tag)ti.next();
                    AuthCert c1 = new AuthCert // w/ propagate
                        (issuer, k, null, null, null, t, true);
                    AuthCert c2 = new AuthCert // w/o propagate
                        (issuer, k, null, null, null, t, false);
                    addCertPathTest(builder, validator,
                                    in, out, forward, c1, s);
                    addCertPathTest(builder, validator,
                                    in, out, forward, c2, s);
                }
            }
        }
        return s;
    }

    private static boolean containsStrongerCert(Set certs, Cert c)
    {
        Iterator i = certs.iterator();
        while (i.hasNext()) {
            Cert cert = (Cert) i.next();
            if (cert.implies(c)) {
                return true;
            }
        }
        return false;
    }
    
    private static void addCertPathTest
        (java.security.cert.CertPathBuilder builder,
         java.security.cert.CertPathValidator validator,
         Loader in, Loader out, boolean forward, Cert c, TestSuite s)
    {
        java.security.cert.CertPathValidator val =
            containsStrongerCert(out.getCerts(), c) ? validator : null;
        jsdsi.CertPathParameters params;
        try {
            if (forward) {
                params = new IssuerCertPathParameters(c, in.getCertStore());
            } else {
                params = new SubjectCertPathParameters(c, in.getCertStore());
            }
        } catch (java.security.InvalidAlgorithmParameterException e) {
            throw new Error(e);
        }
        s.addTest(new CertPathTest("testCycle", builder, val, params));
    }
}
