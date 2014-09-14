// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts.storage;

import java.io.File;

import com.alertavert.android.applications.receipts.storage.FileUtils;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

/**
 * @author m.massenzio@gmail.com (Marco Massenzio)
 *
 */
public class FileUtilsTest extends AndroidTestCase {
  public final static String NAME = "test";
  public final static String FNAME_FULLPATH = FileUtils.SDCARD_DIR + File.separator 
      + NAME + FileUtils.EXT;
  final byte value = 55;
  final int len = 1024;

  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
//    getContext().deleteFile(FNAME);
    // clean up after ourselves:
    File f = new File(FNAME_FULLPATH);
    if (f.exists()) {
    	f.delete();
    }
  }

  @SmallTest
  public void testFilter() {
    // this succeeds by the very definition of the filter:
  	assertTrue(FileUtils.JPEG_FILENAME_FILTER.accept(FileUtils.getReceiptsDir(), 
  			"test.jpg"));
  	// this is in the correct directory, but wrong file type
  	assertFalse(FileUtils.JPEG_FILENAME_FILTER.accept(FileUtils.getReceiptsDir(), 
  	"test.doc"));
  	// wrong place to go looking for receipts images
  	assertFalse(FileUtils.JPEG_FILENAME_FILTER.accept(new File("/data/data"), 
  	"test.jpg"));
  	// wrong place and wrong file type too
  	assertFalse(FileUtils.JPEG_FILENAME_FILTER.accept(new File("/data/images"), 
  	"test.txt"));
  }
  
  @SmallTest
  public void testGetAvailable() {
  	File fileDir = FileUtils.getReceiptsDir();
  	File[] files = fileDir.listFiles();
  	int count = 0;
  	for (File f:files) {
  		if (f.getName().endsWith(FileUtils.EXT))
  			count++;
  	}
  	assertEquals(count, FileUtils.getAvailable().size());
  }
  
  @SmallTest
  public void testRemove() {
  	File f = new File(FileUtils.getReceiptsDir(), "test.jpg");
  	try {
			assertTrue("Could not create file: " + f.getAbsolutePath(), f.createNewFile());
			FileUtils.remove("test");
			assertFalse(f.exists());
			FileUtils.remove("not_exists");
			FileUtils.remove("not a valid *&% file name");
		} catch (Exception e) {
			Log.e("test", e.getLocalizedMessage());
			fail(e.getMessage());
		}
  }
}
