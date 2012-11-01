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

import jsdsi.ExprTag;
import jsdsi.PrefixTag;
import jsdsi.RangeTag;
import jsdsi.SetTag;
import jsdsi.SimpleTag;
import jsdsi.StringTag;
import jsdsi.Tag;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests tag equality, implication, and intersection.
 * 
 * @author Sameer Ajmani
 * @author Sean Radford
 * @version $Revision: 1.5 $ $Date: 2004/03/09 23:03:43 $
 */
public class TagTest extends TestCase
{


	// Note: if you add a new Tag here, you need to add it to the tags
    // array below.
    static StringTag emptyString   = new StringTag("");
    static StringTag readString    = new StringTag("read");
    static StringTag writeString   = new StringTag("write");
    static StringTag fileString    = new StringTag("/my/private/file");
    static StringTag myString    = new StringTag("/my");
    static StringTag myPublicString = new StringTag("/my/public");
    static StringTag myPrivateString = new StringTag("/my/private");
    
    static PrefixTag emptyPrefix   = new PrefixTag("");
    static PrefixTag myPrefix      = new PrefixTag("/my");
    static PrefixTag myPublicPrefix  = new PrefixTag("/my/public");
    static PrefixTag myPrivatePrefix = new PrefixTag("/my/private");

    static ReversePrefixTag emptyRPrefix   = new ReversePrefixTag("");
    static ReversePrefixTag myRPrefix      = new ReversePrefixTag("/my");
    static ReversePrefixTag myPublicRPrefix  = new ReversePrefixTag("/my/public");
    static ReversePrefixTag myPrivateRPrefix = new ReversePrefixTag("/my/private");
    
    static SetTag readWriteSet = new SetTag
        (new ExprTag[] { readString, writeString });
    static SetTag writeReadSet = new SetTag
        (new ExprTag[] { writeString, readString });
    static SetTag myPublicPrivatePrefixSet = new SetTag
        (new ExprTag[] { myPublicPrefix, myPrivatePrefix });
    
    static SimpleTag grant = new SimpleTag("grant", new ExprTag[] { });
    static SimpleTag grunt = new SimpleTag("grunt", new ExprTag[] { });
    static SimpleTag grantRead = new SimpleTag
        ("grant", new ExprTag[] { readString });
    static SimpleTag grantReadFile = new SimpleTag
        ("grant", new ExprTag[] { readString, fileString });
    static SimpleTag grantFileRead = new SimpleTag
        ("grant", new ExprTag[] { fileString, readString });
    static SimpleTag grantWriteFile = new SimpleTag
        ("grant", new ExprTag[] { writeString, fileString });
    static SimpleTag grantReadWriteFile = new SimpleTag
        ("grant", new ExprTag[] { readWriteSet, fileString });

    static RangeTag leGeAlphaRange = new RangeTag
        ("alpha", "write", false, "read", false);
    static RangeTag leGtAlphaRange = new RangeTag
        ("alpha", "write", false, "read", true);
    static RangeTag ltGeAlphaRange = new RangeTag
        ("alpha", "write", true, "read", false);
    static RangeTag ltGtAlphaRange = new RangeTag
        ("alpha", "write", true, "read", true);

    // TODO: the following RangeTag types need more tests
    static StringTag numericString = new StringTag("123.45");    
    static RangeTag leGeNumericRange = new RangeTag
        ("numeric", "123.99", false, "123.11", false);
    
    static StringTag dateString = new StringTag("2004-01-21_18:30:23");
    static RangeTag leGeDateRange = new RangeTag
        ("date", "3010-01-01_00:00:00", false, "1976-07-03_07:00:01", false);
    static RangeTag leGeTimeRange = new RangeTag
        ("time", "3010-01-01_00:00:00", false, "1976-07-03_07:00:01", false);
    
    static StringTag binaryString = new StringTag("\001\002\003\004");
    static RangeTag leGeBinaryRange = new RangeTag
        ("binary", "\002\002\002\002", false, "\000\000\000\000", false);
    
    static ExprTag[] emptyToMyETags = new ExprTag[] {
    		new StringTag(""),
				new StringTag("/"),
				new StringTag("/m"),
				new StringTag("/my"),
    };
    static SetTag emptyToMySet = new SetTag(emptyToMyETags);
    
