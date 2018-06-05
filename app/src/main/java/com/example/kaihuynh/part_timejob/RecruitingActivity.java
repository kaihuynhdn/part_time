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
import android.widget.ImageButton;
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

public class RecruitingActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 111;
    private float dpi;
    private AlertDialog genderDialog;
    private TextInputEditText inputSalary, inputLocation, inputSkill, inputLanguage, inputGender;
    private EditText mJobDescriptionDetail, mJobBenefits, mJobRequirement;
    private ImageButton mEditTitleButton;
    private TextView mJobTitle;
    private Button mRecruitButton;
    private ArrayList<ForeignLanguage> languages;
    private ArrayList<Skill> skills;
    private ForeignLanguageAdapter languageAdapter;
    private SkillAdapter skillAdapter;
    private int edited = 0;

    private CollectionReference mJobReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mJobBenefits = findViewById(R.id.et_benefits);
        mJobDescriptionDetail = findViewById(R.id.et_description_detail);
        mJobRequirement = findViewById(R.id.et_job_requirement);
        mEditTitleButton = findViewById(R.id.btn_edit_job_title);
        mJobTitle = findViewById(R.id.tv_job_title);
        mRecruitButton = findViewById(R.id.btn_recruit);
    }

    private void initialize() {
        mJobReference = FirebaseFirestore.getInstance().collection("jobs");

        dpi = RecruitingActivity.this.getResources().getDisplayMetrics().density;
        initLanguageArray();
        initSkillArray();
    }

    private void addEvents() {
        inputLocationEvents();
        inputGenderEvents();
        inputLanguageEvents();
        inputSkillEvents();
        editTitleEvents();
        recruitButtonEvents();

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
            ConnectivityManager cm = (ConnectivityManager) RecruitingActivity.this
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
                        long timeStamp = new Date().getTime();
                        User user = new User(u.getId(), u.getFullName(), u.getGender(), u.getDayOfBirth(), u.getAddress(), u.getPhoneNumber(), u.getSkills(), u.getEducation(), u.getForeignLanguages(), u.getPersonalDescription(), u.getEmail());
                        Job job = new Job();
                        job.setId(id);
                        job.setRecruiter(user);
                        job.setName(mJobTitle.getText().toString());
                        job.setTimestamp(timeStamp);
                        job.setBenefits(mJobBenefits.getText().toString());
                        job.setDescription(mJobDescriptionDetail.getText().toString());
                        job.setSalary(inputSalary.getText().toString());
                        job.setLocation(inputLocation.getText().toString());
                        job.setRequirement(requirementToString());
                        job.setStatus(Job.RECRUITING);
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
                        job1.setStatus(Job.RECRUITING);
                        job1.setCandidateList(new ArrayList<Candidate>());
                        list.add(job1);
                        list.addAll(u.getRecruitmentList());
                        u.setRecruitmentList(list);
                        UserManager.getInstance().updateUser(u);
                        showSuccessDialog();
                    }
                }else {
                    Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
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
        builder.setTitle(getResources().getString(R.string.notify));
        builder.setMessage(getResources().getString(R.string.recruiting_success));
        builder.setIcon(ContextCompat.getDrawable(this, R.drawable.checked));
        builder.setPositiveButton(getResources().getString(R.string.next_button_dialog), new DialogInterface.OnClickListener() {
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
        if (edited == 0 && mJobTitle.getText().toString().equals(getResources().getString(R.string.job_title))) {
            Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.empty_edittext_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (inputSalary.getText().toString().equals("")) {
            Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.empty_edittext_error), Toast.LENGTH_SHORT).show();
            inputSalary.requestFocus();
            return false;
        }
        if (inputLocation.getText().toString().equals("")) {
            Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.empty_edittext_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mJobBenefits.getText().toString().equals("")) {
            Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.empty_edittext_error), Toast.LENGTH_SHORT).show();
            mJobBenefits.requestFocus();
            return false;
        }

        if (mJobDescriptionDetail.getText().toString().equals("")) {
            Toast.makeText(RecruitingActivity.this, getResources().getString(R.string.empty_edittext_error), Toast.LENGTH_SHORT).show();
            mJobDescriptionDetail.requestFocus();
            return false;
        }

        if (mJobRequirement.getText().toString().equals("") && inputLanguage.getText().toString().equals("")
                && inputSkill.getText().toString().equals("") && inputGender.getText().toString().equals("")) {
            Toast.makeText(RecruitingActivity.this, "Cần nhập đủ thông tin.", Toast.LENGTH_SHORT).show();
            mJobRequirement.requestFocus();
            return false;
        }


        return true;
    }

    private void editTitleEvents() {
        mEditTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(RecruitingActivity.this);
                input.setText(mJobTitle.getText().toString().equals(getResources().getString(R.string.job_title))? "" : mJobTitle.getText().toString());
                input.setSelection(input.getText().length());
                AlertDialog dialog = (new AlertDialog.Builder(RecruitingActivity.this))
                        .setTitle(getResources().getString(R.string.job_title))
                        .setPositiveButton(getResources().getString(R.string.next_button_dialog), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!input.getText().toString().equals("")) {
                                    mJobTitle.setText(input.getText());
                                }else {
                                    mJobTitle.setText(getResources().getString(R.string.job_title));
                                }
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.setView(input, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
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

        ListView listView = new ListView(RecruitingActivity.this);
        skillAdapter = new SkillAdapter(RecruitingActivity.this, R.layout.skill_item, skills);
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
        builder.setTitle(getResources().getString(R.string.pick_education_dialog_title));
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitingActivity.this);
        builder.setTitle(getResources().getString(R.string.add_new_skill_title));
        final EditText editText = new EditText(RecruitingActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.add_new_language_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("") && editText.getText().toString() != null && !editText.getText().toString().equals("Không")) {
                    skills.add(skills.size() - 1, new Skill(editText.getText().toString(), true));
                    skillAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
        ListView listView = new ListView(RecruitingActivity.this);
        languageAdapter = new ForeignLanguageAdapter(RecruitingActivity.this, R.layout.foreign_language_item, languages);
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
        builder.setTitle(getResources().getString(R.string.pick_education_dialog_title));
        builder.setView(listView);
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitingActivity.this);
        builder.setTitle(getResources().getString(R.string.add_new_language_title));
        final EditText editText = new EditText(RecruitingActivity.this);
        builder.setPositiveButton(getResources().getString(R.string.add_new_language_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("") && editText.getText().toString() != null && !editText.getText().toString().equals("Không")) {
                    languages.add(languages.size() - 1, new ForeignLanguage(editText.getText().toString(), true));
                    languageAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
        builder.setTitle(getResources().getString(R.string.pick_education_dialog_title));

        genderDialog = builder.create();
        genderDialog.show();
    }

    private void inputLocationEvents() {
        inputLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(RecruitingActivity.this, PickLocationActivity.class), REQUEST_CODE);
            }
        });

        inputLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    startActivityForResult(new Intent(RecruitingActivity.this, PickLocationActivity.class), REQUEST_CODE);
                }
            }
        });
    }

    private void showBackDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.notify));
        builder.setMessage(getResources().getString(R.string.exit_recruiting_notify));
        builder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
