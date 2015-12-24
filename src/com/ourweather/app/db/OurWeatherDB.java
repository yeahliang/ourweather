package com.ourweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.ourweather.app.model.City;
import com.ourweather.app.model.County;
import com.ourweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 所有有关数据库的操作都放在此类中，该类实现数据库与外部交互的功能
 * @author yue
 *
 */
public class OurWeatherDB {

	public static final String DB_NAME = "Our_Weather.db";

	public static final int VERSION = 1;

	public static OurWeatherDB ourWeatherDB;

	private SQLiteDatabase DB;

	/**
	 * 构造方法私有化，即外部生成该类实例时，不用调用该构造方法
	 */
	private OurWeatherDB(Context context) {
		OurWeatherOpenHelper dbHelper = new OurWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		DB = dbHelper.getWritableDatabase();
	}

	/**
	 * 外部类，调用该方法可获得一个OurWeatherDB实例，并且都是同一个实例，而且实例同一个SQLiteDatabase
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static OurWeatherDB getInstance(Context context) {
		if (ourWeatherDB == null) {
			ourWeatherDB = new OurWeatherDB(context);
		}

		return ourWeatherDB;

	}

	/**
	 * 存储省份信息
	 * 
	 * @param province
	 */
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_Name", province.getProvince_Name());
			values.put("province_Code", province.getProvince_Code());
			DB.insert("province", null, values);
		}
	}

	/**
	 * 外部加载省份信息列表
	 * 
	 * @return
	 */
	public List<Province> loadProvinces() {
		List<Province> provinces = new ArrayList<Province>();
		// 查询数据库省份表的信息，并把它传入List<Province>中
		Cursor cursor = DB
				.query("province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvince_Name(cursor.getString(cursor
						.getColumnIndex("province_name")));
				province.setProvince_Code(cursor.getString(cursor
						.getColumnIndex("province_code")));
				// 添加省份信息到列表中
				provinces.add(province);
			} while (cursor.moveToNext());// 是否还有下一条数据
		}
		if (cursor != null) {
			cursor.close();// 关闭读取接口
		}

		return provinces;
	}

	/**
	 * 存储市的数据
	 */
	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			// values.put("city_Id", city.getId());//不用设置city_id，因为它是自增的
			values.put("city_name", city.getCity_Name());
			values.put("city_code", city.getCity_Code());
			values.put("province_id", city.getProvince_Id());

			DB.insert("city", null, values);
		}
	}

	/**
	 * 外部获取city表信息
	 */
	public List<City> loadCities(int provinceId) {
		List<City> cities = new ArrayList<City>();
		Cursor cursor = DB.query("city", null,"province_id = ?",new String []{String.valueOf(provinceId)},null,null,null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCity_Name(cursor.getString(cursor
						.getColumnIndex("city_name")));
				city.setCity_Code(cursor.getString(cursor
						.getColumnIndex("city_code")));
				city.setProvince_Id(cursor.getInt(cursor
						.getColumnIndex("province_id")));

				cities.add(city);

			} while (cursor.moveToNext());
		}

		if (cursor != null) {
			cursor.close();
		}
		return cities;
	}

	/**
	 * 存储county数据
	 */

	public void saveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCounty_Name());
			values.put("county_code", county.getCounty_Code());
			values.put("city_id", county.getCity_Id());

			DB.insert("county", null, values);
		}

	}

	/**
	 * 外部加载数据库中county的数据
	 */	
	public List<County> loadCounties(int cityId){
		List<County> counties = new ArrayList<County>();
		Cursor cursor = DB.query("county", null, "city_id = ? ", new String []{String.valueOf(cityId)}, null,null,null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setCity_Id(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCounty_Name(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCounty_Code(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCity_Id(cursor.getInt(cursor.getColumnIndex("city_id")));
				
				counties.add(county);//添加县信息进列表
			} while (cursor.moveToNext());
			
		}
		
		
		if (cursor != null) {
			cursor.close();
		}
		return counties;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
