package com.example.kaihuynh.part_timejob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.adapters.SkillAdapter;
import com.example.kaihuynh.part_timejob.others.ForeignLanguage;
import com.example.kaihuynh.part_timejob.others.Skill;

import java.util.ArrayList;

public class RecruitingActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 111;
    private float dpi;
    private AlertDialog genderDialog;
    private TextInputLayout inputSalaryLayout, inputLocationLayout, inputSkillLayout, inputLanguageLayout, inputGenderLayout;
    private TextInputEditText inputSalary, inputLocation, inputSkill, inputLanguage, inputGender;
    private EditText mJobDescriptionDetail, mJobBenefits,mJobRequirement;
    private ImageButton mEditTitleButton;
    private TextView mJobTitle;
    private ArrayList<ForeignLanguage> languages;
    private ArrayList<Skill> skills;
    private ForeignLanguageAdapter languageAdapter;
    private SkillAdapter skillAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addComponents();
        init();
        addEvents();
    }

    private void init() {
        dpi = RecruitingActivity.this.getResources().getDisplayMetrics().density;
        String[] languageArray = getResources().getStringArray(R.array.foreign_language);
        languages = new ArrayList<>();
        for(String s : languageArray){
            languages.add(new ForeignLanguage(s, false));
        }

        String[] skillArray = getResources().getStringArray(R.array.skill_array);
        skills = new ArrayList<>();
        for(String s : skillArray){
            skills.add(new Skill(s, false));
        }
    }

    private void addEvents() {
        inputLocationEvents();
        inputGenderEvents();
        inputLanguageEvents();
        inputSkillEvents();
        editTitleEvents();
    }

    private void editTitleEvents() {
        mEditTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(RecruitingActivity.this);
                AlertDialog dialog = (new AlertDialog.Builder(RecruitingActivity.this))
                        .setTitle("Tiêu đề công việc:")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(input.getText().toString()!=""){
                                    mJobTitle.setText(input.getText());
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create();
                dialog.setView(input, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    private void inputSkillEvents() {
        inputSkill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSkillDialog();
            }
        });

        inputSkill.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showSkillDialog();
                }
            }
        });
    }

    private void showSkillDialog() {
        ListView listView = new ListView(RecruitingActivity.this);
        skillAdapter = new SkillAdapter(RecruitingActivity.this, R.layout.skill_item, skills);
        listView.setAdapter(skillAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(skills.get(i).isChecked()){
                    skills.get(i).setChecked(false);
                }else {
                    if (i==0){
                        for(Skill s : skills){
                            s.setChecked(false);
                        }
                    } else {
                        skills.get(0).setChecked(false);
                    }
                    if(i!= skills.size()-1){
                        skills.get(i).setChecked(true);
                    }else{
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
                String s = "";
                for(Skill f : skills){
                    if(f.isChecked()){
                        s+=f.getName()+"\n";
                    }
                }

                inputSkill.setText(s=="" ? "" : s.substring(0, s.length()-1));
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(listView, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
        alertDialog.show();
    }

    private void showAddSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitingActivity.this);
        builder.setTitle("Thêm kĩ năng:");
        final EditText editText = new EditText(RecruitingActivity.this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().toString()!="" && editText.getText().toString()!= null){
                    skills.add(skills.size()-1, new Skill(editText.getText().toString(), true));
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
        alertDialog.setView(editText, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void inputLanguageEvents() {
        inputLanguage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showLanguageDialog();
                }
            }
        });
        inputLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
            }
        });
    }

    private void showLanguageDialog() {
        ListView listView = new ListView(RecruitingActivity.this);
        languageAdapter = new ForeignLanguageAdapter(RecruitingActivity.this, R.layout.foreign_language_item, languages);
        listView.setAdapter(languageAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(languages.get(i).isChecked()){
                    languages.get(i).setChecked(false);
                }else {
                    if (i==0){
                        for(ForeignLanguage f : languages){
                            f.setChecked(false);
                        }
                    } else {
                        languages.get(0).setChecked(false);
                    }
                    if(i!= languages.size()-1){
                        languages.get(i).setChecked(true);
                    }else{
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
                String s = "";
                for(ForeignLanguage f : languages){
                    if(f.isChecked()){
                        s+=f.getName()+"\n";
                    }
                }
                inputLanguage.setText(s=="" ? "" : s.substring(0, s.length()-1));
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(listView, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
        alertDialog.show();
    }

    private void showAddLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RecruitingActivity.this);
        builder.setTitle("Thêm ngoại ngữ:");
        final EditText editText = new EditText(RecruitingActivity.this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().toString()!="" && editText.getText().toString()!= null){
                    languages.add(languages.size()-1, new ForeignLanguage(editText.getText().toString(), true));
                    languageAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
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
                if(b){
                    showGenderDialog();
                }
            }
        });
    }

    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"Nam", "Nữ", "Khác"}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        inputGender.setText("Nam");
                        break;
                    case 1:
                        inputGender.setText("Nữ");
                        break;
                    case 2:
                        inputGender.setText("Khác");
                        break;
                }
                genderDialog.dismiss();
            }
        });
        builder.setTitle("Chọn...");

        genderDialog = builder.create();
        genderDialog.setCanceledOnTouchOutside(false);
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
                if(b){
                    startActivityForResult(new Intent(RecruitingActivity.this, PickLocationActivity.class), REQUEST_CODE);
                }
            }
        });
    }

    private void addComponents() {
        inputSalary = findViewById(R.id.input_salary_job);
        inputGender = findViewById(R.id.input_gender_job);
        inputSkill = findViewById(R.id.input_skill_job);
        inputLanguage = findViewById(R.id.input_language_job);
        inputLocation = findViewById(R.id.input_location_job);
        inputSalaryLayout = findViewById(R.id.input_salary_job_layout);
        inputGenderLayout = findViewById(R.id.input_gender_job_layout);
        inputSkillLayout = findViewById(R.id.input_skill_job_layout);
        inputLanguageLayout = findViewById(R.id.input_language_job_layout);
        inputLocationLayout = findViewById(R.id.input_location_job_layout);
        mJobBenefits = findViewById(R.id.et_benefits);
        mJobDescriptionDetail = findViewById(R.id.et_description_detail);
        mJobRequirement = findViewById(R.id.et_job_requirement);
        mEditTitleButton = findViewById(R.id.btn_edit_job_title);
        mJobTitle = findViewById(R.id.tv_job_title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            inputLocation.setText(data.getStringExtra("location"));
        }else{
            inputLocation.setSelection(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
