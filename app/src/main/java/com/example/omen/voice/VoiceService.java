package com.example.omen.voice;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.omen.HomeMainActivity;
import com.example.omen.emergencycontact.ListContactsFragment;
import com.example.omen.ui.home.HomeFragment;
import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class VoiceService extends Service implements SpeechDelegate, Speech.stopDueToDelay{
    final String TAG="asd";
    public static SpeechDelegate delegate;


    public VoiceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service ");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand:  ");
        Toast.makeText(getApplicationContext(),"This is a Service running in Background",
                Toast.LENGTH_SHORT).show();
        HomeMainActivity.serviceRunning.setVisibility(View.VISIBLE);


        Speech.init(this);
        delegate = this;
        Speech.getInstance().setListener(this);


        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            muteBeepSoundOfRecorder();

        } else {
            System.setProperty("rx.unsafe-disable", "True");
            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();
                        Log.d(TAG, "onStartCommand: SpeechRecognitionNotAvailable");

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                        Log.d(TAG, "onStartCommand:GoogleVoiceTypingDisabledException ");
                    }
                } else {
                    Toast.makeText(this,"Please grant the permission to use the microphone", Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();

        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("asd", "onTaskRemoved: --------------------------------------------------");

        PendingIntent service =
                PendingIntent.getService(getApplicationContext(), new Random().nextInt(),
                        new Intent(getApplicationContext(), VoiceService.class), PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, service);


        if (checkServiceRunning()) {

            Log.d(TAG, "onTaskRemoved:  check service");
            Intent i;
            PackageManager manager = getPackageManager();
            try {
                Log.d(TAG, "onTask: service launching app");
                i = manager.getLaunchIntentForPackage(getPackageName());
                if (i == null) {
                    Log.d(TAG, "onTask: service i NULL");
                    throw new PackageManager.NameNotFoundException();
                }
                i.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "onTask: service NAME NOT FOUND");
            }

            Log.d(TAG, "onTask: service  DONE");
        }

        super.onTaskRemoved(rootIntent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: SERVICE");
        onSpecifiedCommandPronounced("");
        HomeMainActivity.speakNow.setVisibility(View.GONE);
        HomeMainActivity.serviceRunning.setVisibility(View.GONE);

    }


    public boolean checkServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                    Integer.MAX_VALUE)) {
                if ("com.example.omen.voice.VoiceService".equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }



    @Override
    public void onSpecifiedCommandPronounced(String event) {
        Log.d(TAG, "onSpecifiedCommandPronounced: ");
        HomeMainActivity.speakNow.setVisibility(View.GONE);

        if (Speech.getInstance().isListening()) {
            muteBeepSoundOfRecorder();
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this).request(Manifest.permission.RECORD_AUDIO).subscribe(granted -> {
                if (granted) { // Always true pre-M
                    try {
                        Speech.getInstance().stopTextToSpeech();
                        Speech.getInstance().startListening(null, this);
                    } catch (SpeechRecognitionNotAvailable exc) {
                        //showSpeechNotSupportedDialog();
                        Log.d(TAG, "onSpecifiedCommandPronounced:SpeechRecognitionNotAvailable ");

                    } catch (GoogleVoiceTypingDisabledException exc) {
                        //showEnableGoogleVoiceTyping();
                        Log.d(TAG, "onSpecifiedCommandPronounced: GoogleVoiceTypingDisabledException");
                    }
                } else {
                    Toast.makeText(this, "Please grant the permission to use the microphone", Toast.LENGTH_LONG).show();
                }
            });
            muteBeepSoundOfRecorder();
        }


    }

    @Override
    public void onStartOfSpeech() {
        Log.d(TAG, "onStartOfSpeech: ");

    }

    @Override
    public void onSpeechRmsChanged(float value) {
        Log.d(TAG, "onSpeechRmsChanged: ");
        HomeMainActivity.speakNow.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSpeechPartialResults(List<String> results) {

        for (String partial : results) {
            Log.d(TAG, "onSpeechPartialResults: "+partial);
        }
    }

    @Override
    public void onSpeechResult(String result) {

        Log.d(TAG, "onSpeechResult: "+result);
        if (!TextUtils.isEmpty(result)) {

            HomeMainActivity.voiceToText.setText(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();


            //Boolean found = Arrays.asList(result.split(" ")).contains(keyword);
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("help");
            arrayList.add("bachao");
            arrayList.add("save me");

            Boolean found=false ;
            for (String key:arrayList) {
                found= result.toLowerCase().contains(key);
                if(found)
                    break;
            }
            if(found){
                HomeFragment.panicButton.performClick();

            }

        }
    }


    private void muteBeepSoundOfRecorder() {
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (amanager != null) {
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }
}