    static ExprTag[] emptyToMyPublicETags = new ExprTag[] {
    		new StringTag(""),
				new StringTag("/"),
				new StringTag("/m"),
				new StringTag("/my"),
				new StringTag("/my/"),
				new StringTag("/my/p"),
				new StringTag("/my/pu"),
				new StringTag("/my/pub"),
				new StringTag("/my/publ"),
				new StringTag("/my/publi"),
				new StringTag("/my/public"),
    };
    static SetTag emptyToMyPublicSet = new SetTag(emptyToMyPublicETags);
    
    static ExprTag[] emptyToMyPrivateETags = new ExprTag[] {
    		new StringTag(""),
				new StringTag("/"),
				new StringTag("/m"),
				new StringTag("/my"),
				new StringTag("/my/"),
				new StringTag("/my/p"),
				new StringTag("/my/pr"),
				new StringTag("/my/pri"),
				new StringTag("/my/priv"),
				new StringTag("/my/priva"),
				new StringTag("/my/privat"),
				new StringTag("/my/private")
    };
    static SetTag emptyToMyPrivateSet = new SetTag(emptyToMyPrivateETags);
    
    static ExprTag[] myToMyPublicETags = new ExprTag[] {
    		new StringTag("/my"),
				new StringTag("/my/"),
				new StringTag("/my/p"),
				new StringTag("/my/pu"),
				new StringTag("/my/pub"),
				new StringTag("/my/publ"),
				new StringTag("/my/publi"),
				new StringTag("/my/public"),
				};
    static SetTag myToMyPublicSet = new SetTag(myToMyPublicETags);
    
    static ExprTag[] myToMyPrivateETags = new ExprTag[] {
    		new StringTag("/my"),
				new StringTag("/my/"),
				new StringTag("/my/p"),
				new StringTag("/my/pr"),
				new StringTag("/my/pri"),
				new StringTag("/my/priv"),
				new StringTag("/my/priva"),
				new StringTag("/my/privat"),
				new StringTag("/my/private"),
    };
    static SetTag myToMyPrivateSet = new SetTag(myToMyPrivateETags);
    
    static Tag[] tags = new Tag[] {
        emptyString, readString, writeString, fileString, myString,
        emptyPrefix, myPrefix, myPublicPrefix, myPrivatePrefix,
        emptyRPrefix, myRPrefix, myPublicRPrefix, myPrivateRPrefix,
        readWriteSet, writeReadSet,
        myPublicPrivatePrefixSet, grant, grunt, grantRead, grantReadFile, grantFileRead,
        grantWriteFile, grantReadWriteFile, leGeAlphaRange,
        leGtAlphaRange, ltGeAlphaRange, ltGtAlphaRange,
        numericString, leGeNumericRange,
        dateString, leGeDateRange, leGeTimeRange,
        binaryString, leGeBinaryRange,
    };

    /**
       All pairs of tags that should be equals(), excluding
       self-equality.  A pair need only appear once (i.e., its reverse
       need not appear).
     **/
    static Tag[][] equalities = new Tag[][] {
        { readWriteSet, writeReadSet },
    };
    static boolean expectEquals(Tag tag1, Tag tag2)
    {
        for (int i = 0; i < equalities.length; i++) {
            if (equalities[i][0] == tag1 && equalities[i][1] == tag2)
                return true;
            if (equalities[i][0] == tag2 && equalities[i][1] == tag1)
                return true;
        }
        return false;
    }
    
