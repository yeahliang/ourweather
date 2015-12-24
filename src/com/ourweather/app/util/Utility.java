package com.ourweather.app.util;

import android.R.string;
import android.text.TextUtils;

import com.ourweather.app.db.OurWeatherDB;
import com.ourweather.app.model.City;
import com.ourweather.app.model.County;
import com.ourweather.app.model.Province;

/**
 * 工具类，解析和处理服务器返回的数据
 * 
 * @author yue
 * 
 */
public class Utility {

	/**
	 * 解析和处理服务器返回的有关省份的数据,并存入数据库 服务器返回的省份信息数据形如： "   01|北京，02|天津...  " 等
	 * 
	 * @param ourWeatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			OurWeatherDB ourWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String provinceStr : allProvinces) {
					// 获取每一个省份，并分离省份的code和name
					String[] arrProvince = provinceStr.split("\\|");
					Province province = new Province();
					province.setProvince_Code(arrProvince[0]);
					province.setProvince_Name(arrProvince[1]);

					ourWeatherDB.saveProvince(province);

				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的市级数据，并存入数据库 服务器返回的市级数据格式为： " 2801|广州,2802|佛山..    "
	 * 
	 * @param ourWeatherDB
	 * @param response
	 * @param province_Id
	 * @return
	 */
	public static boolean handleCitiesResponse(OurWeatherDB ourWeatherDB,
			String response, int province_Id) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String cityStr : allCities) {
					String[] arrCities = cityStr.split("\\|");
					City city = new City();
					city.setCity_Code(arrCities[0]);
					city.setCity_Name(arrCities[1]);
					city.setProvince_Id(province_Id);

					ourWeatherDB.saveCity(city);
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * 解析和处理服务器返回的县级数据，并存储与数据库中 服务器返回的县级数据格式形如： "190401|苏州,190202|常熟..."
	 * 县城的天气格式为 "190101|101190404",前面一个为县的代号，后一个字符串为该县（昆山）的天气代号
	 * 
	 * @param ourWeatherDB
	 * @param response
	 * @param city_Id
	 * @return
	 */
	public static boolean handleCountiesResponse(OurWeatherDB ourWeatherDB,
			String response, int city_Id) {

		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String countyStr : allCounties) {
					String[] arrCounty = countyStr.split("\\|");
					County county = new County();
					county.setCounty_Code(arrCounty[0]);
					county.setCounty_Name(arrCounty[1]);
					county.setCity_Id(city_Id);

					ourWeatherDB.saveCounty(county);

				}
				return true;
			}

		}

		return false;
	}

}
