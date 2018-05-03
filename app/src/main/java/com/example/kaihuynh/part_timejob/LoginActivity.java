package com.example.kaihuynh.part_timejob;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity{

    private Button mLoginButton;
    private TextView mToRegisterTextView;
    private EditText mEmail, mPassword;
    private ProgressDialog mProgress;

    @SuppressLint("StaticFieldLeak")
    private static LoginActivity sInstance = null;

    //Firebase instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseFirestore db;
    private CollectionReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWidgets();
        initialize();
        setWidgetListeners();
    }

    private void getWidgets() {
        mEmail = findViewById(R.id.editText_email_login);
        mPassword = findViewById(R.id.editText_password_login);
        mLoginButton = findViewById(R.id.btn_login);
        mToRegisterTextView = findViewById(R.id.tv_to_register);
        sInstance = this;
    }

    private void initialize() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Đang xác nhận thông tin...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        JobManager.getInstance().refreshData();
        db = FirebaseFirestore.getInstance();
        mUserReference = db.collection("users");

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!= null){
                    mUserReference.document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult()!=null){
                                Common.currentToken = FirebaseInstanceId.getInstance().getToken();
                                User u = task.getResult().toObject(User.class);
                                u.setToken(Common.currentToken);
                                UserManager.getInstance().updateUser(u);
                            }

                            while (!JobManager.isRefreshed){

                            }

                            if (mProgress.isShowing()){
                                mProgress.dismiss();
                            }

                            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                            finish();
                        }
                    });
                }
            }
        };
    }

    private void setWidgetListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnect()){
                    mProgress.show();
                    mAuth.signInWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                    } else {
                                        mProgress.dismiss();
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Thông tin đăng nhập không chính xác.",
                                                Toast.LENGTH_SHORT).show();
                                        mEmail.requestFocus();
                                    }

                                    // ...
                                }
                            });
                }else {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối! Vui lòng kiểm tra đường truyền.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mEmail.getText().length()>0 && mPassword.getText().length()>0){
                    mLoginButton.setEnabled(true);
                }else {
                    mLoginButton.setEnabled(false);
                }
            }
        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mEmail.getText().length()>0 && mPassword.getText().length()>0){
                    mLoginButton.setEnabled(true);
                }else {
                    mLoginButton.setEnabled(false);
                }
            }
        });

        mToRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginMethodActivity.class));
            }
        });
    }

    public static LoginActivity getInstance(){
        if (sInstance == null) {
            sInstance = new LoginActivity();
        }
        return sInstance;
    }

    private boolean isConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) LoginActivity.this
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
