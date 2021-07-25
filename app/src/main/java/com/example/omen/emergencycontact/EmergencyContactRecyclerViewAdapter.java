package com.example.omen.emergencycontact;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.omen.Miscellaneous;
import com.example.omen.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;

public class EmergencyContactRecyclerViewAdapter extends RecyclerView.Adapter<EmergencyContactRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<EmergencyContact> emergencyContactList;

    public EmergencyContactRecyclerViewAdapter(Context context, List<EmergencyContact> emergencyContactList) {
        this.context = context;
        this.emergencyContactList = emergencyContactList;
    }

    @NonNull
    @Override
    public EmergencyContactRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_emergency_contact, parent, false);
        return new EmergencyContactRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  EmergencyContactRecyclerViewAdapter.ViewHolder holder, int position) {

        EmergencyContact emergencyContact=emergencyContactList.get(position);

        holder.name.setText(emergencyContact.getName());
        holder.phone_number.setText(emergencyContact.getPhoneNumber());

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert=new AlertDialog.Builder(context);
                alert.setMessage("Do you really want to remove ?")
                        .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                        }).setNegativeButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EmergencyContact emgcontact= new EmergencyContact();
                            emgcontact.setPhoneNumber(emergencyContact.getPhoneNumber());
                            emgcontact.setName(emergencyContact.getName());

                            EmergencyContactDbHandler db=new EmergencyContactDbHandler(context);
                            db.deleteContact(emgcontact);

                            ListContactsFragment.emergencyContactsArrayListRemove(position);

                        }});
                    AlertDialog alertBox=alert.create();
                    alertBox.show();
                }
            });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity((Activity) context)
                        .withPermission(Manifest.permission.CALL_PHONE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+holder.phone_number.getText().toString()));//change the number
                                context.startActivity(callIntent);
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
        });



    }

    @Override
    public int getItemCount() {
        return emergencyContactList.size();
    }


    //viewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView phone_number;
        public ImageView delete;
        public ImageView call;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            phone_number=itemView.findViewById(R.id.phone_number);
            call=itemView.findViewById(R.id.call);
            delete=itemView.findViewById(R.id.delete);
        }

    }



}


