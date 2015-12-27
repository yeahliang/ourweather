package com.ourweather.app.receiver;

import com.ourweather.app.service.AutoUpdateServer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor.AutoCloseInputStream;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context,Intent intent){
		
		Intent i = new Intent(context,AutoUpdateServer.class);
		context.startService(i);
		
	}
}
