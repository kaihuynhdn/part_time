package com.example.kaihuynh.part_timejob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.adapters.SkillAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.ForeignLanguage;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.Skill;
import com.example.kaihuynh.part_timejob.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class UpdateJobActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 111;
    private float dpi;
    private AlertDialog genderDialog;
    private TextInputEditText inputSalary, inputLocation, inputSkill, inputLanguage, inputGender, inputStatus;
    private EditText mJobDescriptionDetail, mJobBenefits, mJobRequirement;
    private TextView mJobTitle;
    private Button mRecruitButton;
    private ArrayList<ForeignLanguage> languages;
    private ArrayList<Skill> skills;
    private ForeignLanguageAdapter languageAdapter;
    private SkillAdapter skillAdapter;
    private int edited = 0;
    private boolean isUpdateJob = false;
    private Job intentJob;

    private CollectionReference mJobReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cập nhật công việc");

        addComponents();
        initialize();
        addEvents();
    }

    private void addComponents() {
        inputSalary = findViewById(R.id.input_salary_job);
        inputGender = findViewById(R.id.input_gender_job);
        inputSkill = findViewById(R.id.input_skill_job);
        inputLanguage = findViewById(R.id.input_language_job);
        inputLocation = findViewById(R.id.input_location_job);
        inputStatus = findViewById(R.id.input_status_job);
        mJobBenefits = findViewById(R.id.et_benefits);
        mJobDescriptionDetail = findViewById(R.id.et_description_detail);
        mJobRequirement = findViewById(R.id.et_job_requirement);
        mJobTitle = findViewById(R.id.tv_job_title);
        mRecruitButton = findViewById(R.id.btn_recruit);
    }

    private void initialize() {
        mJobReference = FirebaseFirestore.getInstance().collection("jobs");

        dpi = UpdateJobActivity.this.getResources().getDisplayMetrics().density;
        initSkillArray();
        initLanguageArray();

        Intent intent = getIntent();
        intentJob = (Job) intent.getSerializableExtra("job");
        if (intentJob!=null){
            isUpdateJob = true;
            mJobTitle.setText(intentJob.getName());
            inputSalary.setText(intentJob.getSalary());
            inputLocation.setText(intentJob.getLocation());
            mJobBenefits.setText(intentJob.getBenefits());
            mJobDescriptionDetail.setText(intentJob.getDescription());
            if (intentJob.getStatus().equals(Job.RECRUITING)){
                inputStatus.setTextColor(getResources().getColor(R.color.green));
            }else {
                inputStatus.setTextColor(getResources().getColor(R.color.red));
            }
            inputStatus.setText(intentJob.getStatus());

            String s = intentJob.getRequirement();
            if (s.contains("\n\n")){
                mJobRequirement.setText(s.substring(0, s.indexOf("\n\n")));
            }

            if (s.contains("- Kĩ năng:")){
                String string;
                String[] splits = s.split("- Kĩ năng:");
                if (splits[0].contains("- Kĩ năng:")){
                    string = splits[0];
                }else {
                    string = splits[1];
                }
                inputSkill.setText(string.substring(2, string.indexOf(".\n")));
            }
            if (s.contains("- Ngoại ngữ:")){
                String string;
                String[] splits = s.split("- Ngoại ngữ:");
                if (splits[0].contains("- Ngoại ngữ:")){
                    string = splits[0];
                }else {
                    string = splits[1];
                }
                inputLanguage.setText(string.substring(2, string.indexOf(".\n")));
            }
            if (s.contains("- Giới tính:")){
                String string;
                String[] splits = s.split("- Giới tính:");
                if (splits[0].contains("- Giới tính:")){
                    string = splits[0];
                }else {
                    string = splits[1];
                }
                inputGender.setText(string.substring(2, string.length()-1));
            }

        }
    }

    private void addEvents() {
        inputLocationEvents();
        inputGenderEvents();
        inputLanguageEvents();
        inputSkillEvents();
        recruitButtonEvents();
        inputStatusEvents();

        mJobTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edited = 1;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private boolean isConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) UpdateJobActivity.this
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

    private void initSkillArray() {
        String[] skillArray = getResources().getStringArray(R.array.skill_array);
        skills = new ArrayList<>();
        for (String s : skillArray) {
            skills.add(new Skill(s, false));
        }
    }

    private void initLanguageArray() {
        String[] languageArray = getResources().getStringArray(R.array.foreign_language);
        languages = new ArrayList<>();
        for (String s : languageArray) {
            languages.add(new ForeignLanguage(s, false));
        }
    }

    private void recruitButtonEvents() {
        mRecruitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect()){
                    if (isValid()) {
                        String id = mJobReference.document().getId();
                        User u = UserManager.getInstance().getUser();
                        User user = new User(u.getId(), u.getFullName(), u.getGender(), u.getDayOfBirth(), u.getAddress(), u.getPhoneNumber(), u.getSkills(), u.getEducation(), u.getForeignLanguages(), u.getPersonalDescription(), u.getEmail());
                        long timeStamp = new Date().getTime();
                        Job job = new Job();
                        if (intentJob!=null){
                            id = intentJob.getId();
                            timeStamp = intentJob.getTimestamp();
                        }
                        job.setId(id);
                        job.setRecruiter(user);
                        job.setName(mJobTitle.getText().toString());
                        job.setTimestamp(timeStamp);
                        job.setBenefits(mJobBenefits.getText().toString());
                        job.setDescription(mJobDescriptionDetail.getText().toString());
                        job.setSalary(inputSalary.getText().toString());
                        job.setLocation(inputLocation.getText().toString());
                        job.setRequirement(requirementToString());
                        job.setStatus(inputStatus.getText().toString());
                        job.setCandidateList(new ArrayList<Candidate>());
                        JobManager.getInstance().updateJob(job);

                        ArrayList<Job> list = new ArrayList<>();
                        Job job1 = new Job();
                        job1.setId(id);
                        job1.setName(mJobTitle.getText().toString());
                        job1.setTimestamp(timeStamp);
                        job1.setBenefits(mJobBenefits.getText().toString());
                        job1.setDescription(mJobDescriptionDetail.getText().toString());
                        job1.setSalary(inputSalary.getText().toString());
                        job1.setLocation(inputLocation.getText().toString());
                        job1.setRequirement(requirementToString());
                        job1.setStatus(inputStatus.getText().toString());
                        job1.setCandidateList(new ArrayList<Candidate>());
                        list.addAll(u.getRecruitmentList());
                        for (int i = 0; i< list.size(); i++){
                            if (list.get(i).getId().equals(job1.getId())){
                                list.set(i, job1);
                                break;
                            }
                        }
                        u.setRecruitmentList(list);
                        UserManager.getInstance().updateUser(u);
                        ListRecruitmentActivity.getInstance().refresh();
                        showSuccessDialog();
                    }
                }else {
                    Toast.makeText(UpdateJobActivity.this, "Lỗi kết nối! Vui lòng kiểm tra đường truyền.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String requirementToString() {
        String s =  "";

        if (!mJobRequirement.getText().toString().equals("")){
            s +=  mJobRequirement.getText().toString() + "\n\n";
        }
        if(!inputSkill.getText().toString().equals("")){
            s += "- Kĩ năng:" + "\n\t" + inputSkill.getText().toString() + ".\n";
        }
        if(!inputLanguage.getText().toString().equals("")){
            s += "- Ngoại ngữ:" + "\n\t" + inputLanguage.getText().toString() + ".\n";
        }
        if(!inputGender.getText().toString().equals("")){
            s += "- Giới tính:" + "\n\t" + inputGender.getText().toString() + ".";
        }
        return s;

    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setIcon(ContextCompat.getDrawable(this, R.drawable.checked));
        builder.setMessage(intentJob!=null ? "Cập nhật công việc thành công !" : "Đăng tuyển thành công!");
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog alertDialog =builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private boolean isValid() {
        if (edited == 0 && mJobTitle.getText().toString().equals("Tên công việc")) {
            Toast.makeText(UpdateJobActivity.this, "Cần thêm tiều đề cho công việc.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputSalary.getText().toString().equals("")) {
            Toast.makeText(UpdateJobActivity.this, "Cần nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            inputSalary.requestFocus();
            return false;
        }
        if (inputLocation.getText().toString().equals("")) {
            Toast.makeText(UpdateJobActivity.this, "Cần nhập thông tin nơi làm việc.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mJobBenefits.getText().toString().equals("")) {
            Toast.makeText(UpdateJobActivity.this, "Cần nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            mJobBenefits.requestFocus();
            return false;
        }

        if (mJobDescriptionDetail.getText().toString().equals("")) {
            Toast.makeText(UpdateJobActivity.this, "Cần nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            mJobDescriptionDetail.requestFocus();
            return false;
        }

        if (mJobRequirement.getText().toString().equals("") && inputLanguage.getText().toString().equals("") && inputSkill.getText().toString().equals("")) {
            Toast.makeText(UpdateJobActivity.this, "Cần nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            mJobRequirement.requestFocus();
            return false;
        }


        return true;
    }

    private void inputStatusEvents() {
        inputStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStatusDialog();
            }
        });

        inputStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showStatusDialog();
                }
            }
        });
    }

    private void showStatusDialog() {
        final String []status = getResources().getStringArray(R.array.status_job);
        new AlertDialog.Builder(this)
                .setTitle("Trạng thái")
                .setItems(status, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputStatus.setText(status[which]);
                        if (inputStatus.getText().toString().equals(Job.RECRUITING)){
                            inputStatus.setTextColor(getResources().getColor(R.color.green));
                        }else if (inputStatus.getText().toString().equals(Job.UNAVAILABLE)){
                            inputStatus.setTextColor(getResources().getColor(R.color.red));
                        }
                    }
                })
                .create()
                .show();

    }

    private void inputSkillEvents() {
        inputSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSkillDialog(inputSkill.getText().toString());
            }
        });

        inputSkill.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showSkillDialog(inputSkill.getText().toString());
                }
            }
        });
    }

    private void showSkillDialog(String s) {
        initSkillArray();
        String[] splits = s.split("\n");
        for (String split : splits) {
            int j;
            for (j = 0; j < skills.size(); j++) {
                if (skills.get(j).getName().equals(split)) {
                    skills.get(j).setChecked(true);
                    break;
                }

            }
            if (j == skills.size() && !split.equals("")) {
                skills.add(skills.size() - 1, new Skill(split, false));
            }
        }

        ListView listView = new ListView(UpdateJobActivity.this);
        skillAdapter = new SkillAdapter(UpdateJobActivity.this, R.layout.skill_item, skills);
        listView.setAdapter(skillAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (skills.get(i).isChecked()) {
                    skills.get(i).setChecked(false);
                } else {
                    if (i == 0) {
                        for (Skill s : skills) {
                            s.setChecked(false);
                        }
                    } else {
                        skills.get(0).setChecked(false);
                    }
                    if (i != skills.size() - 1) {
                        skills.get(i).setChecked(true);
                    } else {
                        showAddSkillDialog();
                    }
                }
                skillAdapter.notifyDataSetChanged();
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn:");
        builder.setPositiveButton("Xong", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder s = new StringBuilder();
                for (Skill f : skills) {
                    if (f.isChecked() && !f.getName().equals("Không")) {
                        s.append(f.getName()).append("\n");
                    }
                }

                inputSkill.setText(s.toString().equals("") ? "" : s.substring(0, s.length() - 1));
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(listView, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.show();
    }

    private void showAddSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateJobActivity.this);
        builder.setTitle("Thêm kĩ năng:");
        final EditText editText = new EditText(UpdateJobActivity.this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("") && editText.getText().toString() != null && !editText.getText().toString().equals("Không")) {
                    skills.add(skills.size() - 1, new Skill(editText.getText().toString(), true));
                    skillAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void inputLanguageEvents() {
        inputLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showLanguageDialog(inputLanguage.getText().toString());
                }
            }
        });
        inputLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog(inputLanguage.getText().toString());
            }
        });
    }

    private void showLanguageDialog(String s) {
        initLanguageArray();
        String[] splits = s.split("\n");
        for (String split : splits) {
            int j;
            for (j = 0; j < languages.size(); j++) {
                if (languages.get(j).getName().equals(split)) {
                    languages.get(j).setChecked(true);
                    break;
                }
            }
            if (j == languages.size() && !split.equals("")) {
                languages.add(languages.size() - 1, new ForeignLanguage(split, true));
            }
        }

        ListView listView = new ListView(UpdateJobActivity.this);
        languageAdapter = new ForeignLanguageAdapter(UpdateJobActivity.this, R.layout.foreign_language_item, languages);
        listView.setAdapter(languageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (languages.get(i).isChecked()) {
                    languages.get(i).setChecked(false);
                } else {
                    if (i == 0) {
                        for (ForeignLanguage f : languages) {
                            f.setChecked(false);
                        }
                    } else {
                        languages.get(0).setChecked(false);
                    }
                    if (i != languages.size() - 1) {
                        languages.get(i).setChecked(true);
                    } else {
                        showAddLanguageDialog();
                    }
                }
                languageAdapter.notifyDataSetChanged();
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn:");
        builder.setView(listView);
        builder.setPositiveButton("Xong", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StringBuilder s = new StringBuilder();
                for (ForeignLanguage f : languages) {
                    if (f.isChecked() && !f.getName().equals("Không")) {
                        s.append(f.getName()).append("\n");
                    }
                }

                inputLanguage.setText(s.toString().equals("") ? "" : s.substring(0, s.length() - 1));
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(listView, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.show();
    }

    private void showAddLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateJobActivity.this);
        builder.setTitle("Thêm ngoại ngữ:");
        final EditText editText = new EditText(UpdateJobActivity.this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("") && editText.getText().toString() != null && !editText.getText().toString().equals("Không")) {
                    languages.add(languages.size() - 1, new ForeignLanguage(editText.getText().toString(), true));
                    languageAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void inputGenderEvents() {
        inputGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialog();
            }
        });
        inputGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showGenderDialog();
                }
            }
        });
    }

    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"Nam", "Nữ", "Nam/Nữ"}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        inputGender.setText(String.valueOf("Nam"));
                        break;
                    case 1:
                        inputGender.setText(String.valueOf("Nữ"));
                        break;
                    case 2:
                        inputGender.setText(String.valueOf("Nam/Nữ"));
                        break;
                }
                genderDialog.dismiss();
            }
        });
        builder.setTitle("Chọn...");

        genderDialog = builder.create();
        genderDialog.show();
    }

    private void inputLocationEvents() {
        inputLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(UpdateJobActivity.this, PickLocationActivity.class), REQUEST_CODE);
            }
        });

        inputLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    startActivityForResult(new Intent(UpdateJobActivity.this, PickLocationActivity.class), REQUEST_CODE);
                }
            }
        });
    }

    private void showBackDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Thoát khỏi đăng tuyển ?");
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            inputLocation.setText(data.getStringExtra("location"));
        } else {
            inputLocation.setSelection(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showBackDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showBackDialog();
    }
}
