package com.example.omen.onboard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.omen.HomeMainActivity;
import com.example.omen.R;

public class EntryActivity extends AppCompatActivity {


    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("login",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("logged",false)){
                startActivity(new Intent(EntryActivity.this, HomeMainActivity.class));
                finish();
        }

        setContentView(R.layout.activity_entry);

        //onboard layout to iterate over
        //add more if want
        layouts=new int[] {
                R.layout.entry_slide_1,
                R.layout.entry_slide_2,
        };

        ViewPagerAdapter adapter= new ViewPagerAdapter(layouts);
        viewPager = (ViewPager2) findViewById(R.id.view_pager);

        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        // adding bottom dots
        addBottomDots(0);

        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);




        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLogScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchLogScreen();
                }
            }
        });

        //set to change
        viewPager.setAdapter(adapter);
        //viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                addBottomDots(position);

                // changing the next button text 'NEXT' / 'GOT IT'
                if (position == layouts.length - 1) {
                    // last page. make button text to GOT IT
                    btnNext.setText(getString(R.string.start));
                    //remove skip
                    btnSkip.setVisibility(View.GONE);
                }
                //if not last then no need to perform this it will still do that
                //but when we come back from last to previous ones we need to make skip visible again
                else {
                    // still pages are left
                    btnNext.setText(getString(R.string.next));
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //when done with this launch login page
    private void launchLogScreen() {
        //prefManager.setFirstTimeLaunch(false);
        //Miscellaneous.showSettingsDialog();
        startActivity(new Intent(EntryActivity.this, HomeMainActivity.class));
        finish();
    }

    //count pages
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorsActive = getResources().getIntArray(R.array.array_dot_active)[0];
        int colorsInactive = getResources().getIntArray(R.array.array_dot_inactive)[0];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }
        //change dot color of active one if there are more than 1 layouts are there
        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive);
        }
    }
}
