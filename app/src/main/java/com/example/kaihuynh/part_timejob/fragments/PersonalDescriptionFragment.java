package com.example.kaihuynh.part_timejob.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.LoginMethodActivity;
import com.example.kaihuynh.part_timejob.ProfileActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.RecruitingActivity;
import com.example.kaihuynh.part_timejob.RegisterPersonalInfoActivity;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.ApplyJob;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.Notification;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalDescriptionFragment extends Fragment {

    private Button mPreviousButton, mDoneButton;
    private CustomViewPager mViewPager;
    private EditText mDescription;
    private ProgressDialog mProgress;
    @SuppressLint("StaticFieldLeak")
    private static PersonalDescriptionFragment sInstance = null;

    public PersonalDescriptionFragment() {
        // Required empty public constructor
        sInstance = this;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_description, container, false);

        getWidgets(view);
        initialize();
        setWidgetListeners();

        return view;
    }

    private void getWidgets(View view) {
        mDescription = view.findViewById(R.id.et_personal_description);
        mPreviousButton = view.findViewById(R.id.btn_previous_description);
        mDoneButton = view.findViewById(R.id.btn_done_description);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
    }

    private void initialize() {
        mProgress = new ProgressDialog(getContext());
        mProgress.setMessage("Đang xử lý thông tin...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

    }

    private void setWidgetListeners() {
        previousButtonEvents();
        doneButtonEvents();
    }

    private boolean isConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void doneButtonEvents() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect()){
                    String dob = PersonalInfoFragment.getInstance().getPersonalInfo().get("dob");
                    String[] dateSplit = dob.split("-");
                    int dayOfMonth = Integer.parseInt(dateSplit[0]);
                    int month = Integer.parseInt(dateSplit[1]) - 1;
                    int year = Integer.parseInt(dateSplit[2]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                    calendar.set(year, month, dayOfMonth);
                    User user = UserManager.getInstance().getUser();
                    user.setAddress(PersonalInfoFragment.getInstance().getPersonalInfo().get("address"));
                    user.setGender(PersonalInfoFragment.getInstance().getPersonalInfo().get("gender"));
                    user.setDayOfBirth(calendar.getTime());
                    user.setEducation(PersonalInfoFragment.getInstance().getPersonalInfo().get("education"));
                    user.setPhoneNumber(PersonalInfoFragment.getInstance().getPersonalInfo().get("phone"));
                    user.setForeignLanguages(ForeignLanguageFragment.getInstance().getLanguages());
                    user.setSkills(SkillFragment.getInstance().getSkills());
                    user.setImageURL("");
                    user.setPersonalDescription(mDescription.getText().toString());
                    user.setFavouriteJobList(new ArrayList<Job>());
                    user.setRecruitmentList(new ArrayList<Job>());
                    user.setAppliedJobList(new ArrayList<ApplyJob>());
                    user.setNotificationList(new ArrayList<Notification>());
                    UserManager.getInstance().updateUser(user);
                    showAlertDialog();
                }else {
                    Toast.makeText(getContext(), "Lỗi kết nối! Vui lòng kiểm tra đường truyền.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.register_notification_dialog, null);
        TextView t1 = view.findViewById(R.id.tv_success);
        TextView t2 = view.findViewById(R.id.tv_content_success);
        t1.setText(String.valueOf("Đăng ký thông tin thành công"));
        t2.setVisibility(View.GONE);
        builder.setView(view);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (getActivity().getIntent().getStringExtra("activity")) {
                    case "RegisterAccountInfoActivity":
                        startActivity(new Intent(getContext(), HomePageActivity.class));
                        LoginMethodActivity.getInstance().finish();
                        getActivity().finish();
                        break;
                    case "HomePageActivity":
                        startActivity(new Intent(getContext(), ProfileActivity.class));
                        getActivity().finish();
                        break;
                    case "RecruitingActivity":
                        startActivity(new Intent(getContext(), RecruitingActivity.class));
                        getActivity().finish();
                        break;
                    case "JobDescriptionActivity":
                        getActivity().finish();
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
    }

    public static PersonalDescriptionFragment getInstance(){
        if (sInstance == null) {
            sInstance = new PersonalDescriptionFragment();
        }
        return sInstance;
    }

}
