package com.example.omen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.omen.onboard.EntryActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start home activity
        if(savedInstanceState==null)
            Log.d("asd", "onCreate: ");
        else
            Log.d("asd", "onCreate: NONONONON");
        //real onoe
        startActivity(new Intent(SplashActivity.this, EntryActivity.class));

        //startActivity(new Intent(SplashActivity.this, HomeMainActivity.class));
        // close splash activity
        finish();
    }
}
