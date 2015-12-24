package com.ourweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库的创建与更新，需要此类
 * @author yue
 *
 */
public class OurWeatherOpenHelper extends SQLiteOpenHelper {

	private static String CREATE_PROVINCE = "create table province (id integer primary key autoincrement,province_name text,province_code text)";

	private static String CREATE_CITY = "create table city(id integer primary key autoincrement,city_name text,city_code text,province_id integer)";

	private static String CREATE_COUNTY = "create table county (id integer primary key autoincrement,county_name text,county_code text,city_id integer)";

	public OurWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
