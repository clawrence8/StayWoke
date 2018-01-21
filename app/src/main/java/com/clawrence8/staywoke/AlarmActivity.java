package com.clawrence8.staywoke;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private static AlarmActivity mInstance;

    private TimePicker mTimePicker;
    private ToggleButton mToggleButton;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;


    public static AlarmActivity getInstance() {
        return mInstance;
    }

    @Override
    public void onStart() {
        super.onStart();
        mInstance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mTimePicker = findViewById(R.id.alarmTimePicker);
        mToggleButton = findViewById(R.id.alarmToggleButton);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleClicked(view);
            }
        });
    }

    public void onToggleClicked(View view) {
        ToggleButton button = (ToggleButton) view;
        if (button.isChecked()) {
            Log.d("AlarmActivity", "Alarm on");
            Calendar calendar = Calendar.getInstance();
            //TODO: Check API version and use getHour/getMinute for API >=23
            calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
            Intent intent = new Intent(AlarmActivity.this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this,0, intent, 0);
            //Can use setExact on API >=23
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), mPendingIntent);
            String hour = new Integer(mTimePicker.getCurrentHour()).toString();
            String minute = new Integer(mTimePicker.getCurrentMinute()).toString();
            Toast.makeText(AlarmActivity.this, "Alarm set for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
        } else {
            if (mPendingIntent != null){
                mAlarmManager.cancel(mPendingIntent);
                Log.d("AlarmActivity", "Alarm off");
            }

        }
    }
}
