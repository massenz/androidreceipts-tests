// Copyright Infinite Bandwidth ltd (c) 2010. All rights reserved.
// Created 11 Oct 2010, by marco
package com.alertavert.android.applications.receipts;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.alertavert.android.applications.receipts.EmailBodyFormatter;
import com.alertavert.receiptscan.model.Money;
import com.alertavert.receiptscan.model.Receipt;

import android.test.AndroidTestCase;
import android.util.Log;

/**
 * <h1>EmailBodyFormatterTest</h1>
 *
 * <p>TODO(marco) Insert class description here
 *
 * <h4>All rights reserved Infinite Bandwidth ltd (c) 2010</h4><br>
 * @author <a href='mailto:m.massenzio@gmail.com'>Marco Massenzio</a>
 * @version 1.0
 */
public class EmailBodyFormatterTest extends AndroidTestCase {
  
  public static final String EXP_OUT = "Name: name\n" +
  "Date: October 11, 2010\n" +
  "Amount: USD10.50\n" +
  "Merchant: amazon\n" +
  "--\n" +
  "Notes\n" +
  "--\n" +
  "My notes\n" +
  "-----------------\n" +
  "Name: name2\n" +
  "Date: October 11, 2010\n" +
  "Amount: USD20.99\n" +
  "Merchant: ebay\n" +
  "-----------------\n";
  
  final Calendar c = Calendar.getInstance();
  Date d;

  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    instance = new EmailBodyFormatter(getContext());
    c.set(2010, Calendar.OCTOBER, 11);
    d = c.getTime();
  }
  
  public EmailBodyFormatter instance;

  /**
   * Test method for {@link com.alertavert.android.applications.receipts.EmailBodyFormatter#EmailBodyFormatter(android.content.Context)}.
   */
  public void testEmailBodyFormatter() {
    assertNotNull(instance);
  }

  /**
   * Test method for {@link com.alertavert.android.applications.receipts.EmailBodyFormatter#format(java.util.Collection)}.
   */
  public void testFormat() {
    Receipt r1 = new Receipt(), r2 = new Receipt();
    r1.setName("name");
    r1.setTimestamp(d);
    r1.setAmount(new Money(10, 50, "USD"));
    r1.setMerchant("amazon");
    r1.setNotes("My notes");
    r2.setName("name2");
    r2.setTimestamp(d);
    r2.setAmount(new Money(20, 99, "USD"));
    r2.setMerchant("ebay");
    // should emit no notes section at all
    r2.setNotes("");
    
    List<Receipt> recs = Arrays.asList(r1, r2);
    Log.d("test", instance.format(recs));
    assertEquals(EXP_OUT, instance.format(recs));
  }

}
