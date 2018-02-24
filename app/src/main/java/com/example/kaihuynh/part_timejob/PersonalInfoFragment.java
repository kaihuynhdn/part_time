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
import android.widget.DatePicker;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfoFragment extends Fragment {

    private final int REQUEST_CODE = 123;

    private TextInputEditText inputDob, inputGender, inputAddress, inputPhoneNumber, inputEducation;
    private TextInputLayout inputDobLayout, inputGenderLayout, inputAddressLayout, inputPhoneNumberLayout, inputEducationLayout;
    private AlertDialog alertDialog;

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
    }

    private void addEvents() {
        inputDobEvents();
        inputGenderEvents();
        showLocationActivity();
    }

    private void showLocationActivity() {
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
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
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
