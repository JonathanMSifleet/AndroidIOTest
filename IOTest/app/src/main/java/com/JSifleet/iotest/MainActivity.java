package com.JSifleet.iotest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

	Button saveTime;
	Button displayTime;
	TextView dateOutput;
	//myDatabase databaseToUse = new myDatabase(null);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		saveTime = (Button) this.findViewById(R.id.saveTime);
		displayTime = (Button) this.findViewById(R.id.displayTime);
		dateOutput = (TextView) this.findViewById(R.id.dateOutput);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.saveTime) {
			if (isExternalStorageWritable()) {
				this.checkPermissions();
				this.saveTimeToFS();
			} else {
				Log.e("Message", "Cannot write to fs");
			}
		} else if (v.getId() == R.id.displayTime) {
			this.displayTime();
		}
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

	public void checkPermissions() {
		String[] requiredPermissions = {
				Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
		};

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
		}

		return;
	}

	public void saveTimeToFS() {
		String filneame = "filename.txt";
		File file = new File(Environment.getExternalStorageDirectory(), filneame);
		FileOutputStream fos;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//Date dateToConvert = new Date(System.currentTimeMillis());
		String dateToWrite = formatter.format(System.currentTimeMillis());

		byte[] data = new String(dateToWrite).getBytes();

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

	/*
	public void insertDummyData(SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("forename", "Jonathan");
		values.put("surname", "Sifleet");
		values.put("extension", 3531);

		// don't worry about the 2nd parameter being null
		// returns the last insert id
		long insertId = db.insert("phonedirectory", null, values);

		// or for an update - returns # of rows updated
		// third parameter is the WHERE clause:
		int rows = db.update("phonedirectory", values, "id = 1", null);
	}

	public void performSillyQuery(SQLiteDatabase db) {
		String sql = "SELECT this FROM there WHERE something = ? AND somethingelse = ?;";
		String[] params = new String[2];
		params[0] = "firstParameterValue";
		params[1] = "42";

		Cursor resultSet = db.rawQuery(sql, params);

		resultSet.moveToFirst(); // must inclide
		while (!resultSet.isAfterLast()) {
			String forename = resultSet.getString(resultSet.getColumnIndex("forename"));
			String surname = resultSet.getString(resultSet.getColumnIndex("surname"));

			resultSet.moveToNext();
		}
	}
	*/

}

