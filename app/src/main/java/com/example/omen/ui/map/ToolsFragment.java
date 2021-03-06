package com.example.omen.ui.map;
/*
import android.location.Location;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.omen.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.skyfishjy.library.RippleBackground;

import java.util.List;
*/


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.omen.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;


import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
/*
import com.tika.app2.GetLocation;
import com.tika.app2.Home.LocationCapture;
import com.tika.app2.R;
*/
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.Observer;


public class ToolsFragment extends Fragment implements OnMapReadyCallback {

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000*5;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 1000*5;
    final static int REQUEST_LOCATION =51;



    private static final String TAG = "dbug";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private Button btnFind;
    private RippleBackground rippleBg;

    private Button btnShareLoc;

    private final int LOCATION_PERMISSION = 42;
    private final float DEFAULT_ZOOM = 15;

    private ToolsViewModel toolsViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);

        Log.d("asd", "onCreateView: ");


        //MY

        materialSearchBar = root.findViewById(R.id.searchBar);
        btnFind = root.findViewById(R.id.btn_find);
        rippleBg = (RippleBackground)root.findViewById(R.id.ripple_bg);
        btnShareLoc = root.findViewById(R.id.btn_share_loc);

        //SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        //SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mapFragment);
        fragmentTransaction.commit();
        mapFragment.getMapAsync(this);


        Log.d("asd", "onCreateView: ");

        //mapFragment.getMapAsync(this);

        mapView = mapFragment.getView();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        Places.initialize(getActivity(), getString(R.string.google_maps_api));
        placesClient = Places.createClient(getActivity());


        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }
            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent browserIntent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/"+text.toString()));
                startActivity(browserIntent);
                getActivity().finish();
            }
            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer witin search bar
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                }else if(buttonCode == MaterialSearchBar.BUTTON_SPEECH){
                    //openVoiceRecognizer();
                }

            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
                                }
                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(),"Prediction Fetching Task Unsuccessful:please ensure that you are connected to internet:",Toast.LENGTH_LONG).show();
                            Log.i(TAG, "prediction fetching task unsuccessful");

                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                Log.d(TAG, "OnItemClickListener: clicked on auto predict item");
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                String suggestion = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(suggestion);
                Log.d(TAG, "OnItemClickListener: suggestion"+suggestion);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        materialSearchBar.clearSuggestions();
                    }
                }, 1000);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        Log.i(TAG, "Place found: " + place.getName());
                        LatLng latLngOfPlace = place.getLatLng();
                        if (latLngOfPlace != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOfPlace, DEFAULT_ZOOM));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i(TAG, "place not found: " + e.getMessage());
                            Log.i(TAG, "status code: " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {
            }
        });




        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LatLng currentMarkerLocation = mMap.getCameraPosition().target;
                rippleBg.startRippleAnimation();


                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){

                    LocationTrack locationTrack = new LocationTrack(getContext());

                    if (locationTrack.canGetLocation()) {
                        double longitude = locationTrack.getLongitude();
                        double latitude = locationTrack.getLatitude();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                double longitude = locationTrack.getLongitude();
                                double latitude = locationTrack.getLatitude();

                                Log.i(TAG, "run: #asd###################" + latitude + " " + longitude);
                                rippleBg.stopRippleAnimation();

                                GetLocation getLocation = new GetLocation(getActivity(), getContext().getApplicationContext());

                                SharedPreferences prefs = getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

                                latitude = Double.parseDouble(prefs.getString("latitude", "0"));
                                longitude = Double.parseDouble(prefs.getString("longitude", "0"));

                                Log.i(TAG, "run: ####################" + latitude + " " + longitude);

                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/police+station/" + "@" + longitude + "/" + latitude));

                                startActivity(browserIntent);
                                getActivity().finish();
                            }
                        }, 3000);


                    } else {
                        showSettingsAlert();
                        rippleBg.stopRippleAnimation();
                    }
                }else{

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
                }
            }
        });


        btnShareLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("asd", "onClick: ");

                LatLng currentMarkerLocation = mMap.getCameraPosition().target;
                rippleBg.startRippleAnimation();

                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED){

                        //what if location is enabled and we do not have permission to get it
                    LocationTrack locationTrack = new LocationTrack(getContext());

                    Log.d("asd", "onClick: ");

                    if (locationTrack.canGetLocation()) {
                        double longitude = locationTrack.getLongitude();
                        double latitude = locationTrack.getLatitude();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                double longitude = locationTrack.getLongitude();
                                double latitude = locationTrack.getLatitude();
                                float accuracy=locationTrack.getAccuracy();

                                rippleBg.stopRippleAnimation();

                                GetLocation getLocation = new GetLocation(getActivity(),getContext().getApplicationContext());

                                SharedPreferences prefs = getActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE);

                                latitude = Double.parseDouble(prefs.getString("latitude","0"));
                                longitude = Double.parseDouble(prefs.getString("longitude","0"));
                                accuracy = Float.parseFloat(prefs.getString("accuracy","0"));

                                Log.i(TAG, "run: ####################" + latitude + " " + longitude+" "+accuracy+"m");
                                Toast.makeText(getActivity(),"lat:"+latitude+"--long: "+longitude+" accuracy: "+accuracy,Toast.LENGTH_LONG).show();
                            }
                        }, 10000);

                    } else {
                        showSettingsAlert();
                        rippleBg.stopRippleAnimation();
                    }
            }else{

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
            }
            }
        });



        final TextView textView = root.findViewById(R.id.text_tools);

        toolsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //need to check if it have permission or not
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);

            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 40, 180);
            }

            //check if gps is enabled or not and then request user to enable it
            showSettingsAlert();

            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    if (materialSearchBar.isSuggestionsVisible())
                        materialSearchBar.clearSuggestions();
                    if (materialSearchBar.isSearchEnabled())
                        materialSearchBar.disableSearch();
                    return false;
                }
            });
        }else{
            Log.d("asd", "onMapReady: alert box");
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOCATION ) {
//            if (resultCode == Activity.RESULT_OK) {
//                getDeviceLocation();
//            }

            switch (resultCode)
            {
                case Activity.RESULT_OK:
                {
                    // All required changes were successfully made
                    getDeviceLocation();
                    Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                    break;
                }
                case Activity.RESULT_CANCELED:
                {
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                    break;
                }
                default:
                {
                    break;
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
                                locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        } else {
                            Toast.makeText(getActivity(), "Unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    public void showSettingsAlert() {

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            //check if gps is enabled or not and then request user to enable it
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    getDeviceLocation();
                }
            });

            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        try {
                            resolvable.startResolutionForResult(getActivity(), REQUEST_LOCATION );
                        } catch (@SuppressLint("NewApi") IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }else{
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }
/*
//SAME THHING WITH CUSTOM ALERT DIALOG
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

            alertDialog.setTitle("GPS is not Enabled!");
            alertDialog.setMessage("Do you want to turn on GPS?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getActivity().startActivity(intent);
                }
            });


            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });


            alertDialog.show();
        }else{
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
        }
*/
    }

    //if granted or rejected handle here
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("asd", "onRequestPermissionsResult:REVIEWING PERMISSION REQUEST ");
        if(requestCode==LOCATION_PERMISSION){
            if(permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //means ACCESS_FINE_LOCATION granted
            }else{
                //if not granted request again
                //it ll reques once again and then never checks this function
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
                //it will ask for again n again stack over flow occures
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION);
            }
        }
    }

        //
    //
    //
    //


    //
    //
    //
    //CLASS


    public static class LocationTrack extends Service implements LocationListener {

        private final Context mContext;
        boolean checkGPS = false;
        boolean checkNetwork = false;
        boolean canGetLocation = false;

        Location loc;
        double latitude;
        double longitude;
        float accuracy;

        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

        private static final long MIN_TIME_BW_UPDATES = 1000 ;
        protected LocationManager locationManager;

        public LocationTrack(Context mContext) {
            this.mContext = mContext;
            getLocation();
        }

        private Location getLocation() {

            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // get GPS status
                checkGPS = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // get network provider status
                checkNetwork = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!checkGPS && !checkNetwork) {
                    Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
                } else {
                    this.canGetLocation = true;

                    // if GPS Enabled get lat/long using GPS Services
                    if (checkGPS) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {

                                SharedPreferences prefs = getSharedPreferences("prefs",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();

                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();

                                Log.i(TAG, "#######################onShake: lat: " + latitude + "lon: " + longitude);

                                editor.putString("latitude",String.valueOf(latitude));
                                editor.putString("longitude",String.valueOf(longitude));
                                editor.putString("accuracy",String.valueOf(accuracy));

                                editor.apply();
                                editor.commit();
                            }
                        }
                    }


                else if (checkNetwork) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (android.location.LocationListener) this);

                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                    if (loc != null) {

                        SharedPreferences prefs = getSharedPreferences("prefs",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();

                        Log.i(TAG, "#######################onShake: lat: " + latitude + "lon: " + longitude);

                        editor.putString("latitude",String.valueOf(latitude));
                        editor.putString("longitude",String.valueOf(longitude));

                        editor.apply();
                        editor.commit();

                    }
                }

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return loc;
        }

        public double getLongitude() {
            if (loc != null) {
                longitude = loc.getLongitude();
            }
            return longitude;
        }

        public double getLatitude() {
            if (loc != null) {
                latitude = loc.getLatitude();
            }
            return latitude;
        }

        public  float getAccuracy(){
            if(loc!=null){
                accuracy=loc.getAccuracy();
            }
            return accuracy;
        }

        public boolean canGetLocation() {
            return this.canGetLocation;
        }

        public void stopListener() {
            if (locationManager != null) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates((android.location.LocationListener) LocationTrack.this);
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onLocationChanged(Location location) {

        }
    }

}
