package com.JSifleet.iotest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

	Button getLocalRestaurants;
	Button displayTime;
	TextView dateOutput;
	myDatabase databaseToUse;
	Button readDatabase;
	Button closeDatabase;
	HygieneWebServiceClient hWSC = new HygieneWebServiceClient();

	Double lat = 0.0;
	Double lng = 0.0;
	boolean gotLocation = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		getLocalRestaurants = (Button) this.findViewById(R.id.getLocalRestaurants);
		displayTime = (Button) this.findViewById(R.id.displayTime);
		dateOutput = (TextView) this.findViewById(R.id.dateOutput);
		readDatabase = (Button) this.findViewById(R.id.readDatabase);
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
			case R.id.getLocalRestaurants:
				String[] FSPermissions = {
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.WRITE_EXTERNAL_STORAGE,
				};

				if (checkGotPermission(FSPermissions)) {
					if (isExternalStorageWritable()) {
						JSONArray jsonRestaurants = hWSC.getRestaurantsFromLocation(lat, lng);
						saveRestaurantsToFS(jsonRestaurants);
						Log.e("Success", "Wrote to fs");
					} else {
						Log.e("Message", "Cannot write to fs");
					}
				}
				break;
			case R.id.displayTime:
				this.displayTime();
				break;
			case R.id.readDatabase:
				this.getSurname(databaseToUse.db);
				break;
			case R.id.closeDatabase:
				databaseToUse.close();
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

	public void displayTime() {

		File file = new File(Environment.getExternalStorageDirectory(), "filename.txt");
		ArrayList<String> readLines = new ArrayList<>();
		try {
			Scanner in = new Scanner(file);
			while (in.hasNextLine()) {
				readLines.add(in.nextLine());
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (String line : readLines) {
			Log.e("Debug message", line);
		}

		dateOutput.setText(readLines.get(0));

	}

	public void saveRestaurantsToFS(JSONArray jsonArray) {
		String filneame = "restaurants.json";
		File file = new File(Environment.getExternalStorageDirectory(), filneame);
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
			databaseToUse = new myDatabase(this);
			Log.e("Message", "Database created");
		} catch (Exception e) {
			Log.e("Message", "error");
		}
	}

	public void getSurname(SQLiteDatabase db) {
		try {
			String sql = "SELECT forename, surname FROM testTable;"; //" WHERE something = ? AND somethingelse = ?;";
			// String[] params = new String[2];
			// params[0] = "firstParameterValue";
			// params[1] = "42";

			Cursor resultSet = db.rawQuery(sql, null); //, params);

			dateOutput.setText("");

			resultSet.moveToFirst(); // must include
			while (!resultSet.isAfterLast()) {
				String forename = resultSet.getString(resultSet.getColumnIndex("forename"));
				String surname = resultSet.getString(resultSet.getColumnIndex("surname"));

				dateOutput.append(forename + " " + surname + "\n");
				resultSet.moveToNext();
			}
		} catch (Exception e) {
			Log.e("Message", "Error reading database");
			dateOutput.setText("Database is closed");
		}
	}

}

