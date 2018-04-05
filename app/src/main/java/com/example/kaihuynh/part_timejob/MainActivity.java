package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private boolean interrupt, firstLoad;
    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private Handler mHandler;
    private int FINISH_LOADED;
    private Thread t;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private static MainActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addComponents();
        initialize();

    }

    private void initialize() {
        mSeekBar.setEnabled(false);
        firstLoad = true;
        interrupt = false;
        mHandler = new Handler();
        FINISH_LOADED = 0;
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    interrupt = false;
                } else {
                    interrupt = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while ((mProgressBar.getProgress() < 100 || FINISH_LOADED == 0)) {
                    android.os.SystemClock.sleep(30);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!interrupt) {
                                mSeekBar.setProgress(mSeekBar.getProgress() + 1);
                                mProgressBar.setProgress(mProgressBar.getProgress() + 1);
                            }
                        }
                    });
                }

                if (mProgressBar.getProgress() >= 100 && FINISH_LOADED != 0) {
                    if (FINISH_LOADED == 1) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    } else if (FINISH_LOADED == 2) {
                        startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                        finish();
                    }
                }
            }
        });

        t.start();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

//                anim();
                if (user == null) {
                    FINISH_LOADED = 1;

                } else {
                    mUserDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            UserManager.getInstance().load(u);
                            JobManager.getInstance().loadData();

                            FINISH_LOADED = 2;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MainActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        };
    }

    private void addComponents() {
        mSeekBar = findViewById(R.id.seekBar);
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
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Thread.currentThread().isAlive()) {
            t.interrupt();
        }
    }
}
