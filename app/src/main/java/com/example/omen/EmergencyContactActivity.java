package com.example.omen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.omen.emergencycontact.AddContactFragment;
import com.example.omen.emergencycontact.ListContactsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class EmergencyContactActivity extends AppCompatActivity {


    Toolbar tab_toolbar;
    TabLayout tab_tablayout;
    ViewPager tab_viewpager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact);

        tab_toolbar = findViewById(R.id.toolbar);
        tab_viewpager = findViewById(R.id.tab_viewpager);
        tab_tablayout = findViewById(R.id.tab_tablayout);

        tab_toolbar.setTitle("EMERGENCY CONTACT");
        setSupportActionBar(tab_toolbar);
        //back icon and to go back make parent in menifest file
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

        setupViewPager(tab_viewpager);
        tab_tablayout.setupWithViewPager(tab_viewpager);
    }

    private void setupViewPager(ViewPager tab_viewpager) {
        ViewPagerAdapter adapter =new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AddContactFragment(),"ADD CONTACT");
        adapter.addFragment(new ListContactsFragment(),"ADDED CONTACTS");

        tab_viewpager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        final ArrayList<Fragment> fragmentList=new ArrayList<>();
        final ArrayList<String> fragmentTitleList=new ArrayList<>();
        ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }
    }

}