package net.givreardent.sam.sss.background;

import net.givreardent.sam.sss.util.SSS;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartupReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("tag", "Received broadcast intent: " + intent.getAction());
		SSS.setNotifications(context);
	}

}
