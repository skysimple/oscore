/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.util;

import junit.framework.TestCase;


/**
 * Atlassian Source Code Template.
 * CVS Revision: $Revision$
 * Last CVS Commit: $Date$
 * Author of last CVS Commit: $Author$
 */
public class TestUrlUtils extends TestCase {
    //~ Constructors ///////////////////////////////////////////////////////////

    public TestUrlUtils(String string) {
        super(string);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    public void testHierachicalUri() {
        assertTrue(UrlUtils.verifyHierachicalURI("http://abc"));
        assertTrue(UrlUtils.verifyHierachicalURI("notes://aba"));
        assertTrue(UrlUtils.verifyHierachicalURI("abc123://aba"));
        assertTrue(UrlUtils.verifyHierachicalURI("http://amos.shop.com/amos/cc/main/catalog/pcd/11373729/ccsyn/66/_x_/New-Balance-Shoes-New-Balance-W763BL---Women's-Clearance-Shoes"));

        assertFalse(UrlUtils.verifyHierachicalURI("http://ab{c}a"));
        assertFalse(UrlUtils.verifyHierachicalURI("1otes://aba"));
    }
}
