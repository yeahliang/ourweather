package com.ourweather.app.model;

import android.R.integer;

public class Province {
	private int id;
	private String province_Name;
	private String province_Code;

	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id=id;
	}
	
	
	/**
	 * @return the province_name
	 */
	public String getProvince_Name() {
		return province_Name;
	}
	/**
	 * @param province_name the province_name to set
	 */
	public void setProvince_Name(String province_Came) {
		this.province_Name = province_Came;
	}
	/**
	 * @return the province_code
	 */
	public String getProvince_Code() {
		return province_Code;
	}
	/**
	 * @param province_code the province_code to set
	 */
	public void setProvince_Code(String province_Code) {
		this.province_Code = province_Code;
	}

}
