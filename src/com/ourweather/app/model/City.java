package com.ourweather.app.model;

public class City {
	private int id;
	private String city_Name;
	private String city_Code;
	private int province_Id;
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
	 * @return the city_Name
	 */
	public String getCity_Name() {
		return city_Name;
	}
	/**
	 * @param city_Name the city_Name to set
	 */
	public void setCity_Name(String city_Name) {
		this.city_Name = city_Name;
	}
	/**
	 * @return the city_Code
	 */
	public String getCity_Code() {
		return city_Code;
	}
	/**
	 * @param city_Code the city_Code to set
	 */
	public void setCity_Code(String city_Code) {
		this.city_Code = city_Code;
	}
	/**
	 * @return the province_Id
	 */
	public int getProvince_Id() {
		return province_Id;
	}
	/**
	 * @param province_Id the province_Id to set
	 */
	public void setProvince_Id(int province_Id) {
		this.province_Id = province_Id;
	}
	
}
