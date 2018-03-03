package com.example.kaihuynh.part_timejob;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class RegisterAccountInfoActivity extends AppCompatActivity {

    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account_info);

        addComponents();
        addEvents();
    }

    private void addEvents() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataRegister();
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater().from(this);
        View view = inflater.inflate(R.layout.register_notification_dialog, null);
        builder.setView(view);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(RegisterAccountInfoActivity.this, RegisterPersonalInfoActivity.class));
                finish();
            }
        });
        builder.setNegativeButton("Về Trang chủ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(RegisterAccountInfoActivity.this, HomePageActivity.class));
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void saveDataRegister() {
    }


    private void addComponents() {
        mRegisterButton = findViewById(R.id.btn_register_account);
    }
}
