package com.ourweather.app.model;

public class County {
	private int id;
	private String county_Name;
	private String county_Code;
	private int city_Id;
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the county_Name
	 */
	public String getCounty_Name() {
		return county_Name;
	}
	/**
	 * @param county_Name the county_Name to set
	 */
	public void setCounty_Name(String county_Name) {
		this.county_Name = county_Name;
	}
	/**
	 * @return the county_Code
	 */
	public String getCounty_Code() {
		return county_Code;
	}
	/**
	 * @param county_Code the county_Code to set
	 */
	public void setCounty_Code(String county_Code) {
		this.county_Code = county_Code;
	}
	/**
	 * @return the city_Id
	 */
	public int getCity_Id() {
		return city_Id;
	}
	/**
	 * @param city_Id the city_Id to set
	 */
	public void setCity_Id(int city_Id) {
		this.city_Id = city_Id;
	}
	
}
