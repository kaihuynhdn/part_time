package com.example.kaihuynh.part_timejob;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.adapters.SkillAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.ForeignLanguage;
import com.example.kaihuynh.part_timejob.others.Skill;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ProfileActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 1000;
    private float dpi;
    private TextInputEditText inputDOB, inputGender, inputAddress, inputEducation, inputLanguage, inputSkill;
    private Toolbar toolbar;
    private Button mEditPersonalDescription;
    private ImageButton mEditInfoProfile;
    private TextView mDescriptionTextView, mName, mPhoneNumber, mEmail;
    private User user;

    private String genderChoice;
    private ArrayList<ForeignLanguage> languages;
    private ArrayList<Skill> skills;
    private ForeignLanguageAdapter languageAdapter;
    private SkillAdapter skillAdapter;
    private ArrayList<String> languageList, skillList;

    //Firebase instance variables
    private CollectionReference mUserReference;
    private DatabaseReference connectedRef;
    private ValueEventListener valueEventListener;

    private boolean isConnect = false;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        addComponents();
        initialize();
        addEvents();

    }

    private void addEvents() {
        inputDobEvents();
        inputGenderEvents();
        inputLocationEvents();
        inputEducationEvents();
        inputLanguageEvents();
        inputSkillEvents();

        editDescriptionEvents();
        editInfoProfileEvents();
    }

    private void initialize() {
        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        checkConnect();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.back));

        user = UserManager.getInstance().getUser();
        mName.setText(user.getFullName());
        mEmail.setText(user.getEmail());
        mPhoneNumber.setText(user.getPhoneNumber());
        inputDOB.setText(new SimpleDateFormat("dd-MM-yyyy").format(user.getDayOfBirth()));
        inputGender.setText(user.getGender());
        inputAddress.setText(user.getAddress());
        inputEducation.setText(user.getEducation());
        inputLanguage.setText(user.getForeignLanguages());
        inputSkill.setText(user.getSkills());
        mDescriptionTextView.setText(user.getPersonalDescription());


        dpi = ProfileActivity.this.getResources().getDisplayMetrics().density;

        languageList = new ArrayList<>();
        skillList = new ArrayList<>();
        languages = new ArrayList<>();
        skills = new ArrayList<>();

        String[] languageArray = getResources().getStringArray(R.array.foreign_language);
        for (String s : languageArray) {
            languageList.add(s);
        }

        String[] skillArray = getResources().getStringArray(R.array.skill_array);
        for (String s : skillArray) {
            skillList.add(s);
        }

        genderChoice = "";
        mUserReference = FirebaseFirestore.getInstance().collection("users");
        mUserReference.document(user.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    User u = task.getResult().toObject(User.class);
                    mName.setText(u.getFullName());
                    mEmail.setText(u.getEmail());
                    mPhoneNumber.setText(u.getPhoneNumber());
                    inputDOB.setText(new SimpleDateFormat("dd-MM-yyyy").format(u.getDayOfBirth()));
                    inputGender.setText(u.getGender());
                    inputAddress.setText(u.getAddress());
                    inputEducation.setText(u.getEducation());
                    inputLanguage.setText(u.getForeignLanguages());
                    inputSkill.setText(u.getSkills());
                    mDescriptionTextView.setText(u.getPersonalDescription());
                    UserManager.getInstance().load(u);
                }
            }
        });
    }

    private void addComponents() {
        toolbar = findViewById(R.id.toolbar_profile);
        inputDOB = findViewById(R.id.input_dob_profile);
        inputGender = findViewById(R.id.input_gender_profile);
        inputAddress = findViewById(R.id.input_address_profile);
        inputEducation = findViewById(R.id.input_education_profile);
        inputLanguage = findViewById(R.id.input_language_profile);
        inputSkill = findViewById(R.id.input_skill_profile);
        mEditPersonalDescription = findViewById(R.id.btn_edit_personal_description);
        mDescriptionTextView = findViewById(R.id.tv_personal_description);
        mName = findViewById(R.id.tv_name_profile);
        mEmail = findViewById(R.id.tv_email_profile);
        mPhoneNumber = findViewById(R.id.tv_phone_number_profile);
        mEditInfoProfile = findViewById(R.id.btn_edit_info_profile);

    }

    private void notification(String s) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void editInfoProfileEvents() {
        mEditInfoProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditInfoDialog(mName.getText().toString(), mPhoneNumber.getText().toString());
            }
        });
    }

    private void showEditInfoDialog(String name, String phoneNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa thông tin:");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.edit_profile_dialog, null);
        final TextInputEditText inputName = view.findViewById(R.id.input_name_profile);
        final TextInputEditText inputPhoneNumber = view.findViewById(R.id.input_phone_profile);
        inputName.setText(name);
        inputPhoneNumber.setText(phoneNumber);

        inputPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    if (inputPhoneNumber.getText().toString().equals("")) {
                        inputPhoneNumber.setText("+84 ");
                    }
                } else {
                    if (inputPhoneNumber.getText().toString().equals("+84 ")) {
                        inputPhoneNumber.setText("");
                    }
                }
            }
        });

        inputPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = inputPhoneNumber.getText().toString();
                if (s.equals("+84")) {
                    inputPhoneNumber.setText("+84 ");
                    inputPhoneNumber.setSelection(inputPhoneNumber.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setFullName(inputName.getText().toString());
                    u.setPhoneNumber(inputPhoneNumber.getText().toString());
                    UserManager.getInstance().updateUser(u);
                    mName.requestFocus();
                    mName.setText(inputName.getText().toString());
                    mPhoneNumber.setText(inputPhoneNumber.getText().toString());
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
                }
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    private void editDescriptionEvents() {
        mEditPersonalDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDescription();
            }
        });
    }

    private void showEditDescription() {
        final EditText editText = new EditText(this);
        editText.setBackground(ContextCompat.getDrawable(this, R.drawable.input_template));
        editText.setMinHeight(300);
        editText.setTextSize(16);
        editText.setHint("Nhập thông tin...");
        editText.setPadding(15, 10, 10, 15);
        editText.setGravity(Gravity.TOP | Gravity.LEFT);
        editText.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f, getResources().getDisplayMetrics()), 1.0f);
        editText.setText(mDescriptionTextView.getText());
        editText.setSelection(mDescriptionTextView.getText().toString().length());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mô tả bản thân:");
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setPersonalDescription(editText.getText().toString());
                    UserManager.getInstance().updateUser(u);
                    mDescriptionTextView.setText(editText.getText().toString());
                    mDescriptionTextView.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int) (15 * dpi), (int) (20 * dpi), (int) (20 * dpi), (int) (15 * dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
        ListView listView = new ListView(this);

        String[] skillItem = s.split("\n");
        for (int i = 0; i < skillItem.length; i++) {
            boolean same = false;
            for (int j = 0; j < skillList.size(); j++) {
                if (skillItem[i].toString().equals(skillList.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same && skillItem[i].toString() != "") {
                skillList.add(skillList.size() - 1, skillItem[i].toString());
            }
        }

        skills.clear();
        for (int i = 0; i < skillList.size(); i++) {
            boolean same = false;
            for (int j = 0; j < skillItem.length; j++) {
                if (skillItem[j].trim().equals(skillList.get(i).trim())) {
                    skills.add(new Skill(skillList.get(i), true));
                    same = true;
                    break;
                }
            }
            if (!same) {
                skills.add(new Skill(skillList.get(i), false));
            }
        }


        skillAdapter = new SkillAdapter(this, R.layout.skill_item, skills);
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
                String s = "";
                for (Skill f : skills) {
                    if (f.isChecked()) {
                        s += f.getName() + "\n";
                    }
                }

                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setSkills(s == "" ? "" : s.substring(0, s.length() - 1));
                    inputSkill.setText(u.getSkills());
                    UserManager.getInstance().updateUser(u);
                    inputSkill.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
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
        alertDialog.setView(listView, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.show();
    }

    private void showAddSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm kĩ năng:");
        final EditText editText = new EditText(this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().toString() != "" && editText.getText().toString() != null) {
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
        ListView listView = new ListView(this);

        String[] languageItem = s.split("\n");
        for (int i = 0; i < languageItem.length; i++) {
            boolean same = false;
            for (int j = 0; j < languageList.size(); j++) {
                if (languageItem[i].equals(languageList.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same && languageItem[i] != "") {
                languageList.add(languageList.size() - 1, languageItem[i]);
            }
        }

        languages.clear();
        for (int i = 0; i < languageList.size(); i++) {
            boolean same = false;
            for (int j = 0; j < languageItem.length; j++) {
                if (languageItem[j].trim().equals(languageList.get(i).trim())) {
                    languages.add(new ForeignLanguage(languageList.get(i), true));
                    break;
                }
            }

            if (!same) {
                languages.add(new ForeignLanguage(languageList.get(i), false));
            }
        }

        languageAdapter = new ForeignLanguageAdapter(this, R.layout.foreign_language_item, languages);
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
                String s = "";
                for (ForeignLanguage f : languages) {
                    if (f.isChecked()) {
                        s += f.getName() + "\n";
                    }
                }
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setForeignLanguages(s == "" ? "" : s.substring(0, s.length() - 1));
                    inputLanguage.setText(u.getForeignLanguages());
                    UserManager.getInstance().updateUser(u);
                    inputLanguage.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
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
        alertDialog.setView(listView, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.show();
    }

    private void showAddLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm ngoại ngữ:");
        final EditText editText = new EditText(this);
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().toString() != "" && editText.getText().toString() != null) {
                    languages.add(languages.size() - 1, new ForeignLanguage(editText.getText().toString(), true));
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
        alertDialog.setView(editText, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void inputEducationEvents() {
        inputEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEducationPicker(inputEducation.getText().toString());
            }
        });

        inputEducation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showEducationPicker(inputEducation.getText().toString());
                }
            }
        });
    }

    private void showEducationPicker(String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.education_picker_dialog, null);
        builder.setTitle("Chọn:");
        builder.setView(view);

        final NumberPicker numberPicker = view.findViewById(R.id.np_education);
        final String[] strings = getResources().getStringArray(R.array.education);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(strings.length - 1);
        numberPicker.setDisplayedValues(strings);
        numberPicker.setWrapSelectorWheel(false);

        if (string == "") {
            numberPicker.setValue(0);
        } else {
            for (int i = 0; i < strings.length; i++) {
                if (string == strings[i] || string.equals(strings[i])) {
                    numberPicker.setValue(i);
                    break;
                }
            }
        }

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setEducation(strings[numberPicker.getValue()]);
                    inputEducation.setText(u.getEducation());
                    UserManager.getInstance().updateUser(u);
                    inputEducation.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
                }


            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void inputLocationEvents() {
        inputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ProfileActivity.this, PickLocationActivity.class), REQUEST_CODE);
            }
        });

        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    startActivityForResult(new Intent(ProfileActivity.this, PickLocationActivity.class), REQUEST_CODE);
                }
            }
        });
    }

    private void inputGenderEvents() {
        inputGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGenderDialog(inputGender.getText().toString());
            }
        });

        inputGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showGenderDialog(inputGender.getText().toString());
                }
            }
        });
    }

    private void showGenderDialog(String s) {
        int checkedItem;
        final String[] strings = getResources().getStringArray(R.array.gender);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (s) {
            case "Nam":
                checkedItem = 0;
                break;
            case "Nữ":
                checkedItem = 1;
                break;
            default:
                checkedItem = 2;
        }
        builder.setSingleChoiceItems(strings, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                genderChoice = strings[i];
            }
        });
        builder.setTitle("Chọn...");
        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setGender(genderChoice);
                    UserManager.getInstance().updateUser(u);
                    inputGender.setText(u.getGender());
                    inputGender.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
                }

            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog genderDialog = builder.create();
        genderDialog.setCanceledOnTouchOutside(false);
        genderDialog.show();
    }

    private void inputDobEvents() {
        inputDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        inputDOB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    showDatePickerDialog();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                calendar.set(year, month, day);
                if (isConnect) {
                    User u = UserManager.getInstance().getUser();
                    u.setDayOfBirth(calendar.getTime());
                    UserManager.getInstance().updateUser(u);
                    inputDOB.setText(new SimpleDateFormat("dd-MM-yyyy").format(u.getDayOfBirth()));
                    inputDOB.requestFocus();
                } else {
                    notification("Lỗi! Vui lòng kiểm tra lại kết nối");
                }

            }
        };

        int dayOfMonth, month, year;

        String dateText = inputDOB.getText().toString();
        if (dateText.equals("") || dateText == null) {
            Calendar calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        } else {
            String[] dateSplit = dateText.split("-");
            dayOfMonth = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, callback, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void checkConnect() {
        valueEventListener = connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    isConnect = true;
                } else {
                    isConnect = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (isConnect) {
                User u = UserManager.getInstance().getUser();
                u.setAddress(data.getStringExtra("location"));
                UserManager.getInstance().updateUser(u);
                inputAddress.setText(u.getAddress());
                inputAddress.requestFocus();
            } else {
                notification("Lỗi! Vui lòng kiểm tra lại kết nối");
            }

        } else {
            inputAddress.setSelection(0);
        }
        super.onActivityResult(requestCode, resultCode, data);

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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (valueEventListener!=null){
            connectedRef.removeEventListener(valueEventListener);
        }
    }
}
