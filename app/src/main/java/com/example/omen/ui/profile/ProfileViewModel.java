package com.example.omen.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {
    // TODO: Implement the ViewModel


    private MutableLiveData<String> mText;

    public ProfileViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is PROFILE fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}