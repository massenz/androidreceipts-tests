// Copyright Infinite Bandwidth ltd (c) 2010. All rights reserved.
// Created 6 Oct 2010, by marco
package com.alertavert.android.applications.receipts;

import com.alertavert.receiptscan.model.Money;

import junit.framework.TestCase;

/**
 * <h1>MoneyTest</h1>
 *
 * <p>TODO(marco) Insert class description here
 *
 * <h4>All rights reserved Infinite Bandwidth ltd (c) 2010</h4><br>
 * @author <a href='mailto:m.massenzio@gmail.com'>Marco Massenzio</a>
 * @version 1.0
 */
public class MoneyTest extends TestCase {

  /**
   * Test method for {@link com.alertavert.receiptscan.model.Money#getFloatValue()}.
   */
  public void testGetFloatValue() {
    Money m = new Money(1, 0, "USD");
    assertEquals(1.00, m.getFloatValue(), 0.01);
    m = new Money(1, 99, "USD");
    assertEquals(1.99, m.getFloatValue(), 0.01);
    m = new Money(123456, 85, "USD");
    assertEquals(123456.85, m.getFloatValue(), 0.01);
    m = new Money(0, 0, "USD");
    assertEquals(0.00, m.getFloatValue(), 0.01);
  }

  /**
   * Test method for {@link com.alertavert.receiptscan.model.Money#parse(float, java.lang.String)}.
   */
  public void testParse() {
    Money m = Money.parse(1.99f, "EUR");
    assertEquals(1, m.getIntValue());
    assertEquals(99, m.getFraction());
    m = Money.parse(2f, "EUR");
    assertEquals(2, m.getIntValue());
    assertEquals(0, m.getFraction());
    m = Money.parse(25658.99f, "EUR");
    assertEquals(25658, m.getIntValue());
    assertEquals(99, m.getFraction());
    m = Money.parse(0.00f, "EUR");
    assertEquals(0, m.getIntValue());
    assertEquals(0, m.getFraction());
  }

  /**
   * Test method for {@link com.alertavert.receiptscan.model.Money#toString()}.
   */
  public void testToString() {
    assertEquals("23.65", (new Money(23, 65, "GBP")).toString());
    assertEquals("0.25", (new Money(0, 25, "GBP")).toString());
    assertEquals("234568.65", (new Money(234568, 65, "GBP")).toString());
    assertEquals("0.00", (new Money(0, 0, "GBP")).toString());
  }

  /**
   * Test method for {@link com.alertavert.receiptscan.model.Money#toStringWithCurrency()}.
   */
  public void testToStringWithCurrency() {
    String s = (new Money(23, 65, "GBP")).toStringWithCurrency();
    assertEquals("GBP 23.65", s, s);
    s = (new Money(0, 25, "USD")).toStringWithCurrency();
    assertEquals("USD 0.25", s, s);
    s = (new Money(234568, 65, "ITL")).toStringWithCurrency();
    assertEquals("ITL 234568.65", s, s);
    s = (new Money(0, 0, "EUR")).toStringWithCurrency();
    assertEquals("EUR 0.00", s, s);
  }

}
