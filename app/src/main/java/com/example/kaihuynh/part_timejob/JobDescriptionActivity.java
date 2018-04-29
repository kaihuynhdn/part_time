package com.example.kaihuynh.part_timejob;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.ApplyJob;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.MyResponse;
import com.example.kaihuynh.part_timejob.models.Notification;
import com.example.kaihuynh.part_timejob.models.NotificationFCM;
import com.example.kaihuynh.part_timejob.models.Sender;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.example.kaihuynh.part_timejob.remote.APIService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobDescriptionActivity extends AppCompatActivity {

    private TabHost mTabHost;
    private TextView mTitle, mDate, mStatus, mStatusSymbol, mSalary, mLocation, mBenefit, mRequirement, mDescription;
    private TextView mRecruiterName, mRecruiterEmail, mRecruiterPhone, mRecruiterAddress;
    private Button mApplyButton, mSaveButton, mManageButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout relativeLayout;
    private String description = "";
    private Job job;
    private APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_description);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Mô tả công việc");

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
        mManageButton = findViewById(R.id.btn_manage);
        swipeRefreshLayout = findViewById(R.id.sw_job_description);
        relativeLayout = findViewById(R.id.relative_layout);
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
        mService = Common.getClientFCM();

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.lightBlue_700));
        Intent intent = getIntent();
        job = (Job) intent.getSerializableExtra("job");
        JobManager.getInstance().loadJobById(job.getId());
        swipeRefreshLayout.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                while (!JobManager.isLoadJobById){

                }
                if (JobManager.getInstance().getJobById()!=null){
                    job = JobManager.getInstance().getJobById();
                }
                mTitle.setText(job.getName());
                mStatus.setText(job.getStatus());
                if (job.getStatus().equals(Job.RECRUITING)) {
                    mStatusSymbol.setTextColor(ContextCompat.getColor(JobDescriptionActivity.this, R.color.green));
                } else {
                    mStatusSymbol.setTextColor(ContextCompat.getColor(JobDescriptionActivity.this, R.color.red));
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

                User u = UserManager.getInstance().getUser();


                if (job.getRecruiter().getId().equals(u.getId())) {
                    mSaveButton.setVisibility(View.GONE);
                    mApplyButton.setVisibility(View.GONE);
                    mManageButton.setVisibility(View.VISIBLE);
                }

                ArrayList<ApplyJob> applyList = new ArrayList<>();
                if (u.getAppliedJobList() != null) {
                    applyList.addAll(u.getAppliedJobList());
                }
                if (applyList.size() > 0) {
                    for (ApplyJob j : applyList) {
                        if (j.getJob().getId().equals(job.getId())) {
                            mApplyButton.setText("Đã ứng tuyển !");
                            mApplyButton.setClickable(false);
                            break;
                        }
                    }
                }

                ArrayList<Job> arrayList = new ArrayList<>();
                if (u.getFavouriteJobList() != null) {
                    arrayList.addAll(u.getFavouriteJobList());
                }
                for (Job j : arrayList) {
                    if (j.getId().equals(job.getId())) {
                        mSaveButton.setText("Hủy Lưu");
                        break;
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                relativeLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
            }
        }, 800);
    }

    private void setWidgetListeners() {
        mApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect()){
                    if (!UserManager.getInstance().isUpdated()) {
                        showDialog();
                    } else if (!mApplyButton.getText().toString().equals("Đã ứng tuyển !")){
                        showDescriptionDialog();
                    }
                }else {
                    Toast.makeText(JobDescriptionActivity.this, "Lỗi kết nối! Vui lòng kiểm tra đường truyền.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect()){
                    if (mSaveButton.getText().toString().equals("Lưu")){
                        User u = UserManager.getInstance().getUser();
                        ArrayList<Job> arrayList = new ArrayList<>();
                        Job j = job;
                        j.setRecruiter(null);
                        j.setCandidateList(new ArrayList<Candidate>());
                        arrayList.add(j);
                        if (u.getFavouriteJobList() != null) {
                            arrayList.addAll(u.getFavouriteJobList());
                        }
                        u.setFavouriteJobList(arrayList);
                        UserManager.getInstance().updateUser(u);
                        Toast.makeText(JobDescriptionActivity.this, "Lưu vào danh sách yêu thích!", Toast.LENGTH_SHORT).show();
                        mSaveButton.setText("Hủy Lưu");
                    }else if (mSaveButton.getText().toString().equals("Hủy Lưu")){
                        UserManager.getInstance().removeFavouriteJob(job.getId());
                        mSaveButton.setText("Lưu");
                    }
                }else {
                    Toast.makeText(JobDescriptionActivity.this, "Lỗi kết nối! Vui lòng kiểm tra đường truyền.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        mManageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(JobDescriptionActivity.this, ListRecruitmentActivity.class));
                finish();
            }
        });
    }

    private boolean isConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) JobDescriptionActivity.this
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

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn cần hoàn thiện hồ sơ cá nhân để ứng tuyển");
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(JobDescriptionActivity.this, RegisterPersonalInfoActivity.class);
                intent.putExtra("activity", "JobDescriptionActivity");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDescriptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater().from(this);
        View view = inflater.inflate(R.layout.description_candidate, null);
        final EditText editText = view.findViewById(R.id.et_candidate_description);
        if (description.equals("")) {
            editText.setText(UserManager.getInstance().getUser().getPersonalDescription());
        } else {
            editText.setText(description);
        }
        editText.setSelection(editText.getText().length());
        builder.setView(view);
        builder.setPositiveButton("Ứng tuyển", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mApplyButton.setText("Đã ứng tuyển !");
                User u = UserManager.getInstance().getUser();
                ArrayList<ApplyJob> applyList = new ArrayList<>();
                final Job addJob = new Job(job.getId(), job.getName(), job.getSalary(), job.getLocation(), job.getTimestamp(), job.getDescription(), job.getRequirement(), job.getBenefits(),new ArrayList<Candidate>(), job.getStatus());
                addJob.setRecruiter(null);
                applyList.add(new ApplyJob(addJob, ApplyJob.VIEWING_STATUS));
                if (u.getAppliedJobList() != null) {
                    applyList.addAll(u.getAppliedJobList());
                }
                u.setAppliedJobList(applyList);
                UserManager.getInstance().updateUser(u);

                ArrayList<Candidate> candidateList = new ArrayList<>();
                Candidate candidate = new Candidate();
                final User user = new User();
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
                user.setImageURL(u.getImageURL());
                user.setToken(u.getToken());

                // update candidate list of job
                candidate.setUser(user);
                candidate.setJobExperience(editText.getText().toString());
                candidate.setStatus(ApplyJob.VIEWING_STATUS);
                candidate.setDate(new Date().getTime());
                candidateList.add(candidate);
                if (job.getCandidateList() != null) {
                    candidateList.addAll(job.getCandidateList());
                }
                job.setCandidateList(candidateList);
                JobManager.getInstance().updateJob(job);
                Toast.makeText(JobDescriptionActivity.this, "Ứng tuyển thành công.", Toast.LENGTH_SHORT).show();

                // Notification
                final NotificationFCM notificationFCM = new NotificationFCM(user.getFullName() + " đã ứng tuyển vào công việc " + job.getName() + ".","Thông báo");
                UserManager.getInstance().loadUserByID(job.getRecruiter().getId());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        User recruiter = UserManager.getInstance().getUserById();
                        Notification notification = new Notification(Notification.TO_RECRUITER, Notification.STATUS_NOT_SEEN, new Date().getTime(), notificationFCM.getBody());
                        notification.setJob(addJob);
                        ArrayList<Notification> notifications = new ArrayList<>();
                        notifications.add(notification);
                        if (recruiter.getNotificationList() != null && !recruiter.getNotificationList().isEmpty()){
                            notifications.addAll(recruiter.getNotificationList());
                        }
                        recruiter.setNotificationList(notifications);

                        notification(notificationFCM , recruiter.getToken());
                        UserManager.getInstance().updateSpecificUser(recruiter);


                    }
                }, 1500);

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

    private void notification(NotificationFCM notificationFCM, String token) {
        Sender sender = new Sender(token, notificationFCM);
        mService.sendNotification(sender)
                .enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if(response.isSuccessful()){

                        }else {

                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {

                    }
                });
    }

    private String getTime(Calendar current, Calendar postingDate) {
        String s = "";
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if (minus < 2) {
            if (minus == 1) {
                s = "Hôm qua lúc " + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            } else if (minus == 0) {
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1 > 0) {
                    s = minus1 + " giờ trước";
                } else {
                    if (current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) == 0) {
                        s = "1 phút trước";
                    } else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " phút trước";
                    }
                }
            }
        } else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
