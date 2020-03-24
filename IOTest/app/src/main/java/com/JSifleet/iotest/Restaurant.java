package com.JSifleet.iotest;

public class Restaurant {

	private Integer id;
	private String businessName;
	private String addressLine1;
	private String addressLine2;
	private String addressLine3;
	private String postCode;
	private Integer hygieneRating;
	private String ratingDate;
	private Double distance;

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public Integer getHygieneRating() {
		return hygieneRating;
	}

	public void setHygieneRating(Integer hygieneRating) {
		this.hygieneRating = hygieneRating;
	}

	public String getRatingDate() {
		return ratingDate;
	}

	public void setRatingDate(String ratingDate) {
		this.ratingDate = ratingDate;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
