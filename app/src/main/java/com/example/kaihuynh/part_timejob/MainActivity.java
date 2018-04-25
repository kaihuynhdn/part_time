package com.example.kaihuynh.part_timejob;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private boolean interrupt, isLoaded;
    private ProgressBar mProgressBar;
    private Handler mHandler;
    private Runnable runnable;
    private int FINISH_LOADED;
    private Thread t;

    private FirebaseFirestore db;
    private CollectionReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addComponents();
        initialize();

    }

    private void initialize() {
        isLoaded = false;
        interrupt = true;
        mHandler = new Handler();
        FINISH_LOADED = 0;
        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();
        mUserReference = db.collection("users");
        JobManager.getInstance().loadData();

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!JobManager.isLoadedJobQuantity) {

                }
                JobManager.getInstance().refreshData();

                while ((!isLoaded || FINISH_LOADED == 0) || !JobManager.isRefreshed) {
                    Log.v("AAA", String.valueOf(isLoaded));
                    Log.v("BBB", String.valueOf(FINISH_LOADED));
                    Log.v("CCC", String.valueOf(JobManager.isRefreshed));
                    Log.v("DDD", String.valueOf(interrupt));
                    if (isConnect()) {
                        isLoaded = true;
                    }
                }
                if (isLoaded && FINISH_LOADED != 0) {
                    if (FINISH_LOADED == 1) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    } else if (FINISH_LOADED == 2){
                        startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                        finish();
                    }
                }
            }
        });

        mHandler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                if (t.isAlive() || !t.isInterrupted()) {
                    t.interrupt();
                }
                Toast.makeText(MainActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.GONE);

            }
        }, 10000);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    FINISH_LOADED = 1;
                    t.start();
                } else {
                    mUserReference.document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Common.currentToken = FirebaseInstanceId.getInstance().getToken();
                                User u = task.getResult().toObject(User.class);
                                UserManager.getInstance().updateUser(u);
                                FINISH_LOADED = 2;
                                t.start();
                            }
                        }
                    });
                }
            }
        };

    }

    private boolean isConnect() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MainActivity.this
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

    private void addComponents() {
        mProgressBar = findViewById(R.id.progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
        mAuth.removeAuthStateListener(mAuthStateListener);
        JobManager.getInstance().removeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Thread.currentThread().isAlive()) {
            t.interrupt();
        }
    }
}
