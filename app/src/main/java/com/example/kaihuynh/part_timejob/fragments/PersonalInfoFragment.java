package com.example.kaihuynh.part_timejob.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.example.kaihuynh.part_timejob.PickLocationActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.RegisterPersonalInfoActivity;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoFragment extends Fragment {

    private final int REQUEST_CODE = 123;

    private Button mNextButton;
    private CustomViewPager mViewPager;
    private TextInputLayout inputLayoutDob, inputLayoutGender, inputLayoutAddress, inputLayoutPhone, inputLayoutEducation;
    private TextInputEditText inputDob, inputGender, inputAddress, inputPhoneNumber, inputEducation;
    private AlertDialog genderDialog;

    @SuppressLint("StaticFieldLeak")
    private static PersonalInfoFragment sInstance = null;


    public PersonalInfoFragment() {
        // Required empty public constructor
        sInstance = this;

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        addComponents(view);
        addEvents();

        return view;
    }

    private void addComponents(View view) {
        inputDob = view.findViewById(R.id.input_dob);
        inputGender = view.findViewById(R.id.input_gender);
        inputAddress = view.findViewById(R.id.input_address);
        inputEducation = view.findViewById(R.id.input_education);
        inputPhoneNumber = view.findViewById(R.id.input_phone_number);
        inputLayoutDob = view.findViewById(R.id.input_dob_layout);
        inputLayoutGender = view.findViewById(R.id.input_gender_layout);
        inputLayoutAddress = view.findViewById(R.id.input_address_layout);
        inputLayoutEducation = view.findViewById(R.id.input_education_layout);
        inputLayoutPhone = view.findViewById(R.id.input_phone_number_layout);
        mNextButton = view.findViewById(R.id.btn_next_personal);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);

    }

    private void addEvents() {
        inputDobEvents();
        inputGenderEvents();
        inputLocationEvents();
        inputEducationEvents();
        inputPhoneNumberEvents();
        nextButtonEvents();
    }

    private void inputPhoneNumberEvents() {
        inputPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(inputPhoneNumber.getText().toString().equals("")){
                        inputPhoneNumber.setText(String.valueOf("+84 "));
                    }
                }else {
                    if(inputPhoneNumber.getText().toString().equals("+84 ")){
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
                if (s.equals("+84")){
                    inputPhoneNumber.setText(String.valueOf("+84 "));
                    inputPhoneNumber.setSelection(inputPhoneNumber.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void nextButtonEvents() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()){
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                }
            }
        });
    }

    private void inputEducationEvents() {
        inputEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEducationPicker();
            }
        });

        inputEducation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){

                    showEducationPicker();
                }
            }
        });
    }

    private void showEducationPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.education_picker_dialog, null);
        builder.setTitle(getContext().getResources().getString(R.string.pick_education_dialog_title));
        builder.setView(view);

        final NumberPicker numberPicker = view.findViewById(R.id.np_education);
        final String[] strings = getResources().getStringArray(R.array.education);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(strings.length-1);
        numberPicker.setDisplayedValues(strings);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });

        builder.setPositiveButton(getContext().getResources().getString(R.string.positive_save_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputEducation.setText(strings[numberPicker.getValue()]);
            }
        });

        builder.setNegativeButton(getContext().getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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
                startActivityForResult(new Intent(getContext(), PickLocationActivity.class), REQUEST_CODE);
            }
        });

        inputAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    startActivityForResult(new Intent(getContext(), PickLocationActivity.class), REQUEST_CODE);
                }
            }
        });
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
                if (b){
                    showGenderDialog();
                }
            }
        });
    }

    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setSingleChoiceItems(getContext().getResources().getStringArray(R.array.gender), -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i){
                    case 0:
                        inputGender.setText(String.valueOf("Nam"));
                        break;
                    case 1:
                        inputGender.setText(String.valueOf("Nữ"));
                        break;
                    case 2:
                        inputGender.setText(String.valueOf("Khác"));
                        break;
                }
                genderDialog.dismiss();
            }
        });
        builder.setTitle(getContext().getResources().getString(R.string.pick_education_dialog_title));

        genderDialog = builder.create();
        genderDialog.setCanceledOnTouchOutside(false);
        genderDialog.show();
    }

    private void inputDobEvents() {
        inputDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        inputDob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    showDatePickerDialog();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                inputDob.setText(String.valueOf(day + "-" + (month+1) + "-" + year));
            }
        };

        int dayOfMonth, month, year;

        String dateText = inputDob.getText().toString();
        if (dateText.equals("")|| dateText == null){
            Calendar calendar = Calendar.getInstance();
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);
        }else{
            String[] dateSplit = dateText.split("-");
            dayOfMonth = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), callback, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private boolean isValid(){
        if (TextUtils.isEmpty(inputDob.getText())){
            inputLayoutDob.setErrorEnabled(true);
            inputLayoutDob.setError(getContext().getResources().getString(R.string.empty_edittext_error));
            return false;
        }else {
            inputLayoutDob.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(inputGender.getText())){
            inputLayoutGender.setErrorEnabled(true);
            inputLayoutGender.setError(getContext().getResources().getString(R.string.empty_edittext_error));
            return false;
        }else {
            inputLayoutGender.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(inputAddress.getText())){
            inputLayoutAddress.setErrorEnabled(true);
            inputLayoutAddress.setError(getContext().getResources().getString(R.string.empty_edittext_error));
            return false;
        }else {
            inputLayoutAddress.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(inputPhoneNumber.getText())){
            inputLayoutPhone.setErrorEnabled(true);
            inputLayoutPhone.setError(getContext().getResources().getString(R.string.empty_edittext_error));
            return false;
        }else {
            inputLayoutPhone.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(inputEducation.getText())){
            inputLayoutEducation.setErrorEnabled(true);
            inputLayoutEducation.setError(getContext().getResources().getString(R.string.empty_edittext_error));
            return false;
        }else {
            inputLayoutEducation.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
                inputAddress.setText(data.getStringExtra("location"));
        }else{
            inputAddress.setSelection(0);
        }

    }

    public static PersonalInfoFragment getInstance(){
        if (sInstance == null) {
            sInstance = new PersonalInfoFragment();
        }
        return sInstance;
    }

    public Map<String, String> getPersonalInfo(){
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("dob", inputDob.getText().toString());
        hashMap.put("gender", inputGender.getText().toString());
        hashMap.put("address", inputAddress.getText().toString());
        hashMap.put("education", inputEducation.getText().toString());
        hashMap.put("phone", inputPhoneNumber.getText().toString());

        return hashMap;
    }
}
