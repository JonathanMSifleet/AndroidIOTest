package com.JSifleet.iotest;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

				/* for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);

					//Restaurant r = new Restaurant();
					//r.setName(jo.getString("BusinessName"));
					//r.setHygieneRating(Integer.parseInt(jo.getString("RatingValue")));

					Log.e("JSONObject", jo.getString("BusinessName")+ jo.getString("RatingValue"));

					//listItems.add(r);
				} */

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
