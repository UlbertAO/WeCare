package com.example.omen.ui.home;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.omen.Miscellaneous;
import com.example.omen.R;
import com.example.omen.emergencycontact.EmergencyContact;
import com.example.omen.emergencycontact.ListContactsFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public static Button panicButton;

    static LocationCallback mLocationCallback;
    static Location mCurrentLocation;
    static String mLastUpdateTime;

    static FusedLocationProviderClient mFusedLocationClient;
    static SettingsClient mSettingsClient;
    static LocationRequest mLocationRequest;
    static LocationSettingsRequest mLocationSettingsRequest;
    static double latitude,longitude;
    static float accuracy=Float.POSITIVE_INFINITY;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        panicButton=root.findViewById(R.id.panic_btn);
        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"WILL HELP YOU NOW JUST WAIT",Toast.LENGTH_LONG).show();


                ListContactsFragment.setContactList(getContext());
//                //FOR CALL and msg setting contact list

                if(ListContactsFragment.getEmergencyContactsArrayList().size()>0) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.postDelayed(this, 5 * 60 * 1000); // every 5 minutes
                        /* your longer code here */
                        Log.d("dsa", "run: sending msg once again");
                        autoSms(getContext());
                    }
                }, 0); // first run instantly

//                //calling after msgs are sent

                    ListContactsFragment.troubleCall(getContext());
                }else
                    Toast.makeText(getActivity(), "ADD EMERGENCY CONTACTS FIRST",Toast.LENGTH_LONG).show();

                    ExtendedFloatingActionButton mAddFab = getActivity().findViewById(R.id.fab);
                    mAddFab.performClick();
            }
        });


        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        return root;
    }


    public static void autoSms(Context context){
        Dexter.withActivity((Activity) context)
                .withPermission(Manifest.permission.SEND_SMS )
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {


                        ProgressDialog progressDialog = new ProgressDialog((Activity) context, R.style.AppTheme_Entry_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Fetchng Location Information...");
                        progressDialog.show();



                        // location updates interval - 10sec
                        final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*3 ;
                        // fastest updates interval - 5 sec
                        // location updates will be received if another app is requesting the locations
                        // than your app can handle
                        final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;




                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient((Activity) context);

                        mLocationCallback = new LocationCallback() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);
                                // location is received
                                mCurrentLocation = locationResult.getLastLocation();
                                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                                Log.d("asd", "autosms: RESULT: lat:"+mCurrentLocation.getLatitude()+
                                        " long: "+mCurrentLocation.getLongitude()+" accuracy: "+mCurrentLocation.getAccuracy()+
                                        " speed "+mCurrentLocation.getSpeed()+" speed meter psec "+
                                        mCurrentLocation.getSpeedAccuracyMetersPerSecond());
                                if(mCurrentLocation.getAccuracy()<accuracy){
                                    accuracy=mCurrentLocation.getAccuracy();
                                    latitude=mCurrentLocation.getLatitude();
                                    longitude=mCurrentLocation.getLongitude();
                                }
                            }
                        };

                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
                        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                        builder.addLocationRequest(mLocationRequest);
                        mLocationSettingsRequest = builder.build();

                        new CountDownTimer(1000*15,1000){
                            @Override
                            public void onTick(long millisUntilFinished) {
                                Log.d("asd","CHAL RAHA H");
                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                        mLocationCallback, Looper.myLooper());
                            }
                            @Override
                            public void onFinish() {
                                Log.d("asd","KHATAM");
                                mFusedLocationClient
                                        .removeLocationUpdates(mLocationCallback)
                                        .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                progressDialog.dismiss();

                                                String msg = "ALERT:WECARE APP\nIm in trouble.\nMy last known location with accuracy :"+accuracy+
                                                        "m was \n\nhttps://www.google.com/maps/place/" + latitude+","+ longitude ;//main msg

                                                SharedPreferences preferences = context.getSharedPreferences("audio",Context.MODE_PRIVATE);
                                                Boolean av=preferences.getBoolean("isAudioAv",false);
                                                if(av) {
                                                    String link = preferences.getString("audioURL", "AUDIO LINK NOT AVAILABLE");
                                                    msg = msg + "\nAs a proof here an audio recording of myself\n" + link;
                                                }

                                                try{
                                                    ArrayList<EmergencyContact> contactList= ListContactsFragment.getEmergencyContactsArrayList();
                                                    for (EmergencyContact emg:contactList) {

                                                        Log.d("asd", "onPermissionGranted: sending msg");
                                                        SmsManager.getDefault().sendTextMessage(emg.getPhoneNumber().toString(),
                                                                null, msg, null, null);
                                                    }
                                                }catch(Exception e){
                                                    AlertDialog.Builder alert=new AlertDialog.Builder(context);
                                                    AlertDialog dialog=alert.create();
                                                    dialog.setMessage(e.getMessage());
                                                    dialog.show();
                                                }

                                            }
                                        });

                            }
                        }.start();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Miscellaneous.showSettingsDialog((Activity) context);

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
                }).check();
    }
}