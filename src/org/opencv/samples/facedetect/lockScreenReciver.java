package org.opencv.samples.facedetect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class lockScreenReciver extends BroadcastReceiver {

	public static boolean wasScreenOn = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
		// если экран выключен то запускаем наш лок скрин
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
			if(tm.getSimState() != TelephonyManager.SIM_STATE_PIN_REQUIRED && tm.getSimState() != TelephonyManager.SIM_STATE_PUK_REQUIRED){
				wasScreenOn = false;

				Intent intent11 = new Intent(context, LockScreen.class);
				intent11.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
				context.startActivity(intent11);

		        
			}else{
				AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	            Intent intentToFire = new Intent(context, lockScreenReciver.class);
	            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intentToFire, 0);            
	            alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, alarmIntent);
			}
			
		}
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        	Intent intent12 = new Intent(context, MainActivity.class);
			intent12.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(intent12);
        }
	}
	
        

       
}