    /**
       All pairs of tags such that the first element should implies()
       the second element, excluding self-implication.
     **/
    static Tag[][] implications = new Tag[][] {
    		/* PrefixTag */
    		{ emptyPrefix, emptyString },
			{ emptyPrefix, myString},
			{ emptyPrefix, readString },
			{ emptyPrefix, writeString },
			{ emptyPrefix, fileString },
			{ emptyPrefix, numericString },
			{ emptyPrefix, dateString },
			{ emptyPrefix, binaryString },
			{ emptyPrefix, myPrefix },
			{ emptyPrefix, myPublicPrefix },
			{ emptyPrefix, myPrivatePrefix },
			{ emptyPrefix, readWriteSet },
			{ emptyPrefix, writeReadSet },
			{ emptyPrefix, readWriteSet },
			{ emptyPrefix, myPublicPrivatePrefixSet },
			{ myPrefix, myString },
			{ myPrefix, fileString },
			{ myPrefix, myPublicPrefix },
			{ myPrefix, myPrivatePrefix },
			{ myPrivatePrefix, fileString },
			{ myPrefix, myPublicPrivatePrefixSet },
			/* ReversePrefixTag */
			{ emptyRPrefix, emptyString},
			{ myRPrefix, emptyString},
			{ myRPrefix, myString},
			{ myPublicRPrefix, emptyString},
			{ myPublicRPrefix, myString},
			{ myPrivateRPrefix, emptyString},
			{ myPrivateRPrefix, myString},
			{ myRPrefix, emptyRPrefix},
			{ myPublicRPrefix, emptyRPrefix},
			{ myPublicRPrefix, myRPrefix},
			{ myPrivateRPrefix, emptyRPrefix},
			{ myPrivateRPrefix, myRPrefix},
			/* SetTag */
			{ readWriteSet, readString },
			{ readWriteSet, writeString },
			{ readWriteSet, writeReadSet },
			{ writeReadSet, readString },
			{ writeReadSet, writeString },
			{ writeReadSet, readWriteSet },
			{ myPublicPrivatePrefixSet, myPrivatePrefix},
			{ myPublicPrivatePrefixSet, myPublicPrefix},
			{ myPublicPrivatePrefixSet, fileString},
			/* SimpleTag */
			{ grant, grantRead },
			{ grant, grantReadFile },
			{ grant, grantFileRead },
			{ grant, grantWriteFile },
			{ grant, grantReadWriteFile },
			{ grantRead, grantReadFile },
			{ grantReadWriteFile, grantReadFile },
			{ grantReadWriteFile, grantWriteFile },
			/* RangeTag */
			{ leGeAlphaRange, readString },
			{ leGeAlphaRange, writeString },
			{ leGeAlphaRange, readWriteSet },
			{ leGeAlphaRange, writeReadSet },
			{ leGeAlphaRange, leGtAlphaRange },
			{ leGeAlphaRange, ltGeAlphaRange },
			{ leGeAlphaRange, ltGtAlphaRange },
			{ ltGeAlphaRange, readString },
			{ ltGeAlphaRange, ltGtAlphaRange },
			{ leGtAlphaRange, writeString },
			{ leGtAlphaRange, ltGtAlphaRange },
			{ leGeNumericRange, numericString },
			{ leGeDateRange, dateString },
			{ leGeTimeRange, dateString },
			{ leGeBinaryRange, binaryString },
			{ leGeBinaryRange, myString} /* this just happens to be so, "/my" = #2F6D79# */ 
    };
    static boolean expectImplies(Tag tag1, Tag tag2)
    {
        for (int i = 0; i < implications.length; i++) {
            if (implications[i][0] == tag1 && implications[i][1] == tag2)
                return true;
        }
        return false;
    }

    /**
       All triples of tags such that the third element equals() the
       first element intersect() the second element, excluding
       self-intersections, intersections that yield NULL_TAG, and
       intersections where the result is one of the first two elements
       (we cover this last case in expectEquals).  A given pair of first
       and second elements need only appear once (i.e., its reverse need
       not appear).
     **/
    static Tag[][] intersections = new Tag[][] {
    		{ emptyPrefix, emptyRPrefix, emptyString}, 
			{ emptyPrefix, myRPrefix, emptyToMySet}, 
			{ emptyPrefix, myPublicRPrefix, emptyToMyPublicSet}, 
			{ emptyPrefix, myPrivateRPrefix, emptyToMyPrivateSet}, 
			{ myPrefix, myRPrefix, myString},
			{ myPrefix, myPublicRPrefix, myToMyPublicSet}, 
			{ myPrefix, myPrivateRPrefix, myToMyPrivateSet}, 
			{ myPublicPrefix, myPublicRPrefix, myPublicString}, 
			{ myPrivatePrefix, myPrivateRPrefix, myPrivateString},
			{ myRPrefix, myPublicPrivatePrefixSet, Tag.NULL_TAG},
			{ myPublicRPrefix, myPublicPrivatePrefixSet, myPublicString},
			{ myPrivateRPrefix, myPublicPrivatePrefixSet, myPrivateString},
			{ readWriteSet, leGtAlphaRange, writeString},
			{ readWriteSet, ltGeAlphaRange, readString},
			{ readWriteSet, leGtAlphaRange, readString},
			{ writeReadSet, leGtAlphaRange, writeString},
			{ writeReadSet, ltGeAlphaRange, readString},
			{ myPublicPrivatePrefixSet, readWriteSet, Tag.NULL_TAG },
			{ myPublicPrivatePrefixSet, writeReadSet, Tag.NULL_TAG },
			{ grantRead, grantReadWriteFile, grantReadFile },
			{ leGtAlphaRange, ltGeAlphaRange, ltGtAlphaRange },
    };
    static Tag expectIntersect(Tag tag1, Tag tag2)
    {
        if (tag1.implies(tag2)) {
            return tag2;
        }
        if (tag2.implies(tag1)) {
            return tag1;
        }
        for (int i = 0; i < intersections.length; i++) {
            if (intersections[i][0] == tag1 && intersections[i][1] == tag2)
                return intersections[i][2];
            if (intersections[i][0] == tag2 && intersections[i][1] == tag1)
                return intersections[i][2];
        }
        return Tag.NULL_TAG;
    }

    
    static class SelfTest extends TestCase
    {
        private Tag tag;
        public SelfTest(Tag t)
        {
            super("SelfTest for "+t.toString());
            tag = t;
        }
        public void runTest()
        {
            assertEquals("self equality", tag, tag);
            assertEquals("self intersection", tag.intersect(tag), tag);
            assertTrue("self implication", tag.implies(tag));
        }
    }
    
