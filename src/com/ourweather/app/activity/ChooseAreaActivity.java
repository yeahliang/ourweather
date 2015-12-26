package com.ourweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.ourweather.app.R;
import com.ourweather.app.db.OurWeatherDB;
import com.ourweather.app.model.City;
import com.ourweather.app.model.County;
import com.ourweather.app.model.Province;
import com.ourweather.app.util.HttpCallbackListener;
import com.ourweather.app.util.HttpUtil;
import com.ourweather.app.util.Utility;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	private TextView mSetTitle = null;

	private ListView mListArea = null;

	private List<String> mDataStrings = new ArrayList<String>();

	private ArrayAdapter<String> mAdapter = null;

	private List<Province> mListProvinces = null;

	private List<City> mListCiies = null;

	private List<County> mListCounties = null;

	private int mCurrentLevel;// 默认为null

	private int LEVEL_PROVINCE = 0;

	private int LEVEL_CITY = 1;

	private int LEVEL_COUNTY = 2;

	private OurWeatherDB DB = null;

	private ProgressDialog mProgressDialog = null;

	private Province mSelectProvince = null;

	private boolean isFromShowWeatherActivity = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是不是存有某县城的信息，有就直接进入天气界面
		SharedPreferences sharedpref = PreferenceManager
				.getDefaultSharedPreferences(this);
		isFromShowWeatherActivity = getIntent().getBooleanExtra(
				"from_ShowWeatherActivity", false);
		if (sharedpref.getBoolean("city_selected", false)
				&& isFromShowWeatherActivity == false) {
			Intent intent = new Intent(ChooseAreaActivity.this,
					showWeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉原默认的标题栏
		setContentView(R.layout.activity_area_choose);

		mSetTitle = (TextView) findViewById(R.id.tv_set_title);
		mListArea = (ListView) findViewById(R.id.lv_list_area);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mDataStrings);
		mListArea.setAdapter(mAdapter);
		// 控制mAdaper
		DB = OurWeatherDB.getInstance(this);
		mListArea.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if (mCurrentLevel == LEVEL_PROVINCE) {// 如果被选中的是Province,则要查询相应的城市
					// 获得点去的省份对象
					Log.d("TAG", "Show Province");
					Province selectProvince = mListProvinces.get(index);
					mSelectProvince = selectProvince;
					Log.d("TAG", selectProvince.getProvince_Name());
					// 根据省份，查看相应的县城信息
					queryCities(selectProvince);
					Log.d("TAG", selectProvince.getProvince_Name() + "  之后");
				} else if (mCurrentLevel == LEVEL_CITY) { // 不能漏掉else啊
					Log.d("TAG", "Show Province");
					City selectCity = mListCiies.get(index);

					queryCounties(selectCity);
					Log.d("TAG", selectCity.getCity_Name());
				} else if (mCurrentLevel == LEVEL_COUNTY) {
					County county = mListCounties.get(index);
					Log.d("TAG", "主界面进来看天气:----->" + county.getCounty_Name());
					Intent intent = new Intent(ChooseAreaActivity.this,
							showWeatherActivity.class);
					intent.putExtra("county_code", county.getCounty_Code());
					startActivity(intent);
					finish();
				}
			}

		});

		queryProvinces();

	}

	private void queryProvinces() {
		// TODO Auto-generated method stub
		mListProvinces = DB.loadProvinces();
		if (mListProvinces.size() > 0) {
			mDataStrings.clear();// 把数据清空
			for (Province province : mListProvinces) {
				mDataStrings.add(province.getProvince_Name());
			}

			mAdapter.notifyDataSetChanged();// 通知绑定的ListView刷新
			mListArea.setSelection(0);// item置0
			mSetTitle.setText("中国");
			mCurrentLevel = LEVEL_PROVINCE;

		} else {// 数据库还没有，那么就去网上下载啊
			String addressProvince = "http://www.weather.com.cn/data/list3/city.xml";
			queryFromServer(addressProvince, "province", null);
		}

	}

	private void queryCities(Province selectProvince) {
		// TODO Auto-generated method stub
		mListCiies = DB.loadCities(selectProvince.getId());
		if (mListCiies.size() > 0) {
			mDataStrings.clear();
			for (City city : mListCiies) {
				mDataStrings.add(city.getCity_Name());
			}

			mAdapter.notifyDataSetChanged();
			mListArea.setSelection(0);
			mSetTitle.setText(selectProvince.getProvince_Name());
			mCurrentLevel = LEVEL_CITY;
		} else {
			String addressCity = "http://www.weather.com.cn/data/list3/city"
					+ selectProvince.getProvince_Code() + ".xml";
			queryFromServer(addressCity, "city", selectProvince);

		}

	}

	private void queryCounties(City selectCity) {
		// TODO Auto-generated method stub
		mListCounties = DB.loadCounties(selectCity.getId());
		if (mListCounties.size() > 0) {
			mDataStrings.clear();
			for (County county : mListCounties) {
				mDataStrings.add(county.getCounty_Name());
			}

			mAdapter.notifyDataSetChanged();
			mListArea.setSelection(0);
			mSetTitle.setText(selectCity.getCity_Name());
			mCurrentLevel = LEVEL_COUNTY;
		} else {
			String addressCounty = "http://www.weather.com.cn/data/list3/city"
					+ selectCity.getCity_Code() + ".xml";
			queryFromServer(addressCounty, "county", selectCity);
		}
	}

	private void queryFromServer(final String address, final String type,
			final Object area) {
		// TODO Auto-generated method stub

		showProgressDailog();
		HttpUtil.sendRequestByHttpURLConnection(address,
				new HttpCallbackListener() {
					// 注意：该方法的回调接口实在子线程中执行的哦~~~~~~~~~~~~~~~~~~~~~~~~~~
					@Override
					public void onFinish(String response) {// 下载成功
						// TODO Auto-generated method stub
						Boolean isSaved = false;
						// 存储成功
						if ("province".equals(type)) {// 下载所有省份的数据
							isSaved = Utility.handleProvincesResponse(DB,
									response);
						}
						if ("city".equals(type)) {// 下载对应城市的数据
							isSaved = Utility.handleCitiesResponse(DB,
									response, ((Province) area).getId());
						}
						if ("county".equals(type)) {// 下载对应县城的数据
							Log.d("TAG", response);
							isSaved = Utility.handleCountiesResponse(DB,
									response, ((City) area).getId());
						}
						if (isSaved) {
							// 下载并且存储成功了，是时候返回主线程更新UI了，因为该回调方法是在子线程执行的哦
							runOnUiThread(new Runnable() {
								public void run() {
									closeProgressDialog();// 关闭进度提示条，别再转圈啦

									if ("province".equals(type)) {
										queryProvinces();
									}
									if ("city".equals(type)) {
										queryCities((Province) area);
									}
									if ("county".equals(type)) {
										queryCounties((City) area);
									}
								}

							});
						}

					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						runOnUiThread(new Runnable() {
							public void run() {
								closeProgressDialog();
								Toast.makeText(ChooseAreaActivity.this,
										"下载或读取失败", Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

	}

	private void showProgressDailog() {
		// TODO Auto-generated method stub
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("下载ing...");
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

	@Override
	public void onBackPressed() {
		if (mCurrentLevel == LEVEL_CITY) {
			queryProvinces();
			mCurrentLevel = LEVEL_PROVINCE;
		} else if (mCurrentLevel == LEVEL_COUNTY) {
			queryCities(mSelectProvince);
			mCurrentLevel = LEVEL_CITY;
		} else {
			if (isFromShowWeatherActivity) {
				startActivity(new Intent(this, showWeatherActivity.class));
			}
			finish();//防止过快点击返回键
		}
	}

}
