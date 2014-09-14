// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts.database;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.alertavert.android.applications.receipts.R;
import com.alertavert.android.applications.receipts.database.SqliteReceiptDao;
import com.alertavert.receiptscan.model.Money;
import com.alertavert.receiptscan.model.Receipt;

public class SqliteReceiptDaoTest extends AndroidTestCase {

  private static final String DB_NAME = "test_db";

  public static final int NUM_RECEIPTS = 5;

  public static final String RECEIPT_NAME = "test-recjjeipt-";

  SqliteReceiptDao dao;
  ReceiptsDbOpenHelperStub openHelper;
  Cursor mockCursor;
  final Mockery mockery = new Mockery();
  final List<Integer> ids = new ArrayList<Integer>();
  int receiptId;

  /*
   * (non-Javadoc)
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
    openHelper = new ReceiptsDbOpenHelperStub(mContext, DB_NAME, null, 2);
    assertNotNull(openHelper);
    dao = new SqliteReceiptDao(mContext, openHelper.getWritableDatabase());
    assertNotNull(dao);
    Receipt r = new Receipt();
    for (int i = 0; i < NUM_RECEIPTS; ++i) {
      r.setName(RECEIPT_NAME + i);
      r.setTimestamp(new Date());
      r.setAmount(new Money(3 * i, (37 * i) % 99, "USD"));
      r.setMerchant("Amazon-" + i);
      r.setNotes("This was an impulse expense!");
      r.setImageUri(URI.create("file:///sdcards/receipts/test/image-" + i + ".jpg"));
      ids.add(dao.store(r));
    }
    receiptId = ids.get(1);
    mockCursor = mockery.mock(Cursor.class);
  }

  /*
   * (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    // clean up database:
    if (openHelper != null) {
      SQLiteDatabase db = openHelper.getWritableDatabase();
      if (db != null) {
        db.execSQL("DELETE FROM RECEIPTS");
        db.execSQL("DELETE FROM REPORTS");
      }
// TODO (marco) figure out why the following throws an exception due to "unfinalized statements"?
//      db.close();
    }
    super.tearDown();
  }

  /**
   * Test method for {@link SqliteReceiptDao#getQueryString(int)}.
   */
  public void testGetQueryString() {
    String expectedSql = mContext.getResources().getString(R.string.find_all);
    String actual = dao.getQueryString(R.string.find_all);
    assertEquals(expectedSql, actual.trim());

    try {
      actual = dao.getQueryString(9999);
      assertNull(actual);
    } catch (Resources.NotFoundException expected) {
      // ignore
    }
  }

  /**
   * Test method for {@link SqliteReceiptDao#findAll()}.
   */
  public void testFindAll() {
    assertEquals(NUM_RECEIPTS, dao.findAll().size());
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#findByName(java.lang.String)}
   * .
   */
  public void testFindByName() {
    // at least the first one must be there
    Receipt r = dao.findByName(RECEIPT_NAME + 0);
    assertNotNull(r);
    assertTrue(r.getName().startsWith(RECEIPT_NAME));

    r = dao.findByName("blah");
    assertNull(r);
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#createFromCursor(android.database.Cursor)}
   * .
   */
  public void testCreateFromCursor() {
    mockery.checking(new Expectations() {
      {
        oneOf(mockCursor).getInt(0);
        will(returnValue(11));
        exactly(5).of(mockCursor).getString(with(any(Integer.class)));
        will(onConsecutiveCalls(returnValue("name"), returnValue("2010-06-22"), returnValue("USD"),
                returnValue("merchant"), returnValue("my notes")));
        oneOf(mockCursor).getFloat(3);
        will(returnValue(11.23f));
        oneOf(mockCursor).getString(7); will(returnValue("file:///data/data/receipts/image.jpg"));
        ignoring(mockCursor).getPosition();
        ignoring(mockCursor).getCount();
      }
    });
    Receipt actual = dao.createFromCursor(mockCursor);
    assertNotNull(actual);
    assertEquals(11, actual.getId());
    assertEquals("name", actual.getName());
    Calendar cal = Calendar.getInstance();
    cal.setTime(actual.getTimestamp());
    assertEquals(2010, cal.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, cal.get(Calendar.MONTH));
    assertEquals(22, cal.get(Calendar.DAY_OF_MONTH));
    assertEquals(11.23, actual.getAmount().getFloatValue(), 0.01);
    assertEquals("USD", actual.getAmount().getCurrency());
    assertEquals("merchant", actual.getMerchant());
    assertEquals("my notes", actual.getNotes());
    mockery.assertIsSatisfied();
  }

  /**
   * Test method for {@link SqliteReceiptDao#store(Receipt)}.
   */
  public void testStore() {
    try {
      Receipt r = new Receipt();
      r.setName("name");
      DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
      r.setTimestamp(fmt.parse("2010-06-22"));
      r.setAmount(new Money(22, 33, "USD"));
      r.setMerchant("Blades");
      r.setNotes("Bought a few blades for self-defense");
      r.setImageUri(URI.create("file:///receipts/blades.jpg"));
      int id = dao.store(r);
      assertFalse(id == SqliteReceiptDao.INVALID_ID);
      Receipt r2 = dao.findByName(r.getName());
      assertNotNull(r2);
    } catch (ParseException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#getAll()}.
   */
  public void testGetAll() {
    assertEquals(NUM_RECEIPTS, dao.getAll().size());
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#getCount()}.
   */
  public void testGetCount() {
    assertEquals(NUM_RECEIPTS, dao.getCount());
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#remove(int)}.
   */
  public void testRemove() {
    assertFalse(ids.isEmpty());
    int count = dao.getCount();
    assertTrue(dao.remove(ids.get(0)));
    assertEquals(--count, dao.getCount());
  }

  /**
   * Test method for
   * {@link com.alertavert.android.applications.receipts.database.SqliteReceiptDao#retrieve(int)}
   * .
   */
  public void testRetrieve() {
    Receipt r = dao.retrieve(receiptId);
    assertNotNull(r);
    assertEquals("IDs don't match after retrieve", receiptId, r.getId());
    r = dao.retrieve(999);
    assertNull("An invalid ID did not return a null Receipt", r);
  }
}