    static class EqualsTest extends TestCase
    {
        Tag tag1;
        Tag tag2;
        public EqualsTest(Tag t1, Tag t2)
        {
            super("EqualsTest");
            tag1 = t1;
            tag2 = t2;
        }
        public void runTest()
        {
            // equals must be symmetric
            if (expectEquals(tag1, tag2)) {
                assertTrue(tag1+" equals() "+tag2,
                           tag1.equals(tag2));
                assertTrue(tag2+" equals() "+tag1,
                           tag2.equals(tag1));
            } else {
                assertFalse(tag1+" not equals() "+tag2,
                            tag1.equals(tag2));
                assertFalse(tag2+" not equals() "+tag1,
                            tag2.equals(tag1));
            }
        }
    }
    
    static class ImpliesTest extends TestCase
    {
        Tag tag1;
        Tag tag2;
        public ImpliesTest(Tag t1, Tag t2)
        {
            super("ImpliesTest");
            tag1 = t1;
            tag2 = t2;
        }
        public void runTest()
        {
            if (expectImplies(tag1, tag2)) {
                assertTrue(tag1+" implies() "+tag2,
                           tag1.implies(tag2));
                if (tag2.implies(tag1)) {
                    assertTrue(tag1+" <=> "+tag2,
                               tag1.equals(tag2));
                }
            } else {
                assertFalse(tag1+" not implies() "+tag2,
                            tag1.implies(tag2));
            }
        }
    }
    
    static class IntersectTest extends TestCase
    {
        Tag tag1;
        Tag tag2;
        public IntersectTest(Tag t1, Tag t2)
        {
            super("IntersectTest");
            tag1 = t1;
            tag2 = t2;
        }
        public void runTest()
        {
            // intersect must be symmetric
            Tag tag12 = tag1.intersect(tag2);
            Tag tag21 = tag2.intersect(tag1);
            Tag expected = expectIntersect(tag1, tag2);
            assertEquals(tag1+" intersect() "+tag2, expected, tag12);
            assertEquals(tag2+" intersect() "+tag1, expected, tag21);
        }
    }
    
    public static Test suite()
    {
        TestSuite s = new NamedTestSuite("TagTest");
        // check tag self-equality, intersection, implication
        for (int i = 0; i < tags.length; i++) {
            s.addTest(new SelfTest(tags[i]));
        }
        // check tag equality
        for (int i = 0; i < tags.length; i++) {
            for (int j = i+1; j < tags.length; j++) {
                s.addTest(new EqualsTest(tags[i], tags[j]));
            }
        }
        // check tag implication
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < tags.length; j++) {
                if (i == j) continue; // covered in SelfTest
                s.addTest(new ImpliesTest(tags[i], tags[j]));
            }
        }
        // check tag intersection
        for (int i = 0; i < tags.length; i++) {
            for (int j = i+1; j < tags.length; j++) {
                s.addTest(new IntersectTest(tags[i], tags[j]));
            }
        }
        return s;
    }
}
