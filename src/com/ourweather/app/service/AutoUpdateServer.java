package com.ourweather.app.service;

import com.ourweather.app.R;
import com.ourweather.app.receiver.AlarmReceiver;
import com.ourweather.app.util.HttpCallbackListener;
import com.ourweather.app.util.HttpUtil;
import com.ourweather.app.util.Utility;

import android.R.integer;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AutoUpdateServer extends Service {
	private int flag = 0;
	private Notification notification;
	/**
	 * 更新时间间隔
	 */
	private final long UPDATE_EACH_TIME = SystemClock.elapsedRealtime() + 1000 * 60 * 3;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// notification = new Notification(R.drawable.ic_launcher,"OurWeather",
		// System.currentTimeMillis());
		notification = new Notification();
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 新线程更新weather
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();

		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent toBroadcastIntent = new Intent(this, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0,
				toBroadcastIntent, 0);
		// 3小时更新一次
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, UPDATE_EACH_TIME, pi);

		Toast.makeText(this, "服务后台执行ing", Toast.LENGTH_SHORT).show();

		showNotification();// 前台显示天气信息

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 前台显示天气信息
	 */
	private void showNotification() {

		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		String cityName = spf.getString("city_name", "");
		String temp = spf.getString("temp2", null) + "~"
				+ spf.getString("temp1", null);
		String weatherString = spf.getString("weather", "");

		notification.setLatestEventInfo(this, cityName + " ---->第 "
				+ (flag + 1) + "次更新", temp + "   " + weatherString, null);
		this.startForeground(++flag, notification);
	}

	/**
	 * 从网上下载最新天气数据
	 */
	private void updateWeather() {
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = spf.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		Log.d("TAG", address);
		HttpUtil.sendRequestByHttpURLConnection(address,
				new HttpCallbackListener() {

					@Override
					public void onFinish(String response) {
						// TODO Auto-generated method stub
						Utility.handleWeatherResponse(AutoUpdateServer.this,
								response);
						Log.d("TAG", response);
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						e.printStackTrace();
					}
				});
	}
}
