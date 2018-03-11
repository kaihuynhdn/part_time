package com.example.kaihuynh.part_timejob;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kaihuynh.part_timejob.others.CustomViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalDescriptionFragment extends Fragment {

    private Button mPreviousButton, mDoneButton;
    private CustomViewPager mViewPager;

    public PersonalDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_description, container, false);

        addComponents(view);
        addEvents();

        return view;
    }

    private void addEvents() {
        previousButtonEvents();
        doneButtonEvents();
    }

    private void doneButtonEvents() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HomePageActivity.class));
                getActivity().finish();
                LoginMethodActivity.getInstance().finish();
            }
        });
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
    }

    private void addComponents(View view) {
        mPreviousButton = view.findViewById(R.id.btn_previous_description);
        mDoneButton = view.findViewById(R.id.btn_done_description);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
    }

}
