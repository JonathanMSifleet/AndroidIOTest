package com.JSifleet.iotest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	Button getRestaurants;
	TextView dateOutput;
	restaurantDatabase databaseToUse;
	Button writeDatabase;
	Button closeDatabase;
	HygieneWebServiceClient hWSC = new HygieneWebServiceClient();

	Double lat = 0.0;
	Double lng = 0.0;
	boolean gotLocation = false;

	JSONArray jsonRestaurants;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		getRestaurants = (Button) this.findViewById(R.id.getRestaurants);
		dateOutput = (TextView) this.findViewById(R.id.dateOutput);
		writeDatabase = (Button) this.findViewById(R.id.writeDatabase);
		closeDatabase = (Button) this.findViewById(R.id.closeDatabase);

		this.getLocation();

		try {
			this.createDatabase();
			Log.e("Message", "Success");
		} catch (Exception e) {
			Log.e("Message", "Error");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.getRestaurants:
				String[] FSPermissions = {
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE
				};

				if (checkGotPermission(FSPermissions)) {
					if (isExternalStorageWritable()) {
						JSONArray jsonRestaurants = hWSC.getRestaurantsFromLocation(lat, lng);
						writeRestaurantsToFS("restaurants.json", jsonRestaurants);
					} else {
						Log.e("Message", "Cannot write to fs");
					}
				}
				break;
			case R.id.writeDatabase:
				jsonRestaurants = this.readJSON("restaurants.json");
				this.saveJSONToDB(jsonRestaurants);
				break;
			case R.id.closeDatabase:
				databaseToUse.close();
				Log.e("Message", "Database has closed");
				break;
		}
	}

	public void getLocation() {

		String[] locationPermissions = {
				Manifest.permission.INTERNET,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.ACCESS_COARSE_LOCATION
		};

		if (checkGotPermission(locationPermissions)) {

			LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
				@Override
				public void onLocationChanged(Location location) {
					lat = location.getLatitude();
					lng = location.getLongitude();
					if (gotLocation == false) {
						Log.e("Location:", lat.toString() + ", " + lng.toString());
						gotLocation = true;
					}
				}

				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {
				}

				@Override
				public void onProviderEnabled(String provider) {
				}

				@Override
				public void onProviderDisabled(String provider) {
				}
			});
		}

	}

	public boolean checkGotPermission(String[] requiredPermissions) {

		boolean ok = true;
		for (int i = 0; i < requiredPermissions.length; i++) {
			int result = ActivityCompat.checkSelfPermission(this, requiredPermissions[i]);
			if (result != PackageManager.PERMISSION_GRANTED) {
				ok = false;
			}
		}

		if (!ok) {
			ActivityCompat.requestPermissions(this, requiredPermissions, 1);
			// last permission must be > 0
		} else {
			return true;
		}
		return false;
	}

	public JSONArray readJSON(String filename) {
		File file = new File(Environment.getExternalStorageDirectory(), "IOTest/" + filename);

		String json = "";
		try {
			InputStream is = new FileInputStream(file);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			json = new String(buffer, "UTF-8");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		try {
			return new JSONArray(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return new JSONArray();
	}

	public void saveJSONToDB(JSONArray restaurants) {

		ArrayList<Integer> ids = new ArrayList<>();
		ArrayList<String> businessNames = new ArrayList<>();
		ArrayList<String> addressLine1s = new ArrayList<>();
		ArrayList<String> addressLine2s = new ArrayList<>();
		ArrayList<String> addressLine3s = new ArrayList<>();
		ArrayList<String> postCodes = new ArrayList<>();
		ArrayList<Integer> hygieneRatings = new ArrayList<>();
		ArrayList<String> ratingDates = new ArrayList<>();
		ArrayList<Double> distances = new ArrayList<>();

		for (int i = 0; i < restaurants.length(); i++) {
			try {
				JSONObject jo = restaurants.getJSONObject(i);

				ids.add(jo.getInt("id"));
				businessNames.add(jo.getString("BusinessName"));
				addressLine1s.add(jo.getString("AddressLine1"));
				addressLine2s.add(jo.getString("AddressLine2"));
				addressLine3s.add(jo.getString("AddressLine3"));
				postCodes.add(jo.getString("PostCode"));
				hygieneRatings.add(jo.getInt("RatingValue"));
				ratingDates.add(jo.getString("RatingDate"));
				distances.add(jo.getDouble("DistanceKM"));
			} catch (JSONException e) {
				Log.e("Error", "Something has gone wrong");
				e.printStackTrace();
			}
		}

		databaseToUse.insertRestaurants(databaseToUse.db, ids, businessNames, addressLine1s, addressLine2s, addressLine3s, postCodes, hygieneRatings, ratingDates, distances);

	}

	public void writeRestaurantsToFS(String filename, JSONArray jsonArray) {

		File folder = new File(Environment.getExternalStorageDirectory(), "/IOTest");

		if (!folder.exists()) {
			folder.mkdirs();
		}

		File file = new File(Environment.getExternalStorageDirectory(), "IOTest/" + filename);
		FileOutputStream fos;

		byte[] data = new String(jsonArray.toString()).getBytes();

		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
			fos.close();
			Log.e("Message", "Data written");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("Message", "Data not written");
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Log.e("Message", "Data not written");
		}
	}

	public boolean isExternalStorageWritable() {
		// checks if external storage is available for read and write
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	public void createDatabase() {
		try {
			databaseToUse = new restaurantDatabase(this);
			Log.e("Message", "Database created");
		} catch (Exception e) {
			Log.e("Message", "error");
		}
	}

	/*
	public void readDatabase(SQLiteDatabase db) {
		try {
			String sql = "SELECT id, BusinessName, AddressLine1, AddressLine2, AddressLine3, PostCode, RatingValue, RatingDate, DistanceKM FROM restaurants;"; //" WHERE something = ? AND somethingelse = ?;";
			// String[] params = new String[2];
			// params[0] = "firstParameterValue";
			// params[1] = "42";

			Cursor resultSet = db.rawQuery(sql, null); //, params);

			dateOutput.setText("");

			resultSet.moveToFirst(); // must include
			while (!resultSet.isAfterLast()) {
				String id = resultSet.getString(resultSet.getColumnIndex("id"));
				String businessName = resultSet.getString(resultSet.getColumnIndex("BusinessName"));
				String addressLine1 = resultSet.getString(resultSet.getColumnIndex("AddressLine1"));
				String addressLine2 = resultSet.getString(resultSet.getColumnIndex("AddressLine2"));
				String addressLine3 = resultSet.getString(resultSet.getColumnIndex("AddressLine3"));
				String postCode = resultSet.getString(resultSet.getColumnIndex("PostCode"));
				String hygieneRating = resultSet.getString(resultSet.getColumnIndex("RatingValue"));
				String ratingDate = resultSet.getString(resultSet.getColumnIndex("RatingDate"));
				String distanceKM = resultSet.getString(resultSet.getColumnIndex("DistanceKM"));

				dateOutput.append(id + "\n");
				dateOutput.append(businessName + "\n");
				dateOutput.append(addressLine1 + "\n");
				dateOutput.append(addressLine2 + "\n");
				dateOutput.append(addressLine3 + "\n");
				dateOutput.append(postCode + "\n");
				dateOutput.append(hygieneRating + "\n");
				dateOutput.append(ratingDate + "\n");
				dateOutput.append(distanceKM + "\n");

				resultSet.moveToNext();
				break;
			}
		} catch (Exception e) {
			Log.e("Message", "Error reading database");
			dateOutput.setText("Database is closed");
		}
	} */

}

