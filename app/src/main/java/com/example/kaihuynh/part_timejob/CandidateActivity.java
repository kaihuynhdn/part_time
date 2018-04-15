package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.ApplyJob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CandidateActivity extends AppCompatActivity {

    private TextView mName, mEmail, mDate, mDescription;
    private TextInputEditText mDOB, mGender, mEducation, mAddress, mLanguage, mSkill, mPhone;
    private Toolbar toolbar;
    private Button mIgnoreButton, mAcceptButton;
    private Candidate candidate;
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        addComponents();
        initialize();
        setWidgetListeners();
    }

    private void initialize() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        candidate = (Candidate) intent.getSerializableExtra("candidate");
        job = (Job) getIntent().getSerializableExtra("job");
        mName.setText(candidate.getUser().getFullName());
        mEmail.setText(candidate.getUser().getEmail());
        mEducation.setText(candidate.getUser().getEducation());
        mPhone.setText(candidate.getUser().getPhoneNumber());
        mGender.setText(candidate.getUser().getGender());
        mAddress.setText(candidate.getUser().getAddress());
        mLanguage.setText(candidate.getUser().getForeignLanguages());
        mSkill.setText(candidate.getUser().getSkills());
        mDescription.setText(candidate.getJobExperience());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(candidate.getDate()));
        mDate.setText(getTime(Calendar.getInstance(), calendar));
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(candidate.getUser().getDayOfBirth());
        mDOB.setText(Calendar.getInstance().get(Calendar.YEAR) - calendar1.get(Calendar.YEAR) + " tuổi");

        setActionButton(candidate.getStatus());
    }

    private void addComponents() {
        toolbar = findViewById(R.id.toolbar_candidate);
        mName = findViewById(R.id.tv_name_candidate);
        mEmail = findViewById(R.id.tv_email_candidate);
        mPhone = findViewById(R.id.tv_phone_number_candidate);
        mDOB = findViewById(R.id.input_age_candidate);
        mGender = findViewById(R.id.input_gender_candidate);
        mEducation = findViewById(R.id.input_education_candidate);
        mAddress = findViewById(R.id.input_address_candidate);
        mLanguage = findViewById(R.id.input_language_candidate);
        mSkill = findViewById(R.id.input_skill_candidate);
        mDescription = findViewById(R.id.tv_description_candidate);
        mDate = findViewById(R.id.tv_date_candidate);
        mIgnoreButton = findViewById(R.id.btn_ignore);
        mAcceptButton = findViewById(R.id.btn_accept);
    }

    private void setWidgetListeners() {
        mIgnoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToCandidate(ApplyJob.UNEMPLOYED_STATUS);
                setActionButton(ApplyJob.UNEMPLOYED_STATUS);
            }
        });

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionToCandidate(ApplyJob.EMPLOYED_STATUS);
                setActionButton(ApplyJob.EMPLOYED_STATUS);
            }
        });
    }

    private void setActionButton(String action){
        if (action.equals(ApplyJob.UNEMPLOYED_STATUS)){
            mAcceptButton.setVisibility(View.GONE);
            mIgnoreButton.setText("Bị từ chối !");
            mIgnoreButton.setClickable(false);
        }else if (action.equals(ApplyJob.EMPLOYED_STATUS)){
            mIgnoreButton.setVisibility(View.GONE);
            mAcceptButton.setText("Được tuyển chọn !");
            mAcceptButton.setClickable(false);
        }
    }

    private void actionToCandidate(final String action){
        ArrayList<Candidate> candidateList = new ArrayList<>();
        for (Candidate c : job.getCandidateList()){
            if (c.getUser().getId().equals(candidate.getUser().getId())){
                c.setStatus(action);
            }
            candidateList.add(c);
        }
        job.setCandidateList(candidateList);
        JobManager.getInstance().updateJob(job);

        UserManager.getInstance().loadUserByID(candidate.getUser().getId());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                User u = UserManager.getInstance().getUserById();
                ArrayList<ApplyJob> applyJobs = new ArrayList<>();
                for(ApplyJob a : u.getAppliedJobList()){
                    if (a.getJob().getId().equals(job.getId())){
                        a.setStatus(action);
                    }
                    applyJobs.add(a);
                }
                u.setAppliedJobList(applyJobs);
                UserManager.getInstance().updateJobStatus(u);
            }
        }, 1500);

    }

    private String getTime(Calendar current, Calendar postingDate){
        String s = "";
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if(minus<2){
            if (minus == 1){
                s = "Hôm qua lúc " + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            }else if(minus == 0){
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1>0){
                    s = minus1 + " giờ trước";
                }else {
                    if(current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE)==0){
                        s = "1 phút trước";
                    }else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " phút trước";
                    }
                }
            }
        }else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
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
}
