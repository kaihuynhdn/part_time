package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private Button mLoginButton;
    private TextView mToRegisterTextView;

    public static LoginActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addComponents();
        addEvents();
    }

    private void addEvents() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                finish();
            }
        });

        mToRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginMethodActivity.class));
            }
        });
    }

    private void addComponents() {
        mLoginButton = findViewById(R.id.btn_login);
        mToRegisterTextView = findViewById(R.id.tv_to_register);
        sInstance = this;
    }

    public static LoginActivity getInstance(){
        if (sInstance == null) {
            sInstance = new LoginActivity();
        }
        return sInstance;
    }
}
