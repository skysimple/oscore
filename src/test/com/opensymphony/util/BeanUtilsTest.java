/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.util;

import com.opensymphony.util.BeanUtils;

import junit.framework.*;

import java.awt.*;


/**
 * JUnit test suite for BeanUtils... accessing properties.
 *
 * Simple AWT javabeans are used for testing purposes.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 * @version $Revision$
 */
public class BeanUtilsTest extends TestCase {
    //~ Instance fields ////////////////////////////////////////////////////////

    Button button;
    ScrollPane scrollPane;

    //~ Constructors ///////////////////////////////////////////////////////////

    public BeanUtilsTest(String name) {
        super(name);
    }

    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * test getFoo().getBar()
     */
    public void testCompositeGetters() throws Exception {
        assertEquals("arial", BeanUtils.getValue(button, "font.name"));
        assertEquals(new Integer(Font.BOLD), BeanUtils.getValue(button, "font.style"));
    }

    /**
     * test composite setters (eg. x.getBlah().setFoo())
     */
    public void testCompositeSetters() throws Exception {
        A a = new A();
        B b = new B();
        a.setB(b);
        b.setName("test1");
        assertEquals("test1", a.getB().getName());
        assertEquals("test1", BeanUtils.getValue(a, "b.name"));
        assertTrue(BeanUtils.setValue(a, "b.name", "newname"));
        assertEquals("newname", BeanUtils.getValue(a, "b.name"));
        assertEquals("newname", a.getB().getName());
        assertTrue(BeanUtils.setValue(a, "b.name", "test1"));
        assertEquals("test1", BeanUtils.getValue(a, "b.name"));
    }

    /**
     * test getters that return null
     */
    public void testEmptyGetters() throws Exception {
        assertNull(BeanUtils.getValue(button, "locale"));
    }

    /**
     * test getters that don't exist, or contain invalid chars
     */
    public void testInvalidGetters() throws Exception {
        assertNull(BeanUtils.getValue(button, "xcsdfs"));
        assertNull(BeanUtils.getValue(button, "_()"));
        assertNull(BeanUtils.getValue(button, ""));
        assertNull(BeanUtils.getValue(button, null));
    }

    /**
     * test setters that don't exist.
     */
    public void testInvalidSetters() throws Exception {
        assertTrue(BeanUtils.setValue(button, "label", "My label"));
        assertTrue(!BeanUtils.setValue(button, "xcsdfs", "dfsd"));
        assertTrue(!BeanUtils.setValue(button, "_()", "sdfd"));
        assertTrue(!BeanUtils.setValue(button, "", ""));
        assertTrue(!BeanUtils.setValue(button, null, null));
        button.setLabel("my button");
    }

    /**
     * test isBlah() instead of getBlah()
     */
    public void testIsGetters() throws Exception {
        assertEquals(Boolean.TRUE, BeanUtils.getValue(scrollPane, "visible"));
    }

    /**
     * test setting null values
     */
    public void testNullSetters() throws Exception {
        assertTrue(BeanUtils.setValue(scrollPane, "background", null));
        assertNull(scrollPane.getBackground());
        scrollPane.setBackground(Color.blue);
    }

    /**
     * test available property names for a class.
     */
    public void testPropertyNames() throws Exception {
        String[] buttonPs = BeanUtils.getPropertyNames(button);
        assertTrue(arrayContains(buttonPs, "label"));
        assertTrue(arrayContains(buttonPs, "background"));
        assertTrue(arrayContains(buttonPs, "font"));
        assertTrue(arrayContains(buttonPs, "visible"));
        assertTrue(!arrayContains(buttonPs, "dfds"));
        assertTrue(!arrayContains(buttonPs, "NAME"));
        assertTrue(!arrayContains(buttonPs, "Name"));

        String[] aPs = BeanUtils.getPropertyNames(new A());
        String[] bPs = BeanUtils.getPropertyNames(new B());
        assertTrue(arrayContains(aPs, "b"));
        assertTrue(arrayContains(bPs, "name"));
    }

    /**
     * test simple getBlah() methods
     */
    public void testSimpleGetters() throws Exception {
        assertEquals("my button", BeanUtils.getValue(button, "label"));
        assertEquals("scrolly", BeanUtils.getValue(scrollPane, "name"));
        assertEquals(Color.blue, BeanUtils.getValue(scrollPane, "background"));
    }

    /**
     * test simple setBlah() methods.
     */
    public void testSimpleSetters() throws Exception {
        assertEquals("my button", button.getLabel());
        assertTrue(BeanUtils.setValue(button, "label", "new button"));
        assertEquals("new button", button.getLabel());
        button.setLabel("my button");

        assertEquals(Color.blue, scrollPane.getBackground());
        assertTrue(BeanUtils.setValue(scrollPane, "background", Color.green));
        assertEquals(Color.green, scrollPane.getBackground());
        scrollPane.setBackground(Color.blue);

        assertTrue(scrollPane.isVisible());
        assertTrue(BeanUtils.setValue(scrollPane, "visible", Boolean.FALSE));
        assertTrue(!scrollPane.isVisible());
        scrollPane.setVisible(true);
    }

    protected void setUp() throws Exception {
        button = new Button();
        button.setLabel("my button");

        Font font = new Font("arial", Font.BOLD, 12);
        button.setFont(font);

        scrollPane = new ScrollPane();
        scrollPane.setBackground(Color.blue);
        scrollPane.setName("scrolly");
        scrollPane.setVisible(true);
    }

    /**
     * Check if array contains particular value.
     */
    private boolean arrayContains(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return true;
            }
        }

        return false;
    }

    //~ Inner Classes //////////////////////////////////////////////////////////

    public class A {
        private B b;

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }
    }

    public class B {
        private String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
