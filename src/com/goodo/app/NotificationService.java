package com.goodo.app;

import com.goodo.app.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends Service{
	private  String time = "";
	private  String content = "";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		time = intent.getStringExtra("BeginTime");
		content = intent.getStringExtra("Work");
		System.out.println("23456"+time);
		
		int icon = R.drawable.start_u28;
		long when = (System.currentTimeMillis());
		Notification nfc = new Notification(icon, time+":"+content, when);
		Context cxt = getApplicationContext();
		String title = "appÃ·–—";
		String text = time+":"+content;
		Intent intent2 = null;
		PendingIntent nfcIntent = PendingIntent.getActivity(cxt, 0, intent2, 0);
		nfc.setLatestEventInfo(cxt, title, text, nfcIntent);
		nfc.defaults = Notification.DEFAULT_SOUND;
		NotificationManager nfcManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nfcManager.notify(1,nfc);
		return super.onStartCommand(intent, flags, startId);
	}
}
