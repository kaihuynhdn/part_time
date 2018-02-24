package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginMethodActivity extends AppCompatActivity {

    private Button mCreateButton;
    private TextView mToLoginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_method);

        addComponents();
        addEvents();
    }

    private void addEvents() {
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMethodActivity.this, RegisterPersonalInfoActivity.class));
            }
        });

        mToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginMethodActivity.this, LoginActivity.class));
            }
        });
    }

    private void addComponents() {
        mCreateButton = findViewById(R.id.btn_create);
        mToLoginTextView = findViewById(R.id.tv_toLogin_method);
    }
}
