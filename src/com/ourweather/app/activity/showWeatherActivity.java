package com.ourweather.app.activity;

import com.ourweather.app.R;
import com.ourweather.app.util.HttpCallbackListener;
import com.ourweather.app.util.HttpUtil;
import com.ourweather.app.util.Utility;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class showWeatherActivity extends Activity {

	private TextView mTitleTextView = null;

	private TextView mTempTextView = null;

	private TextView mWeatherTextView = null;

	private TextView mCurrentDateTextView = null;

	private TextView mUpdateTimeTextView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather_show);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mTitleTextView = (TextView) findViewById(R.id.tv_county_name);
		mTempTextView = (TextView) findViewById(R.id.tv_temp_show);
		mWeatherTextView = (TextView) findViewById(R.id.tv_weather_show);
		mCurrentDateTextView = (TextView) findViewById(R.id.tv_current_date);
		mUpdateTimeTextView = (TextView) findViewById(R.id.tv_update_time);
		// 判断是不是从选县城的Activity过来的
		String county_code = getIntent().getStringExtra("county_code");
		Log.d("TAG", "county_code------>" + county_code);
		if (TextUtils.isEmpty(county_code)) {// 如果是空，那么是从程序直接打开进来的，并未选择县城，那么显示本地天气
			showWeather();
		} else {// 选择显示某县城天气
			queryWeather(county_code);
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
							//存储天气信息
							Utility.handleWeatherResponse(showWeatherActivity.this, response);
							Log.d("TAG", "111111111111111111111111");
							runOnUiThread(new Runnable() {
								public void run() {
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
								//不能再子线程使用Toast到UI线程
								Toast.makeText(showWeatherActivity.this,
										"获取天气失败", Toast.LENGTH_SHORT).show();

							}
						});
					}
				});
	}

	protected void queryWeatherByCode(String weather_code) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weather_code + ".html";
		queryFromServer(address, "weather_code");
	}

	private void showWeather() {
		// TODO Auto-generated method stub
		SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
		mTitleTextView.setText(spf.getString("city_name", null));
		String temp = spf.getString("temp1", null) +"~"+spf.getString("temp2", null);
		mTempTextView.setText(temp);
		mWeatherTextView.setText(spf.getString("weather", null));
		mUpdateTimeTextView.setText(spf.getString("ptime", null));
		mCurrentDateTextView.setText(spf.getString("current_date", null));
		
	}

}
