package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.example.kaihuynh.part_timejob.controllers.UserManger;
import com.example.kaihuynh.part_timejob.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private SeekBar mSeekBar;
    private ProgressBar mProgressBar;
    private Handler mHandler;

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
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("users");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user==null){
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }else {
                    mUserDatabaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User u = dataSnapshot.getValue(User.class);
                            UserManger.getInstance().load(u);
                            startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }



    private void addEvents() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressBar.getProgress() < 100) {
                    android.os.SystemClock.sleep(30);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(mSeekBar.getProgress() + 1);
                            mProgressBar.setProgress(mProgressBar.getProgress() + 1);
                        }
                    });
                }
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        }).start();
    }

    private void addComponents() {
        mSeekBar = findViewById(R.id.seekBar);
        mProgressBar = findViewById(R.id.progressBar);
        mHandler = new Handler();
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

}
