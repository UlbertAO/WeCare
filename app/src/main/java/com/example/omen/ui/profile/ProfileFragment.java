package com.example.omen.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omen.Miscellaneous;
import com.example.omen.R;
import com.google.android.gms.common.internal.Objects;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import butterknife.OnClick;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private ProfileViewModel profileViewModel;

    private final int STORAGE_PERMISSION=121;
    private  int PICK_IMAGE=0;

    public static final int REQUEST_IMAGE = 100;
    public static final int THUMBNAIL_SIZE = 240;

    SharedPreferences prefs;
    File file;

    public static boolean SETTING_CLICK=false;


    CircleImageView pfp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("asd", "onCreateView: ");
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root=inflater.inflate(R.layout.fragment_profile, container, false);


        SharedPreferences prefs = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
        String name=prefs.getString("name","DEMO USER");
        String email=prefs.getString("email","DEMO EMAIL");
        String phone=prefs.getString("phone","DEMO PHONE NUMBER");

        TextView tv=(TextView) root.findViewById(R.id.name);
        tv.setText(name);
        tv=(TextView)root.findViewById(R.id.email);
        tv.setText(email);
        tv=(TextView)root.findViewById(R.id.phn);
        tv.setText(phone);

        pfp=root.findViewById(R.id.pfp_img);

        //looking for shared preferences if we have previously saved or changed pfp
        prefs = getActivity().getSharedPreferences("pfpImg", Context.MODE_PRIVATE);
        String imgPref=prefs.getString("imgPref","NOIMG");
        if(imgPref!="NOIMG")
            pfp.setImageBitmap(Miscellaneous.decodeBase64(imgPref));


        pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("asd", "onClick: pfp  ");
                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions(getActivity());
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    Miscellaneous.showSettingsDialog(getActivity());
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();


