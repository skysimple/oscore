/*
 * Copyright (c) 2002-2003 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.util;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;


/**
 * FileManagerTest
 * @author Jason Carreira
 * Created May 8, 2003 3:35:46 PM
 */
public class FileManagerTest extends TestCase {
    //~ Static fields/initializers /////////////////////////////////////////////

    public static final Log LOG = LogFactory.getLog(FileManagerTest.class);

    //~ Instance fields ////////////////////////////////////////////////////////

    private boolean wasReloading;

    //~ Methods ////////////////////////////////////////////////////////////////

    public void setUp() {
        wasReloading = FileManager.isReloadingConfigs();
        FileManager.setReloadingConfigs(true);
    }

    public void tearDown() {
        FileManager.setReloadingConfigs(wasReloading);
    }

    public void testFileChanged() {
        try {
            File file = getFile();
            String fileName = file.getName();
            assertNotNull(FileManager.loadFile(fileName, FileManagerTest.class));

            Thread.sleep(50);

            OutputStream out = new FileOutputStream(file);
            out.write(32);
            out.close();
            assertTrue(FileManager.fileNeedsReloading(fileName));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testFileUnchanged() {
        try {
            File file = getFile();
            String fileName = file.getName();
            assertNotNull(FileManager.loadFile(fileName, FileManagerTest.class));
            assertFalse(FileManager.fileNeedsReloading(fileName));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private File getFile() throws IOException {
        URL xworkUrl = ClassLoaderUtil.getResource("somefile.xml", FileManagerTest.class);
        File xworkFile = new File(xworkUrl.getFile());
        File dirFile = xworkFile.getParentFile();
        File tmpFile = File.createTempFile("FileManagerTest", ".txt", dirFile);

        return tmpFile;
    }
}
