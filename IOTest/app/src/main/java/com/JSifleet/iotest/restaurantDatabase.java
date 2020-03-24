package com.JSifleet.iotest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class restaurantDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "data.sqlite"; // or use for external storage Environment.getExternalStorageDirectory() + "/data.sqlite";
	private static final int VERSION = 1;
	public SQLiteDatabase db;

	public restaurantDatabase(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
		this.db = getWritableDatabase(); // required for database to save
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createStatement = "CREATE TABLE IF NOT EXISTS testTable (surname TEXT PRIMARY KEY, forename TEXT)";
		db.execSQL(createStatement);
		this.insertDummyData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS testTable");
		this.onCreate(db);
	}

	private void insertDummyData(SQLiteDatabase db) {

		String surnames[] = {"Sifleet", "Misson"};
		String forenames[] = {"Jonathan", "Thomas"};

		for (int i = 0; i < surnames.length; i++) {
			try {
				ContentValues values = new ContentValues();
				values.put("surname", surnames[i]);
				values.put("forename", forenames[i]);

				// don't worry about the 2nd parameter being null
				// returns the last insert id
				long insertId = db.insert("testTable", null, values);

				// or for an update - returns # of rows updated
				// third parameter is the WHERE clause:
				int rows = db.update("testTable", values, null, null);
				Log.e("Message", "Data inserted successfully");
			} catch (Exception e) {
				Log.e("Message", "Error inserting data");
			}
		}
	}
}