package com.JSifleet.iotest;

public class Restaurant {

	String name = "";
	int hygieneRating = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHygieneRating() {
		return hygieneRating;
	}

	public void setHygieneRating(int hygieneRating) {
		this.hygieneRating = hygieneRating;
	}

	public String toString() {
		return name + ": " + hygieneRating + "\r\n";
	}
}
