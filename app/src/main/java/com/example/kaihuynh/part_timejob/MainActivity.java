package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private boolean interrupt, isLoaded;
    private ProgressBar mProgressBar;
    private Handler mHandler;
    private int FINISH_LOADED;
    private Thread t;
    private ValueEventListener valueEventListener;

    private FirebaseFirestore db;
    private CollectionReference mUserReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference connectedRef;

    private static MainActivity sInstance = null;

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

        connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        checkConnect();
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while ((!isLoaded || FINISH_LOADED == 0)) {
                    SystemClock.sleep(2500);
                    if (!interrupt) {
                        isLoaded = true;
                    }
                }
                if (isLoaded && FINISH_LOADED != 0) {
                    if (FINISH_LOADED == 1) {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        });

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
                                User u = task.getResult().toObject(User.class);
                                UserManager.getInstance().load(u);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(MainActivity.this, HomePageActivity.class));
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                    });
                }
            }
        };

    }

    private void checkConnect() {
        valueEventListener = connectedRef.addValueEventListener(new ValueEventListener() {
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
        mAuth.removeAuthStateListener(mAuthStateListener);
        JobManager.getInstance().removeListener();
        if (valueEventListener != null) {
            connectedRef.removeEventListener(valueEventListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Thread.currentThread().isAlive()) {
            t.interrupt();
        }
        if (valueEventListener != null) {
            connectedRef.removeEventListener(valueEventListener);
        }
    }
}
