package com.clawrence8.staywoke;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements SpotifyPlayer.NotificationCallback,
        ConnectionStateCallback {

    private static AlarmActivity mInstance;

    private TimePicker mTimePicker;
    private ToggleButton mToggleButton;
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private static final String CLIENT_ID = "6c6c0848d7eb452b9574acc42d2e5068";
    private static final String REDIRECT_URI = "stay-woke-alarm-app://callback";

    private static Player mPlayer;

    // Request code that will be used to verify if the result comes from correct activity
// Can be any integer
    private static final int REQUEST_CODE = 1337;


    public static Player getPlayer() {
        return mPlayer;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        mTimePicker = findViewById(R.id.alarmTimePicker);
        mToggleButton = findViewById(R.id.alarmToggleButton);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onToggleClicked(view);
            }
        });

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                final Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        if (mPlayer == null) {
                            mPlayer = spotifyPlayer;
                            mPlayer.addConnectionStateCallback(AlarmActivity.this);
                            mPlayer.addNotificationCallback(AlarmActivity.this);
                        }

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("AlarmActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }
    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("AlarmActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("AlarmActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("AlarmActivity", "User logged in");

        //mPlayer.playUri(null, "spotify:track:2Uvy6SkNqxvnH1W68dymxG", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("AlarmActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error e) {
        Log.d("AlarmActivity", "Login failed");
        Log.d("AlarmActivity", e.name());
    }

    @Override
    public void onTemporaryError() {
        Log.d("AlarmActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("AlarmActivity", "Received connection message: " + message);
    }
}
