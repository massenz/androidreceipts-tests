// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import android.app.Activity;
import android.app.Instrumentation;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Gallery;

import com.alertavert.android.applications.receipts.ControllerActivity;
import com.alertavert.android.applications.receipts.ReceiptsGalleryActivity;
import com.alertavert.android.applications.receipts.storage.ReceiptDAO;
import com.alertavert.receiptscan.model.Receipt;

public class ReceiptsGalleryActivityTest extends
        ActivityInstrumentationTestCase2<ReceiptsGalleryActivity> {
  /**
   * @param activityClass
   */
  public ReceiptsGalleryActivityTest(String name) {
    super(ControllerActivity.ANDROID_PKG, ReceiptsGalleryActivity.class);
    setName(name);
  }

  Instrumentation mInstr;
  ReceiptsGalleryActivity galleryActivity;
  Gallery mGallery;
  Mockery mockery = new JUnit4Mockery();
  ReceiptDAO mockDao;
  final Date d = new Date();
  final Receipt r = new Receipt();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mInstr = getInstrumentation();
    setActivityInitialTouchMode(false);
    Activity a = getActivity();
    assertTrue(a instanceof ReceiptsGalleryActivity);
    galleryActivity = (ReceiptsGalleryActivity) a;
    createAndInjectMocks();
  }

  /**
   * Creates mocks for the DAO and the data proxy, and injects them into the Activity
   */
  private void createAndInjectMocks() {
    mockDao = mockery.mock(ReceiptDAO.class);
    galleryActivity.mDao = mockDao;
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  boolean galleryInitialized = false;

  @SmallTest
  public void testPreConditions() {
    Log.d("test", getName() + " starts here ---");

    // I am not sure all this is necessary, following the code on the tutorial
    // at http://developer.android.com/resources/tutorials/testing/helloandroid_test.html
    galleryActivity.finish();
    galleryActivity = this.getActivity();

    setupExpectations();
    final Lock uiLock = new ReentrantLock();
    final Condition cond = uiLock.newCondition();
    galleryActivity.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Log.d("test", getName() + " init gallery");
        try {
          uiLock.lock();
          galleryActivity.initGallery();
          galleryInitialized = true;
          cond.signal();
        } finally {
          uiLock.unlock();
        }
        Log.d("test", getName() + " init gallery done");
      }
    });
    assertNotNull(galleryActivity.getDao());
    assertEquals(mockDao, galleryActivity.getDao());
    assertNotNull(galleryActivity.mGallery);
    Log.d("test", getName() + " findViewById");
    try {
      uiLock.lock();
      // this should really be in a loop, but it's easier to just test once, if not ready yet
      // wait 5 seconds, try again and if still not ready, just give up
      if (galleryInitialized) {
        mGallery = (Gallery) galleryActivity.findViewById(R.id.gallery);
      } else if (!cond.await(5, TimeUnit.SECONDS)) {
          Log.d("test", "Waiting for UI Thread to run timed out, giving up");
          fail("Waiting for UI Thread to run timed out, giving up");
      } else if (galleryInitialized) {
        mGallery = (Gallery) galleryActivity.findViewById(R.id.gallery);
      } 
    } catch (InterruptedException ex) {
      fail(ex.getMessage());
    } finally {
      uiLock.unlock();
    }
    Log.d("test", getName() + " findViewById done");
    assertEquals(mGallery, galleryActivity.mGallery);
    assertNotNull(galleryActivity.mGallery.getAdapter());
    assertNotNull(galleryActivity.receiptNameTxt);
    assertNotNull(galleryActivity.receiptDateBtn);
    Log.d("test", getName() + " ends here ---");
  }

  /**
   * 
   */
  private void setupExpectations() {
    // setting the expectations on the mocks
    mockery.checking(new Expectations() {
      {
        r.setName("r1");
        r.setId(1);
        r.setTimestamp(d);
        allowing(mockDao).getCount();
        will(returnValue(1));
        allowing(mockDao).retrieve(with(any(int.class)));
        will(returnValue(r));
        @SuppressWarnings("unchecked")
        Map<Integer, Receipt> map = mockery.mock(Map.class);
        allowing(map).get(with(any(int.class)));
        will(returnValue(r));
        allowing(map).keySet();
        will(returnValue(new HashSet<Integer>(Arrays.asList(1))));
        allowing(mockDao).getAll();
        will(returnValue(map));
      }
    });
  }

  @SmallTest
  public void testCreateDao() {
    ReceiptDAO expected = galleryActivity.getDao();
    assertNotNull(expected);
  }

  @SmallTest
  public void testCreateDbHelper() {
    SQLiteOpenHelper helper = galleryActivity.getHelper();
    assertNotNull(helper);
  }

  @UiThreadTest
  public void testOnCreateDialogCreatesDatePicker() {
    // TODO (marco) test that tapping on date the dialog pops up & changing date modifies Receipt
  }
}
