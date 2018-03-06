package com.example.kaihuynh.part_timejob;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.kaihuynh.part_timejob.adapters.PagerAdapter;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.shuhart.stepview.StepView;

public class RegisterPersonalInfoActivity extends AppCompatActivity {

    private final int NUMBER_TABS = 4;
    private StepView mRegisterStepView;
    private CustomViewPager mRegisterViewPager;
    private PagerAdapter mPagerAdapter;

    public static RegisterPersonalInfoActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_personal_info);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        addComponents();
        addEvents();
    }

    private void addEvents() {
        viewPageChangeEvent();
    }

    private void viewPageChangeEvent() {
        mRegisterViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mRegisterStepView.go(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void addComponents() {
        mRegisterStepView = findViewById(R.id.stepView_register);

        mRegisterViewPager = findViewById(R.id.viewPage_register);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), NUMBER_TABS);
        mRegisterViewPager.setAdapter(mPagerAdapter);
        mRegisterViewPager.setPagingEnabled(false);

        sInstance=this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static RegisterPersonalInfoActivity getInstance(){
        if (sInstance == null) {
            sInstance = new RegisterPersonalInfoActivity();
        }
        return sInstance;
    }
}
