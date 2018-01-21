package com.clawrence8.staywoke;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.spotify.sdk.android.player.Player;

/**
 * Created by Clayton on 1/20/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Player player = AlarmActivity.getPlayer();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri != null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
        if (player != null) {
            //TODO Reinitialize this when the alarm is received
            player.playUri(null, "spotify:track:2Uvy6SkNqxvnH1W68dymxG", 0, 0);
        } else {
            ringtone.play();
        }
        ComponentName componentName = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        intent.setComponent(componentName);
        context.startService(intent);
        setResultCode(Activity.RESULT_OK);
    }



}
