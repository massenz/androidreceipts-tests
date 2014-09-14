// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts.connectivity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Mockery;

import android.content.Intent;
import android.net.EmailParseException;
import android.net.ParseException;
import android.net.Uri;
import android.os.Parcelable;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.alertavert.android.applications.receipts.ControllerActivity;
import com.alertavert.android.applications.receipts.connectivity.MailSender;
import com.alertavert.android.applications.receipts.storage.FileUtils;
import com.alertavert.receiptscan.model.Receipt;

/**
 * <h1>MailSenderTest</h1>
 * <p>
 * TODO (mmassenzio) Insert class description here
 * 
 * <h3>Copyright (c) 2009. All rights reserved.</h3>
 * 
 * @author m.massenzio@gmail.com (Marco Massenzio)
 * 
 */
public class MailSenderTest extends ActivityUnitTestCase<ControllerActivity> {
  /**
   * @param activityClass
   */
  public MailSenderTest(Class<ControllerActivity> activityClass) {
    super(ControllerActivity.class);
    
  }

  public static final String TAG = "test";

  private static final String EMAIL = "mmassenzio@google.com";

  private URI emailUri;
  final Mockery mockery = new Mockery();
  MailSender sender;

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    emailUri = new URI("mailto", EMAIL, null);
    sender = new MailSender(EMAIL, getActivity());
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test method for
   * {@link MailSender#MailSender(java.lang.String)}
   * .
   */
  public void testMailSender() {
    assertNotNull(sender);
    assertNotNull(sender.getDestination());
  }

  /**
   * Test method for {@link MailSender#setDestinationEmail(java.lang.String)}.
   */
  @SmallTest
  public void testSetDestinationEmail() {
    try {
      sender.setDestinationEmail(EMAIL);
    } catch (ParseException ex) {
      fail(ex.getLocalizedMessage());
    }
  }

  @SmallTest
  public void testSetDestinationEmailInvalidString() {
    try {
      sender.setDestinationEmail("not really an email");
      fail("Invalid email string was accepted: " + sender.getDestination().getTo());
    } catch (EmailParseException ex) {
      // good
    } catch (IllegalArgumentException ex) {
      // good
    }
  }

  @SmallTest
  public void testSetDestinationEmailInvalidEmail() {
    try {
      sender.setDestinationEmail("myaddress/domain.html");
      fail("Invalid email string was accepted: " + sender.getDestination().getTo());
    } catch (EmailParseException ex) {
      // good
    } catch (IllegalArgumentException ex) {
      // good
    }
  }

  /**
   * Test method for {@link MailSender#setDestination(java.net.URI)}.
   */
  @SmallTest
  public void testSetDestinationUri() {
    try {
      sender.setDestination(emailUri);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(sender.getDestination());
    assertEquals(EMAIL, sender.getDestination().getTo());
  }

  /**
   * Test method for
   * {@link MailSender#setSenderOption(java.lang.String, java.lang.String)}
   * .
   */
  @SmallTest
  public void testSetSenderOption() {
    assertFalse(sender.setSenderOption("name", "value"));
  }

  /**
   * Test method for
   * {@link MailSender#send(java.util.Collection)}
   * .
   */
  @LargeTest
  public void testSend() {
    Set<Receipt> receipts = new HashSet<Receipt>();
    Receipt test = new Receipt();
    test.setName("receipt-3");
    test.setImageUri(URI.create("file:///data/receipts/" + test.getName() + FileUtils.EXT));
    receipts.add(test);
    assertTrue("Could not send email, check logs for reason", sender.send(receipts));
    // checking post-conditions
    Intent i = getStartedActivityIntent();
    // i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
    ArrayList<Parcelable> attachments = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
    assertEquals(1, attachments.size());
    assertTrue(attachments.get(0) instanceof android.net.Uri);
    Uri u = (Uri)attachments.get(0);
    assertTrue(u.getPath().contains(test.getName()));
  }
}
