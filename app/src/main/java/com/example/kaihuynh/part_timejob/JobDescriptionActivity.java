package com.example.kaihuynh.part_timejob;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class JobDescriptionActivity extends AppCompatActivity {

    private TabHost mTabHost;
    private TextView mTitle, mDate, mStatus, mStatusSymbol, mSalary, mLocation, mBenefit, mRequirement, mDescription;
    private TextView mRecruiterName, mRecruiterEmail, mRecruiterPhone, mRecruiterAddress;
    private Button mApplyButton, mSaveButton;
    private String description="";
    private Job job;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWidgets();
        setWidgets();
        initial();
        setWidgetListeners();
    }

    private void getWidgets() {
        mTabHost = findViewById(R.id.tabHost);
        mTitle = findViewById(R.id.tv_job_title);
        mDate = findViewById(R.id.tv_job_date);
        mStatus = findViewById(R.id.tv_job_status);
        mStatusSymbol = findViewById(R.id.tv_job_status_symbol);
        mSalary = findViewById(R.id.tv_job_salary);
        mLocation = findViewById(R.id.tv_job_location);
        mBenefit = findViewById(R.id.tv_job_benefit);
        mRequirement = findViewById(R.id.tv_job_requirement);
        mDescription = findViewById(R.id.tv_job_description);
        mRecruiterName = findViewById(R.id.tv_recruiter_name);
        mRecruiterAddress = findViewById(R.id.tv_recruiter_address);
        mRecruiterPhone = findViewById(R.id.tv_recruiter_phone);
        mRecruiterEmail = findViewById(R.id.tv_recruiter_email);
        mApplyButton = findViewById(R.id.btn_apply);
        mSaveButton = findViewById(R.id.btn_save);
    }

    private void setWidgets() {
        mTabHost.setup();
        TabHost.TabSpec mJobDetailTabSpec = mTabHost.newTabSpec("t1");
        mJobDetailTabSpec.setIndicator("CÔNG VIỆC");
        mJobDetailTabSpec.setContent(R.id.tab1);
        mTabHost.addTab(mJobDetailTabSpec);

        TabHost.TabSpec mRecruiterDetailTabSpec = mTabHost.newTabSpec("t2");
        mRecruiterDetailTabSpec.setIndicator("NGƯỜI TUYỂN");
        mRecruiterDetailTabSpec.setContent(R.id.tab2);
        mTabHost.addTab(mRecruiterDetailTabSpec);


    }

    private void initial() {
        Intent intent = getIntent();
        job = (Job) intent.getSerializableExtra("job");
        mTitle.setText(job.getName());
        mStatus.setText(job.getStatus());
        if(job.getStatus().equals("Đang tuyển")){
            mStatusSymbol.setTextColor(ContextCompat.getColor(this, R.color.green));
        }else {
            mStatusSymbol.setTextColor(ContextCompat.getColor(this, R.color.red));
        }
        mSalary.setText(job.getSalary());
        mLocation.setText(job.getLocation());
        mBenefit.setText(job.getBenefits());
        mDescription.setText(job.getDescription());
        mRequirement.setText(job.getRequirement());
        mRecruiterName.setText(job.getRecruiter().getFullName());
        mRecruiterEmail.setText(job.getRecruiter().getEmail());
        mRecruiterPhone.setText(job.getRecruiter().getPhoneNumber());
        mRecruiterAddress.setText(job.getRecruiter().getAddress());

    }

    private void setWidgetListeners(){
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User u = UserManager.getInstance().getUser();
                ArrayList<Job> applyList = new ArrayList<>();
                if (u.getAppliedJobList()!=null){
                    applyList.addAll(u.getAppliedJobList());
                }
                if (applyList.size()>0){
                    for (Job j : applyList){
                        if(j.getId().equals(job.getId())){
                            Toast.makeText(JobDescriptionActivity.this, "Công việc này đã ứng tuyển trước đó.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                showDescriptionDialog();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User u = UserManager.getInstance().getUser();
                ArrayList<Job> arrayList = new ArrayList<>();
                if (u.getFavouriteJobList()!=null){
                    arrayList.addAll(u.getFavouriteJobList());
                }
                for (Job j : arrayList){
                    if(j.getId().equals(job.getId())){
                        Toast.makeText(JobDescriptionActivity.this, "Công việc này đã được thêm vào danh sách.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                arrayList.add(job);
                u.setFavouriteJobList(arrayList);
                UserManager.getInstance().updateUser(u);
                Toast.makeText(JobDescriptionActivity.this, "Lưu vào danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater().from(this);
        View view = inflater.inflate(R.layout.description_candidate, null);
        final EditText editText = view.findViewById(R.id.et_candidate_description);
        if(description.equals("")){
            editText.setText(UserManager.getInstance().getUser().getPersonalDescription());
        }else {
            editText.setText(description);
        }
        editText.setSelection(editText.getText().length());
        builder.setView(view);
        builder.setPositiveButton("Ứng tuyển", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                User u = UserManager.getInstance().getUser();
                ArrayList<Job> applyList = new ArrayList<>();
                applyList.add(job);
                if (u.getAppliedJobList()!=null){
                    applyList.addAll(u.getAppliedJobList());
                }
                u.setAppliedJobList(applyList);
                UserManager.getInstance().updateUser(u);

                ArrayList<Candidate> candidateList = new ArrayList<>();
                Candidate candidate = new Candidate();
                User user = new User();
                user.setId(u.getId());
                user.setFullName(u.getFullName());
                user.setGender(u.getGender());
                user.setAddress(u.getAddress());
                user.setForeignLanguages(u.getForeignLanguages());
                user.setDayOfBirth(u.getDayOfBirth());
                user.setEmail(u.getEmail());
                user.setPhoneNumber(u.getPhoneNumber());
                user.setSkills(u.getSkills());
                user.setEducation(u.getEducation());

                candidate.setUser(user);
                candidate.setJobExperience(editText.getText().toString());
                candidate.setStatus("Đang chờ");
                candidate.setDate(new Date().getTime());
                candidateList.add(candidate);
                if (job.getCandidateList()!=null){
                    candidateList.addAll(job.getCandidateList());
                }
                job.setCandidateList(candidateList);
                JobManager.getInstance().updateJob(job);
                Toast.makeText(JobDescriptionActivity.this, "Ứng tuyển thành công.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                description = editText.getText().toString();
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar current = Calendar.getInstance();
        Date date = new Date(job.getTimestamp());
        Calendar postingDate = Calendar.getInstance();
        postingDate.setTime(date);
        mDate.setText(getTime(current, postingDate));
    }
}
