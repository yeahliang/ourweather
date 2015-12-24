package com.ourweather.app.util;

public interface HttpCallbackListener {

	//访问服务器成功，回调该方法
	void onFinish(String response);
	
	//访问服务器失败，回调该方法
	void onError(Exception e);
	
}


