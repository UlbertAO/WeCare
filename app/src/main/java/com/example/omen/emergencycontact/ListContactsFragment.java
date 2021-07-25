package com.example.omen.emergencycontact;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omen.Miscellaneous;
import com.example.omen.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class ListContactsFragment extends Fragment {

    static RecyclerView recyclerView;
    static EmergencyContactRecyclerViewAdapter emergencyContactRecyclerViewAdapter;
    static ArrayList<EmergencyContact> emergencyContactsArrayList = new ArrayList<>();

    static Context context;
    static TextView isEmpty;
    static EmergencyContactDbHandler db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_list_contacts, container, false);

        context=getActivity();
        isEmpty=root.findViewById(R.id.is_empty);
        recyclerView=root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // db=new EmergencyContactDbHandler(getActivity());
//
//        if(db.getCount()==0) {
//            isEmpty.setVisibility(View.VISIBLE);
//            Toast.makeText(getActivity(), "No Contact Added Yet", Toast.LENGTH_SHORT).show();
//        }else {
////            emergencyContactsArrayList = new ArrayList<>();
//
//            List<EmergencyContact> allContacts = db.getAllContacts();
//            if(db.getCount()!=emergencyContactsArrayList.size()) {
//                for (EmergencyContact emergencyContact : allContacts) {
//                    Log.d("asd", emergencyContact.getName() + ":" + emergencyContact.getPhoneNumber());
//                    emergencyContactsArrayList.add(emergencyContact);
//                }
//            }
        if(setContactList(getActivity())){
            emergencyContactRecyclerViewAdapter = new EmergencyContactRecyclerViewAdapter(getActivity(), emergencyContactsArrayList);
            recyclerView.setAdapter(emergencyContactRecyclerViewAdapter);
        }else{
            isEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(context, "No Contact Added Yet", Toast.LENGTH_SHORT).show();
        }
//        db.close();

        //troubleCall();

        return root;
    }

    public static ArrayList<EmergencyContact> getEmergencyContactsArrayList(){
        return emergencyContactsArrayList;
    }
    public static Boolean setContactList(Context context) {

        db = new EmergencyContactDbHandler(context);

        if (db.getCount() == 0) {
            db.close();
            return false;
        } else {
//
            List<EmergencyContact> allContacts = db.getAllContacts();
            if(db.getCount()!=emergencyContactsArrayList.size()) {
                for (EmergencyContact emergencyContact : allContacts) {
                    Log.d("dsa", emergencyContact.getName() + ":" + emergencyContact.getPhoneNumber());
                    emergencyContactsArrayList.add(emergencyContact);
                }
            }
            db.close();
        return true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public static void emergencyContactsArrayListAdd(EmergencyContact emergencyContact){
        Log.d("asd", "emergencyContactsArrayListAdd: ");
//        emergencyContactsArrayList = new ArrayList<>();
        emergencyContactsArrayList.add(emergencyContact);
        Log.d("asd", "emergencyContactsArrayListAdd: ");
//        emergencyContactRecyclerViewAdapter.notifyDataSetChanged();
        emergencyContactRecyclerViewAdapter = new EmergencyContactRecyclerViewAdapter(context, emergencyContactsArrayList);
        recyclerView.setAdapter(emergencyContactRecyclerViewAdapter);

        if(db.getCount()!=0){
            isEmpty.setVisibility(View.GONE);
        }
        Log.d("asd", "emergencyContactsArrayListAdd: ");

    }
    public static void emergencyContactsArrayListRemove(int position){
//        Log.d("asd", "emergencyContactsArrayListRemove: "+position+"---"+emergencyContactsArrayList.toString());
        emergencyContactsArrayList.remove(position);
        emergencyContactRecyclerViewAdapter.notifyDataSetChanged();
        if(db.getCount()==0){
            isEmpty.setVisibility(View.VISIBLE);
        }

//        Log.d("asd", "emergencyContactsArrayListRemove: "+emergencyContactsArrayList.toString());

    }

    public static void troubleCall(Context context){
        Log.d("asd", "troubleCall: entry");
            Dexter.withActivity((Activity) context)
                    .withPermission(Manifest.permission.CALL_PHONE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Log.d("asd", "troubleCall: intent");
                            if(emergencyContactsArrayList.size()>0){
                            //trying to call all one by one if not picked FAILED
                                EmergencyContact emg=emergencyContactsArrayList.get(0);
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + emg.getPhoneNumber().toString()));//change the number
                                context.startActivity(callIntent);

                            }
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Miscellaneous.showSettingsDialog(context);
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();

                        }
                    }).check();

    }
}