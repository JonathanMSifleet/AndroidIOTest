package com.JSifleet.iotest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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
		String createStatement = "CREATE TABLE IF NOT EXISTS restaurants (id INTEGER PRIMARY KEY, BusinessName TEXT, AddressLine1 TEXT, AddressLine2 TEXT, AddressLine3 TEXT, PostCode TEXT, RatingValue INTEGER, RatingDate TEXT, DistanceKM REAL)";
		db.execSQL(createStatement);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS restaurants");
		this.onCreate(db);
	}

	public void insertRestaurants(SQLiteDatabase db, ArrayList<Integer> ids, ArrayList<String> businessNames, ArrayList<String> addressLine1s, ArrayList<String> addressLine2s, ArrayList<String> addressLine3s, ArrayList<String> postCodes, ArrayList<Integer> hygieneRatings, ArrayList<String> ratingDates, ArrayList<Double> distances) {
		try {
			for (int i = 0; i < ids.size(); i++) {

				ContentValues values = new ContentValues();
				values.put("id", ids.get(i));
				values.put("BusinessName", businessNames.get(i));
				values.put("AddressLine1", addressLine1s.get(i));
				values.put("AddressLine2", addressLine2s.get(i));
				values.put("AddressLine3", addressLine3s.get(i));
				values.put("PostCode", postCodes.get(i));
				values.put("RatingValue", hygieneRatings.get(i));
				values.put("RatingDate", ratingDates.get(i));
				values.put("DistanceKM", distances.get(i));

				// don't worry about the 2nd parameter being null
				// returns the last insert id
				long insertId = db.insert("restaurants", null, values);

				// or for an update - returns # of rows updated
				// third parameter is the WHERE clause:
				int rows = db.update("restaurants", values, null, null);
				Log.e("Message", "Data inserted successfully");
			}
		} catch (Exception e) {
			Log.e("Message", "Error inserting data");
		}
	}
}