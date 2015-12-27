package com.ourweather.app.activity;

import com.ourweather.app.R;
import com.ourweather.app.service.AutoUpdateServer;
import com.ourweather.app.util.HttpCallbackListener;
import com.ourweather.app.util.HttpUtil;
import com.ourweather.app.util.Utility;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class showWeatherActivity extends Activity implements OnClickListener {

	private TextView mTitleTextView = null;

	private TextView mTempTextView = null;

	private TextView mWeatherTextView = null;

	private TextView mCurrentDateTextView = null;

	private TextView mUpdateTimeTextView = null;

	private Button mChangeCityButton = null;

	private Button mUpdateWeatherButton = null;
	
	private ProgressDialog mProgressDialog =null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题，必须在加载布局文件前执行
		setContentView(R.layout.activity_weather_show);

		mTitleTextView = (TextView) findViewById(R.id.tv_county_name);
		mTempTextView = (TextView) findViewById(R.id.tv_temp_show);
		mWeatherTextView = (TextView) findViewById(R.id.tv_weather_show);
		mCurrentDateTextView = (TextView) findViewById(R.id.tv_current_date);
		mUpdateTimeTextView = (TextView) findViewById(R.id.tv_update_time);
		mChangeCityButton = (Button) findViewById(R.id.bt_change_city);
		mUpdateWeatherButton = (Button) findViewById(R.id.bt_update_weather);
		mChangeCityButton.setOnClickListener(this);
		mUpdateWeatherButton.setOnClickListener(this);

		// 判断是不是从选县城的Activity过来的
		String county_code = getIntent().getStringExtra("county_code");
		Log.d("TAG", "county_code------>" + county_code);
		if (TextUtils.isEmpty(county_code)) {// 如果是空，那么是从程序直接打开进来的，并未选择县城，那么显示本地天气
			showWeather();
		} else {// 选择显示某县城天气
			queryWeather(county_code);
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_change_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_ShowWeatherActivity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.bt_update_weather:
			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = sharedPreferences
					.getString("weather_code", "");
			showProgressDailog();
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherByCode(weatherCode);
			}

			break;
		default:
			break;
		}
	}

	/**
	 * 由县城号去查询天气号码
	 * 
	 * @param county_code
	 */
	private void queryWeather(String county_code) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ county_code + ".xml";
		queryFromServer(address, "county_code");
	}
	

	/**
	 * 由天气代号查询天气信息
	 * @param weather_code
	 */
	private void queryWeatherByCode(String weather_code) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weather_code + ".html";
		queryFromServer(address, "weather_code");
	}

	/**
	 * 从服务器查询天气号或者天气信息
	 * 
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendRequestByHttpURLConnection(address,
				new HttpCallbackListener() {

					@Override
					public void onFinish(String response) {
						// TODO Auto-generated method stub

						Log.d("TAG", response);
						if ("county_code".equals(type)) {
							if (!TextUtils.isEmpty(response)) {// 查询得到的 ----
																// 县城号|天气号 ----
																// 信息不为空

								// 处理返回信息
								String[] arrayStrings = response.split("\\|");
								if (arrayStrings != null
										&& arrayStrings.length == 2) {
									String weatherNum = arrayStrings[1];
									queryWeatherByCode(weatherNum);
								}
							}
						} else if ("weather_code".equals(type)) {
							// 存储天气信息
							Utility.handleWeatherResponse(
									showWeatherActivity.this, response);
							Log.d("TAG", "111111111111111111111111");
							runOnUiThread(new Runnable() {
								public void run() {
									closeProgressDialog();
									showWeather();
								}
							});
						}
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						runOnUiThread(new Runnable() {
							public void run() {
								// 不能再子线程使用Toast到UI线程
closeProgressDialog();
								Toast.makeText(showWeatherActivity.this,
										"获取天气失败", Toast.LENGTH_SHORT).show();

							}
						});
					}
				});
	}


	/**
	 * 查询已经下载并存储好的天气信息
	 */
	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences spf = PreferenceManager
				.getDefaultSharedPreferences(this);
		mTitleTextView.setText(spf.getString("city_name", null));
		String temp = spf.getString("temp2", null) + "~"
				+ spf.getString("temp1", null);
		mTempTextView.setText(temp);
		mWeatherTextView.setText(spf.getString("weather", null));
		mUpdateTimeTextView.setText(spf.getString("ptime", null));
		mCurrentDateTextView.setText(spf.getString("current_date", null));
		
		Intent serverIntent = new Intent(showWeatherActivity.this,AutoUpdateServer.class);
		startService(serverIntent);
	}

	private void showProgressDailog() {
		// TODO Auto-generated method stub
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("更新ing...");
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		mProgressDialog.show();
	}

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}
}
