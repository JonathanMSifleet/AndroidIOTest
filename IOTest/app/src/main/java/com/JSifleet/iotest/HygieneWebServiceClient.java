package com.JSifleet.iotest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HygieneWebServiceClient {

	public JSONArray getRestaurantsFromLocation(double lat, double lng) {

		try {
			URL u = new URL("http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_location&lat=" + lat + "&long=" + lng);

			Log.e("URL", u.toString());

			try {
				HttpURLConnection tc = (HttpURLConnection) u.openConnection();
				InputStreamReader isr = new InputStreamReader(tc.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				String line = "";
				String json = "";

				while ((line = in.readLine()) != null) {
					json = json + line;
				}
				in.close();

				return new JSONArray(json);

			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			Log.e("Error", "Malformed URL");
		}
		return new JSONArray();
	}

}
