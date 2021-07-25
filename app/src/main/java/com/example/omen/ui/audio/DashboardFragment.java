package com.example.omen.ui.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.omen.R;

import java.net.URL;


public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    Button add;
    EditText audioUrl;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        audioUrl=root.findViewById(R.id.inputAudioUrl);
        add =root.findViewById(R.id.addAudioUrl);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    SharedPreferences preferences = getActivity().getSharedPreferences("audio",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("audioURL",audioUrl.getText().toString());
                    editor.putBoolean("isAudioAv",true);
                    editor.apply();

                }else{
                    Toast.makeText(getActivity(), "Enter Valid URL",Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    public Boolean validate(){
        String link=audioUrl.getText().toString();

        try{
            new URL(link).toURI();
            return true;
        }catch(Exception e){
            audioUrl.setError("Enter valid URL");
            return false;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = getActivity().getSharedPreferences("audio",Context.MODE_PRIVATE);
        Boolean av=preferences.getBoolean("isAudioAv",false);
        if(av){
            add.setText("UPDATE");
            String link=preferences.getString("audioURL","AUDIO LINK NOT AVAILABLE");
            audioUrl.setText(link);

        }else{

            Toast.makeText(getActivity(), "Enter Audio Link Before You Get Into Trouble",Toast.LENGTH_LONG).show();
        }
    }
}