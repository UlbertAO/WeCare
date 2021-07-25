package com.example.omen.ui.map;

import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;

public class LocationCapture {

    public static double longitude;
    public static double latitude;
    double lat;
    double longit;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView;
    Button xyz;
    String action;

}
