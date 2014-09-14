// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

public class ControllerActivityTest extends ActivityInstrumentationTestCase2<ControllerActivity> {

  /**
   * @param activityClass
   */
  public ControllerActivityTest(String name) {
    super(ControllerActivity.ANDROID_PKG, ControllerActivity.class);
    setName(name);
  }

  ControllerActivity instance;
  SharedPreferences prefs;
  Mockery context = new JUnit4Mockery();
  Resources res;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    instance = getActivity();
    res = instance.getResources();
    assertNotNull(res);
    prefs = instance.getSharedPreferences(getFromId(R.string.PREFS), Activity.MODE_PRIVATE);
    assertNotNull(prefs);
  }
  
  public String getFromId(int id) {
    return res.getString(id);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Removes any settings that have been altered during tests and brings them back to where they
   * ought to be prior to install.
   */
  private void cleanUpPrefs() {
    Log.d("test", "Restoring preferences to pre-installation state");
    prefs.edit().clear().commit();
  }

  @UiThreadTest
  public void testAcceptAndFinish() {
    // TODO (marco) nothing more to test here, until I figure out how to check the actual View
  }

  @SmallTest
  public void testOnCreateDialogInvalidIdReturnsNull() {
    assertNull(instance.onCreateDialog(123456));
    assertNull(instance.onCreateDialog(-1));
    assertNull(instance.onCreateDialog(0));
  }

  @SmallTest
  public void testOnCreateDlgInvalidEmail() {
    Dialog d = instance.onCreateDialog(ControllerActivity.DIALOG_EMAIL_INVALID);
    assertNotNull(d);
  }
}
