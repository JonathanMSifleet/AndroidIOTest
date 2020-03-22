package com.JSifleet.iotest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "data.sqlite";
	private static final int VERSION = 1;

	public myDatabase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE IF NOT EXISTS testTable (text surname PRIMARY KEY, text forename);";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}
