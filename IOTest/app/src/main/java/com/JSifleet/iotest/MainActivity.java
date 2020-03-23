package com.JSifleet.iotest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
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
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

	Button saveTime;
	Button displayTime;
	TextView dateOutput;
	myDatabase databaseToUse;
	Button readDatabase;
	Button closeDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		saveTime = (Button) this.findViewById(R.id.saveTime);
		displayTime = (Button) this.findViewById(R.id.displayTime);
		dateOutput = (TextView) this.findViewById(R.id.dateOutput);
		readDatabase = (Button) this.findViewById(R.id.readDatabase);
		closeDatabase = (Button) this.findViewById(R.id.closeDatabase);

		try {
			this.createDatabase();
			Log.e("Message", "Success");
		} catch (Exception e) {
			Log.e("Message", "Error");
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.saveTime:
				if (isExternalStorageWritable()) {
					this.checkPermissions();
					this.saveTimeToFS();
				} else {
					Log.e("Message", "Cannot write to fs");
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
		}
	}

}

