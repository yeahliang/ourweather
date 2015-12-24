package com.ourweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {

	public static void sendRequestByHttpURLConnection(final String address,
			final HttpCallbackListener listener) {
		// 开启新线程执行网络下载工作
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				HttpURLConnection conn = null;
				try {
					URL url = new URL(address);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					//获得网络输入流
					InputStream is = conn.getInputStream();
					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(is));

					StringBuilder response = new StringBuilder();
					String line;
					//获得服务器的返回数据
					while ((line = bReader.readLine()) != null) {
						response.append(line);
					}
					
					if (listener != null) {
						//服务器访问请求成功，回调执行onFinsh方法
						listener.onFinish(response.toString());//该回调方法仍然在子线程中执行，不可以更新主线程的UI
					}
					if(is != null){
						is.close();
					}
					if(bReader != null){
						bReader.close();
					}

				} catch (Exception e) {
					// TODO: handle exception
					
				if (listener != null) {
					//读取服务器请求失败，回调该方法处理
					listener.onError(e);
				}
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
					

				}

			}
		}).start();
	}

}
