package com.example.kaihuynh.part_timejob;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.example.kaihuynh.part_timejob.others.CustomViewPager;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoFragment extends Fragment {

    private final int REQUEST_CODE = 123;

    private Button mNextButton;
    private CustomViewPager mViewPager;
    private TextInputEditText inputDob, inputGender, inputAddress, inputPhoneNumber, inputEducation;
    private TextInputLayout inputDobLayout, inputGenderLayout, inputAddressLayout, inputPhoneNumberLayout, inputEducationLayout;
    private AlertDialog genderDialog;


    public PersonalInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        inputDobLayout = view.findViewById(R.id.input_dob_layout);
        inputGenderLayout = view.findViewById(R.id.input_gender_layout);
        inputAddressLayout = view.findViewById(R.id.input_address_layout);
        inputEducationLayout = view.findViewById(R.id.input_education_layout);
        inputPhoneNumberLayout = view.findViewById(R.id.input_phone_number_layout);
        mNextButton = view.findViewById(R.id.btn_next_personal);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);

    }

    private void addEvents() {
        inputDobEvents();
        inputGenderEvents();
        inputLocationEvents();
        inputEducationEvents();
        nextButtonEvents();
    }

    private void nextButtonEvents() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
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
        builder.setTitle("Chọn...");
        builder.setView(view);

        final NumberPicker numberPicker = view.findViewById(R.id.np_education);
        final String[] strings = {"Trung học", "Trung cấp", "Cao đẳng", "Cử nhân", "Thạc sĩ", "Tiến sĩ", "Khác"};
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(strings.length-1);
        numberPicker.setDisplayedValues(strings);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {

            }
        });

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputEducation.setText(strings[numberPicker.getValue()]);
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
                inputDob.setText(day + "/" + (month+1) + "/" + year);
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
            String[] dateSplit = dateText.split("/");
            dayOfMonth = Integer.parseInt(dateSplit[0]);
            month = Integer.parseInt(dateSplit[1]) - 1;
            year = Integer.parseInt(dateSplit[2]);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), callback, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
                inputAddress.setText(data.getStringExtra("location"));
        }else{
            inputAddress.setSelection(0);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
