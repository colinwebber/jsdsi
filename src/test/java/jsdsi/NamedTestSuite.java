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

import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
   A TestSuite that prints its name when it starts running.
 **/
public class NamedTestSuite extends TestSuite
{
    public NamedTestSuite() {
		super();
		// TODO Auto-generated constructor stub
	}
	public NamedTestSuite(Class theClass, String name)
    {
        super(name);
    }
    public NamedTestSuite(String name)
    {
        super(name);
    }
    public void run(TestResult result)
    {
        System.out.println("Running "+getName()+":");
        super.run(result);
        System.out.println();
    }
    
    public void testNothingToPreventNoTestsWarning() {
    	
    }
}