//
//                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                try {
//                    i.setType("image/*");
//                    i.putExtra("return-data", true);
//                    startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE);
//                }catch (ActivityNotFoundException ex){
//                    ex.printStackTrace();
//                }
            }
        });


        ImageView modifyProfile=root.findViewById(R.id.modify_profile);
        modifyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView modifyImg=root.findViewById(R.id.modify_img);
                ImageView modifyEmail=root.findViewById(R.id.modify_email);
                ImageView modifyName=root.findViewById(R.id.modify_name);
                ImageView modifyPhone=root.findViewById(R.id.modify_phn);
                ImageView modifyAddress=root.findViewById(R.id.modify_add);

                if(!SETTING_CLICK) {
                    DrawableCompat.setTint(modifyProfile.getDrawable(), ContextCompat.getColor(getContext(), R.color.primary_darker));
                    Toast.makeText(getActivity(),"DONT FORGET TO TURN IT OFF",Toast.LENGTH_LONG).show();
                        modifyImg.setVisibility(View.VISIBLE);
                        modifyName.setVisibility(View.VISIBLE);
                        modifyEmail.setVisibility(View.VISIBLE);
                        modifyPhone.setVisibility(View.VISIBLE);
                        modifyAddress.setVisibility(View.VISIBLE);

                        modifyImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("asd", "onClick:modifyImg  ");
                                Dexter.withActivity(getActivity())
                                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        .withListener(new MultiplePermissionsListener() {
                                            @Override
                                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                                if (report.areAllPermissionsGranted()) {
                                                    showImagePickerOptions(getActivity());
                                                }

                                                if (report.isAnyPermissionPermanentlyDenied()) {
                                                    Miscellaneous.showSettingsDialog(getActivity());
                                                }
                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                                token.continuePermissionRequest();
                                            }
                                        }).check();

                            }
                        });

                        SharedPreferences prefs = getActivity().getSharedPreferences("details", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();

                        String name = prefs.getString("name", "DEMO USER NAME");
                        String email = prefs.getString("email", "DEMO EMAIL");
                        String phone = prefs.getString("phone", "DEMO PHONE NUMBER");
                        String address = prefs.getString("address", "DEMO ADDRESS");


                        modifyName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity().getApplicationContext(), "UPDATE YOUR NAME", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                    final EditText input = new EditText(getActivity());
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.MATCH_PARENT);
                                    input.setLayoutParams(lp);
                                    input.setHint(name);
                                    alertDialog.setView(input);
                //                    alertDialog.setIcon(R.drawable.key);

                                    alertDialog.setPositiveButton("UPDATE",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String name = input.getText().toString();

                                                    if(name.isEmpty()||name.length()<2){

                                                        Toast.makeText(getActivity().getApplicationContext(),
                                                                "ENTER VALID NAME: more than 1 letter", Toast.LENGTH_LONG).show();
                                                        dialog.dismiss();
                                                    }else{
                                                        TextView tv=(TextView) root.findViewById(R.id.name);
                                                        tv.setText(name);

                                                        editor.putString("name",name);
                                                        editor.apply();

                                                        Toast.makeText(getActivity().getApplicationContext(),
                                                                "SUCCESSFULLY UPDATED YOUR NAME TO:\""+name+"\"", Toast.LENGTH_LONG).show();
                                                        Log.d("asd", "onClick:"+name);
                                                    }
                                                }
                                            });

                                    alertDialog.setNegativeButton("CANCEL",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });

                                    alertDialog.show();
                            }
                        });


                    modifyEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(getActivity().getApplicationContext(), "UPDATE YOUR EMAIL", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            input.setHint(email);
                            alertDialog.setView(input);
                            alertDialog.setPositiveButton("UPDATE",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            String email = input.getText().toString();

                                            if(email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "ENTER VALID EMAIL", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }else{
                                                TextView tv=(TextView) root.findViewById(R.id.email);
                                                tv.setText(email);

                                                editor.putString("email",email);
                                                editor.apply();

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "SUCCESSFULLY UPDATED YOUR EMAIL TO:\""+email+"\"", Toast.LENGTH_LONG).show();
                                                Log.d("asd", "onClick:"+name);
                                            }
                                        }
                                    });

                            alertDialog.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            alertDialog.show();

                        }
                    });

                    modifyPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(), "UPDATE YOUR PHONE NUMBER", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            input.setHint(phone);
                            alertDialog.setView(input);
                            //                    alertDialog.setIcon(R.drawable.key);

                            alertDialog.setPositiveButton("UPDATE",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            String phone = input.getText().toString();

                                            if(phone.isEmpty()||phone.length()!=10){

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "ENTER VALID PHONE NUMBER :exact 10 digits", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }else{
                                                TextView tv=(TextView) root.findViewById(R.id.phn);
                                                tv.setText(phone);

                                                editor.putString("phone",phone);
                                                editor.apply();

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "SUCCESSFULLY UPDATED YOUR PHONE NUMBER TO:\""+phone+"\"", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            alertDialog.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            alertDialog.show();

                        }
                    });

                    modifyAddress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(), "UPDATE YOUR ADDRESS", Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                            final EditText input = new EditText(getActivity());
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.MATCH_PARENT);
                            input.setLayoutParams(lp);
                            input.setHint(address);
                            alertDialog.setView(input);

                            alertDialog.setPositiveButton("UPDATE",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            String address = input.getText().toString();

                                            if(address.isEmpty()){

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "ENTER VALID ADDRESS", Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }else{
                                                TextView tv=(TextView) root.findViewById(R.id.address);
                                                tv.setText(address);

                                                editor.putString("address",address);
                                                editor.apply();

                                                Toast.makeText(getActivity().getApplicationContext(),
                                                        "SUCCESSFULLY UPDATED YOUR ADDRESS TO:\""+address+"\"", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                            alertDialog.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                            alertDialog.show();
                        }
                    });

                    SETTING_CLICK=true;//ll help to visible other icons
                }else{
                    DrawableCompat.setTint(modifyProfile.getDrawable(), ContextCompat.getColor(getContext(), R.color.white));
                    modifyImg.setVisibility(View.GONE);
                    modifyName.setVisibility(View.GONE);
                    modifyEmail.setVisibility(View.GONE);
                    modifyPhone.setVisibility(View.GONE);
                    modifyAddress.setVisibility(View.GONE);

                    SETTING_CLICK=false;
                }
            }
        });


        return root;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                Log.d("asd", "onActivityResult: PFP FRAGMWNT"+uri);
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = ThumbnailUtils.extractThumbnail(MediaStore.Images.Media.getBitmap(getActivity()
                            .getContentResolver(), uri), THUMBNAIL_SIZE, THUMBNAIL_SIZE);

                    //receiving bitmap object of image and the getting its thubnail of THUMBNAIL_SIZE

                    Log.d("asd", "onActivityResult: bitmap: "+bitmap);

                    prefs = getActivity().getSharedPreferences("pfpImg", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("imgPref", Miscellaneous.encodeTobase64(bitmap));
                    editor.apply();

                    //String imgPref=prefs.getString("imgPref","NOIMG");
                    pfp.setImageBitmap(bitmap);

                } catch (IOException e) {
                    Toast.makeText(getActivity(),"SOMETHING WENT WRONG: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }



    private void showImagePickerOptions(Context context) {
        ImagePickerActivity.showImagePickerOptions(context, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(context);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(context);
            }
        });
    }

    private void launchCameraIntent(Context context) {
        Log.d("asd", "launchCameraIntent: ");
        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent(Context context) {
        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    //now check onActivityResult

}