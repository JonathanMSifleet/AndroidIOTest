package com.JSifleet.iotest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "data.sqlite"; // or use for external storage Environment.getExternalStorageDirectory() + "/data.sqlite";
	private static final int VERSION = 1;
	public SQLiteDatabase db;

	public myDatabase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		this.db = getWritableDatabase(); // required for database to save
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE IF NOT EXISTS testTable (surname TEXT PRIMARY KEY, forename TEXT)";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS testTable");
		this.onCreate(db);
	}
}
