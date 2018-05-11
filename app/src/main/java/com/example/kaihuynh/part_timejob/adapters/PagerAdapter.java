package com.example.kaihuynh.part_timejob.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.kaihuynh.part_timejob.fragments.ForeignLanguageFragment;
import com.example.kaihuynh.part_timejob.fragments.PersonalDescriptionFragment;
import com.example.kaihuynh.part_timejob.fragments.PersonalInfoFragment;
import com.example.kaihuynh.part_timejob.fragments.SkillFragment;

/**
 * Created by Kai on 2018-02-07.
 */

public class PagerAdapter extends FragmentPagerAdapter{

    private int mNoOfTabs;

    public PagerAdapter(FragmentManager fm, int numberOfTabs){
        super(fm);
        this.mNoOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new PersonalInfoFragment();
            case 1:
                return new ForeignLanguageFragment();
            case 2:
                return new SkillFragment();
            case 3:
                return new PersonalDescriptionFragment();
            default:
                return new Fragment();
        }
    }


    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
