package com.example.omen.emergencycontact;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omen.R;

import static android.app.Activity.RESULT_OK;

public class AddContactFragment extends Fragment {


    final int ADD_CONTACT=121;
    ImageView contacts;
    EditText name;
    EditText number;
    Button add;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View root=inflater.inflate(R.layout.fragment_add_contact, container, false);

        contacts=root.findViewById(R.id.contacts);
        name=root.findViewById(R.id.contact_name);
        number=root.findViewById(R.id.contact_number);
        add=root.findViewById(R.id.add_emergency_contact);

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent,ADD_CONTACT );
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validate()){
                    Toast.makeText(getActivity(),"Enter Valid Details",Toast.LENGTH_LONG).show();
                    return;
                }

                String contact_name=name.getText().toString();
                String contact_number=number.getText().toString();

                //add to db

                EmergencyContact emergencyContact=new EmergencyContact();
                emergencyContact.setName(contact_name);
                emergencyContact.setPhoneNumber(contact_number);

                EmergencyContactDbHandler db=new EmergencyContactDbHandler(getActivity());
                boolean isAdded=db.insertContact(emergencyContact);
                if(isAdded){

                    Toast.makeText(getActivity(),"ADDED SUCCESSFULLY",Toast.LENGTH_LONG).show();
                    ListContactsFragment.emergencyContactsArrayListAdd(emergencyContact);
                }
                else Toast.makeText(getActivity(),"FAILED TO ADD : probably added already",Toast.LENGTH_LONG).show();

            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==ADD_CONTACT){
            if(resultCode==RESULT_OK){

                Uri contactData = data.getData();
                Cursor cursor = getActivity().managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                name.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                number.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)));

            }
        }
    }


    public boolean validate(){
        boolean valid=true;

        String contact_name=name.getText().toString();
        String contact_number=number.getText().toString();

        if(contact_name.isEmpty()){
            name.setError("Cannot be empty");
            valid=false;
        }else{
            name.setError(null);
        }
        if(contact_number.isEmpty()){
            number.setError("Invalid phone number");
            valid=false;
        }else{
            number.setError(null);
        }
        return valid;
    }



}