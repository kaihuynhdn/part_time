package com.example.kaihuynh.part_timejob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kaihuynh.part_timejob.fragments.JobAppliedFragment;
import com.example.kaihuynh.part_timejob.fragments.JobLikedFragment;
import com.example.kaihuynh.part_timejob.fragments.JobListFragment;
import com.example.kaihuynh.part_timejob.fragments.NotificationFragment;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {
    private int mNoOfTabs;

    public HomeViewPagerAdapter(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.mNoOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new JobListFragment();
            case 1:
                return new JobLikedFragment();
            case 2:
                return new JobAppliedFragment();
            case 3:
                return new NotificationFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return mNoOfTabs;
    }

}
