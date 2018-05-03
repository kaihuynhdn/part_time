package com.example.kaihuynh.part_timejob;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.ApplyJob;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.Notification;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class RegisterAccountInfoActivity extends AppCompatActivity {

    private Button mRegisterButton;
    private TextInputLayout inputNameLayout, inputEmailLayout, inputPasswordLayout, inputConfirmPasswordLayout;
    private TextInputEditText mFullName, mEmail, mPassword, mConfirmPassword;
    private ProgressDialog mProgress;

    //Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account_info);

        getWidgets();
        initialize();
        setWidgetListeners();
    }

    private void getWidgets() {
        mFullName = findViewById(R.id.input_fullName);
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mConfirmPassword = findViewById(R.id.input_confirm_password);
        mRegisterButton = findViewById(R.id.btn_register_account);
        inputNameLayout = findViewById(R.id.input_fullName_layout);
        inputEmailLayout = findViewById(R.id.input_layout_email);
        inputPasswordLayout = findViewById(R.id.input_layout_password);
        inputConfirmPasswordLayout = findViewById(R.id.input_layout_confirm_password);
    }

    private void initialize() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Đang kiểm tra dữ liệu...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

    }

    private void setWidgetListeners() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataRegister();
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.register_notification_dialog, null);
        builder.setView(view);
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(RegisterAccountInfoActivity.this, RegisterPersonalInfoActivity.class);
                intent.putExtra("activity", "RegisterAccountInfoActivity");
                startActivity(intent);
                LoginMethodActivity.getInstance().finish();
                LoginActivity.getInstance().finish();
                finish();
            }
        });
        builder.setNegativeButton("Về Trang chủ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(RegisterAccountInfoActivity.this, HomePageActivity.class));
                LoginMethodActivity.getInstance().finish();
                LoginActivity.getInstance().finish();
                finish();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void saveDataRegister() {
        if (isValid()){
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Common.currentToken = FirebaseInstanceId.getInstance().getToken();

                                FirebaseUser userFirebase = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(mFullName.getText().toString()).build();
                                userFirebase.updateProfile(profileUpdates);

                                User user = new User();
                                user.setId(userFirebase.getUid());
                                user.setEmail(userFirebase.getEmail());
                                user.setFullName(mFullName.getText().toString());
                                user.setNotificationList(new ArrayList<Notification>());
                                user.setFavouriteJobList(new ArrayList<Job>());
                                user.setAppliedJobList(new ArrayList<ApplyJob>());
                                user.setRecruitmentList(new ArrayList<Job>());
                                user.setToken(Common.currentToken);

                                UserManager.getInstance().updateUser(user);


                                mProgress.dismiss();
                                showAlertDialog();
                                signIn();
                            } else {
                                // If sign in fails, display a message to the user.
                                inputEmailLayout.setError("Địa chỉ email đã tồn tại.");
                                mProgress.dismiss();
                            }

                        }
                    });


        }

    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterAccountInfoActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private boolean isValid(){
        if(mFullName.getText().toString().equals("")){
            inputNameLayout.setErrorEnabled(true);
            inputNameLayout.setError("Chưa điền đủ thông tin.");
            inputNameLayout.requestFocus();
            return false;
        }else {
            inputNameLayout.setErrorEnabled(false);
        }

        if(TextUtils.isEmpty(mEmail.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(mEmail.getText().toString()).matches()){
            inputEmailLayout.setErrorEnabled(true);
            inputEmailLayout.setError("Thông tin email chưa chính xác.");
            inputEmailLayout.requestFocus();
            return false;
        }else {
            inputEmailLayout.setErrorEnabled(false);
        }

        if (mPassword.getText().toString().equals("") || mPassword.getText().toString().length()<6){
            inputPasswordLayout.setErrorEnabled(true);
            inputPasswordLayout.setError("Mật khẩu phải ít nhất 6 ký tự.");
            inputPasswordLayout.requestFocus();
            return false;
        }else {
            inputPasswordLayout.setErrorEnabled(false);
        }

        if(!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())){
            inputConfirmPasswordLayout.setErrorEnabled(true);
            inputConfirmPasswordLayout.setError("Xác nhận mật khẩu chưa chính xác.");
            inputConfirmPasswordLayout.requestFocus();
            return false;
        }else {
            inputConfirmPasswordLayout.setErrorEnabled(false);
        }

        return true;
    }
}
