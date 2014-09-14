// Copyright AlertAvert.com (c) 2010. All rights reserved.

package com.alertavert.android.applications.receipts.database;

import java.io.File;

import com.alertavert.android.applications.receipts.database.ReceiptsDbOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

/**
 * 
 * <h1>ReceiptsDbOpenHelperStub</h1>
 * <p>
 * Stubs all the methods of ReceiptsDbOpenHelper so that they can be 'instrumented'
 * 
 * @author m.massenzio@gmail.com (Marco Massenzio)
 */
public class ReceiptsDbOpenHelperStub extends ReceiptsDbOpenHelper {

	int version;
	int oldVersion;
	int newVersion;
	boolean wasCreated;
	boolean wasUpgraded;
	SQLiteDatabase db;

	ReceiptsDbOpenHelperStub(Context ctx, String dbName, CursorFactory factory, int version) {
		super(ctx, dbName, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.version = db.getVersion();
		Log.d(ReceiptsDbOpenHelper.TAG, "OnCreate :: " + db.getPath() + ", version: " + version);
		this.wasCreated = true;
		this.db = db;
		super.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(ReceiptsDbOpenHelper.TAG, "OnUpgrade from rev " + oldVersion + " to rev. " + newVersion);
		this.version = db.getVersion();
		this.oldVersion = oldVersion;
		this.newVersion = newVersion;
		this.db = db;
		this.wasUpgraded = true;
	}
	
	@Override
	public SQLiteDatabase getWritableDatabase() {
		this.db = super.getWritableDatabase();
		return this.db;
	}
	
  public void deleteDb() {
  	String dbPath = db.getPath();
    if (dbPath != null) {
      Log.d(ReceiptsDbOpenHelper.TAG, "Deleting DB at " + dbPath);
      File dbFile = new File(dbPath);
      boolean wasDeleted = dbFile.delete();
      Log.d(ReceiptsDbOpenHelper.TAG, "Database was " + (wasDeleted ? "" : "not ") + "deleted");
    }
  }
}
