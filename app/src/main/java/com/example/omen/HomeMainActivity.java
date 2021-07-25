package com.example.omen;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.afollestad.materialdialogs.Theme;
import com.example.omen.voice.Constants;
import com.example.omen.voice.VoiceService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog.Builder;


import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeMainActivity extends AppCompatActivity {


    LocationCallback mLocationCallback;
    Location mCurrentLocation;
    String mLastUpdateTime;

    FusedLocationProviderClient mFusedLocationClient;
    SettingsClient mSettingsClient;
    LocationRequest mLocationRequest;
    LocationSettingsRequest mLocationSettingsRequest;

    // location updates interval - 10sec
    final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*3 ;
    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    float accuracy=Float.POSITIVE_INFINITY;
    double latitude,longitude;



    Button start;
    Button stop;
    final String TAG="asd";
    public static TextView speakNow;
    public static TextView voiceToText;
    public static TextView serviceRunning;

    boolean voice=true;












    private static final float END_SCALE = 0.85f;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavView;
    private CoordinatorLayout contentView;

    private final int THUMBNAIL_SIZE=100;
    private final int LOCATION_PERMISSION = 42;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);


        if(savedInstanceState==null)
            Log.d("asd", "HOMEonCreate: ");
        else
            Log.d("asd", "HOMEonCreate: NONONONON");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

        }else{

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }

        SharedPreferences pref=getSharedPreferences("login",MODE_PRIVATE);
        if(!pref.getBoolean("logged",false)) {
            pref.edit().putBoolean("logged", true).apply();
            Miscellaneous.showSettingsDialog(this);
        }

        initToolbar();
        initFab();
        initNavigation();

        voice();

        //showBottomNavigation(false);
    }
    

    public void voice(){
        start = findViewById(R.id.start);
        stop = findViewById(R.id.stop);
        speakNow = (TextView) findViewById(R.id.speak_now);
        voiceToText = (TextView) findViewById(R.id.txtViewResult);
        serviceRunning = (TextView) findViewById(R.id.serviceRunning);

        enableAutoStart();
        startService(new Intent(HomeMainActivity.this, VoiceService.class));


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: started");
                startService(new Intent(HomeMainActivity.this, VoiceService.class));
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: stop");
                stopService(new Intent(HomeMainActivity.this, VoiceService.class));

            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: mainactivity");
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


    private void enableAutoStart() {
        for (Intent intent : Constants.AUTO_START_INTENTS) {
            if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                new Builder(this).title("ENABLE AUTO START")
                        .content("ALLOW SPEECH TO ALWAYS RUN IN BACKGROUND...")
                        .theme(Theme.LIGHT)
                        .positiveText("ALLOW")
                        .onPositive((dialog, which) -> {
                            try {
                                for (Intent intent1 : Constants.AUTO_START_INTENTS)
                                    if (getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY)
                                            != null) {
                                        startActivity(intent1);
                                        break;
                                    }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        })
                        .show();
                break;
            }
        }
    }


    @Override
    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
        super.onTopResumedActivityChanged(isTopResumedActivity);
        Log.d("asd", "onTopResumedActivityChanged: ");
    }

    private void initToolbar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void initFab() {
        // Use the ExtendedFloatingActionButton to handle the
        // parent FAB

        ExtendedFloatingActionButton mAddFab = findViewById(R.id.fab);

        FloatingActionButton shareLocaiton, addEmergencyContact,voiceControl;
        TextView shareLocaitonText, emergencyContactText,voiceControlText;

        shareLocaiton = findViewById(R.id.share_location_fab);
        addEmergencyContact = findViewById(R.id.add_emergency_contact_fab);
        voiceControl = findViewById(R.id.voice_control_fab);

        shareLocaitonText = findViewById(R.id.share_location_fab_text);
        emergencyContactText = findViewById(R.id.add_emergency_contact_fab_text);
        voiceControlText = findViewById(R.id.voice_control_fab_text);

        // Now set all the FABs and all the action name
        // texts as GONE
        shareLocaiton.setVisibility(View.GONE);
        addEmergencyContact.setVisibility(View.GONE);
        voiceControl.setVisibility(View.GONE);
        shareLocaitonText.setVisibility(View.GONE);
        emergencyContactText.setVisibility(View.GONE);
        voiceControlText.setVisibility(View.GONE);


        // Set the Extended floating action button to
        // shrinked state initially
        mAddFab.shrink();


        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //Toast.makeText(HomeMainActivity.this, "Replace with your own action SHARE", Toast.LENGTH_SHORT).show();

                if (!mAddFab.isExtended()) {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs VISIBLE.
                    shareLocaiton.show();
                    addEmergencyContact.show();
                    voiceControl.show();
                    shareLocaitonText.setVisibility(View.VISIBLE);
                    emergencyContactText.setVisibility(View.VISIBLE);
                    voiceControlText.setVisibility(View.VISIBLE);

                    // Now extend the parent FAB, as
                    // user clicks on the shrinked
                    // parent FAB
                    mAddFab.extend();

                } else {
                    // when isAllFabsVisible becomes
                    // true make all the action name
                    // texts and FABs GONE.
                    shareLocaiton.hide();
                    addEmergencyContact.hide();
                    voiceControl.hide();
                    shareLocaitonText.setVisibility(View.GONE);
                    emergencyContactText.setVisibility(View.GONE);
                    voiceControlText.setVisibility(View.GONE);

                    // Set the FAB to shrink after user
                    // closes all the sub FABs
                    mAddFab.shrink();
                }
            }
        });

        voiceControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout voiceControlLL=findViewById(R.id.voice_control_ll);
                if(voice) {
                    voiceControlLL.setVisibility(View.GONE);
                    voiceControlText.setText("SHOW VOICE CONTROLS");
                    voice=false;
                }else{
                    voiceControlLL.setVisibility(View.VISIBLE);
                    voiceControlText.setText("HIDE VOICE CONTROLS");
                    voice=true;

                }
            }
        });

        addEmergencyContact.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent contactIntent=new Intent(HomeMainActivity.this,EmergencyContactActivity .class);
                        startActivity(contactIntent);
                    }
                });

        shareLocaiton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dexter.withActivity(HomeMainActivity.this)
                                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {


                                        ProgressDialog progressDialog = new ProgressDialog(HomeMainActivity.this, R.style.AppTheme_Entry_Dialog);
                                        progressDialog.setIndeterminate(true);
                                        progressDialog.setMessage("Fetchng Location Information...");
                                        progressDialog.show();


                                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(HomeMainActivity.this);
                                        mLocationCallback = new LocationCallback() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onLocationResult(LocationResult locationResult) {
                                                super.onLocationResult(locationResult);
                                                // location is received
                                                mCurrentLocation = locationResult.getLastLocation();
                                                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                                                Log.d("asd", "onLocationResult: RESULT: lat:"+mCurrentLocation.getLatitude()+
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


                                        Log.d("asd", "onCreateView: done with location setup");

                                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                                        builder.addLocationRequest(mLocationRequest);
                                        mLocationSettingsRequest = builder.build();
                                        Log.d("asd", "onCreateView: done with location setting");

                                        new CountDownTimer(1000*15,1000){
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                                        mLocationCallback, Looper.myLooper());
                                            }
                                            @Override
                                            public void onFinish() {
                                                mFusedLocationClient
                                                        .removeLocationUpdates(mLocationCallback)
                                                        .addOnCompleteListener(HomeMainActivity.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                progressDialog.dismiss();

                                                                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                                                sharingIntent.setType("text/plain");
                                                                String shareBody = "Im in trouble. my last known location with accuracy :"+accuracy+
                                                                        "m was \n\nhttps://www.google.com/maps/place/" + latitude+","+ longitude ;//main msg
                                                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "HELP");//for mail
                                                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                                                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                                            }
                                                        });
                                            }
                                        }.start();

                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Miscellaneous.showSettingsDialog(HomeMainActivity.this);

                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();

                                    }
                                }).check();
                    }
                });

    }

    private void initNavigation() {

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavView = findViewById(R.id.bottom_nav_view);
        contentView = findViewById(R.id.content_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
       mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_tools, R.id.nav_share,
                R.id.bottom_home, R.id.bottom_dashboard, R.id.bottom_notifications,R.id.bottom_profile)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavView, navController);
        animateNavigationDrawer();


    }


    private void animateNavigationDrawer() {
//        drawerLayout.setScrimColor(getResources().getColor(R.color.text_brown));
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


                NavigationView navigationView = findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);

                SharedPreferences prefs = getSharedPreferences("details", Context.MODE_PRIVATE);
                String name = prefs.getString("name", "DEMO USER");
                String email = prefs.getString("email", "DEMO EMAIL");
                //we have name email phone password in details pref
                TextView tv = (TextView) headerView.findViewById(R.id.name);
                tv.setText(name);
                tv = (TextView) headerView.findViewById(R.id.email);
                tv.setText(email);

                //for drawer pfp
                CircleImageView pfp = headerView.findViewById(R.id.drawer_pfp_img);
                //looking for shared preferences if we have previously saved or changed pfp
                prefs =getSharedPreferences("pfpImg", Context.MODE_PRIVATE);
                String imgPref=prefs.getString("imgPref","NOIMG");
                if(imgPref!="NOIMG")
                    pfp.setImageBitmap(ThumbnailUtils.extractThumbnail(Miscellaneous.decodeBase64(imgPref)
                            , THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                //we need smaller image for drawer so again this



            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.HAMBURGUR that 3 dot
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                return true;
            case R.id.action_aboutus:
                //To open a URL website you do the following:
                String url = "https://ulbertao.github.io/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //automatically handle the back arrow navigation and also the back button when pressed.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }



}
