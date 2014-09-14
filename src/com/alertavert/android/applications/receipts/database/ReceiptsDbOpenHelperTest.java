// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts.database;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.alertavert.android.applications.receipts.ControllerActivity;

public class ReceiptsDbOpenHelperTest extends ActivityInstrumentationTestCase2<ControllerActivity> {

	/**
	 * @param pkg
	 * @param activityClass
	 */
	public ReceiptsDbOpenHelperTest(String name) {
		super(ControllerActivity.ANDROID_PKG, ControllerActivity.class);
		setName(name);
	}

	public static final String DB_NAME = "test.db";
	ReceiptsDbOpenHelperStub stub;

	/**
	 * Test method for
	 * {@link ReceiptsDbOpenHelperStub#onCreate(android.database.sqlite.SQLiteDatabase)}
	 * .
	 */
	public void testOnCreateSQLiteDatabase() {
		final String PKG = getActivity().getPackageName();
		File f = new File("/data/data/" + PKG + "/databases/" + DB_NAME);
		Log.d("test", "Deleting db at " + f.getAbsolutePath());
		Log.d("test", "File was " + (f.delete()? "" : "not ") + "deleted");
		stub = new ReceiptsDbOpenHelperStub(getActivity(), DB_NAME, null, 1);
		SQLiteDatabase db = stub.getWritableDatabase();
		assertNotNull(db);
		assertTrue("onCreate was not called", stub.wasCreated);
		assertEquals(stub.db, db);
	}

	/**
	 * Test method for
	 * {@link ReceiptsDbOpenHelperStub#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)}
	 * .
	 */
	public void testOnUpgradeSQLiteDatabase() {
		stub = new ReceiptsDbOpenHelperStub(getActivity(), DB_NAME, null, 1);
		SQLiteDatabase db = stub.getReadableDatabase();
		assertFalse(stub.wasUpgraded);
		stub = new ReceiptsDbOpenHelperStub(getActivity(), DB_NAME, null, 2);
		db = stub.getReadableDatabase();
		assertTrue(stub.wasUpgraded);
		assertEquals(1, stub.oldVersion);
		assertEquals(2, stub.newVersion);
		assertNotNull(db);
		assertEquals(stub.db, db);
		assertEquals(2, db.getVersion());
		stub.deleteDb();
	}

}
