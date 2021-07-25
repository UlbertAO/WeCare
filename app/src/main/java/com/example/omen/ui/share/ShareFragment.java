package com.example.omen.ui.share;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.omen.BuildConfig;
import com.example.omen.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ShareFragment extends Fragment {

    private ShareViewModel shareViewModel;


    Button share;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);


//        mSettingsClient = LocationServices.getSettingsClient(getActivity());




        /*
        final TextView textView = root.findViewById(R.id.text_share);
        shareViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */


        share =root.findViewById(R.id.shareApk);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationInfo app = getActivity().getApplicationContext().getApplicationInfo();
                String filePath = app.sourceDir;

                Intent intent = new Intent(Intent.ACTION_SEND);

                // MIME of .apk is "application/vnd.android.package-archive".
                // but Bluetooth does not accept this. Let's use "*/*" instead.
                intent.setType("*/*");

                // Append file and send Intent
                File originalApk = new File(filePath);

                try {
                    //Make new directory in new location=
                    File tempFile = new File(getActivity().getExternalCacheDir() + "/ExtractedApk");
                    //If directory doesn't exists create new
                    if (!tempFile.isDirectory())
                        if (!tempFile.mkdirs())
                            return;
                    //Get application's name and convert to lowercase
                    tempFile = new File(tempFile.getPath() + "/" + getString(app.labelRes).replace(" ","").toLowerCase() + ".apk");
                    //If file doesn't exists create new
                    if (!tempFile.exists()) {
                        if (!tempFile.createNewFile()) {
                            return;
                        }
                    }
                    //Copy file to new location
                    InputStream in = new FileInputStream(originalApk);
                    OutputStream out = new FileOutputStream(tempFile);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    System.out.println("File copied.");
                    //Open share dialog
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
                    Uri photoURI = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", tempFile);
//          intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    startActivity(Intent.createChooser(intent, "Share app via"));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



        return root;
    }


}