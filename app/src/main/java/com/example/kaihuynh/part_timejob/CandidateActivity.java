package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.models.Candidate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CandidateActivity extends AppCompatActivity {

    private TextView mName, mEmail, mDate, mDescription;
    private TextInputEditText mDOB, mGender, mEducation, mAddress, mLanguage, mSkill, mPhone;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate);

        addComponents();
        initialize();

    }

    private void initialize() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        Candidate candidate = (Candidate) intent.getSerializableExtra("candidate");
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
