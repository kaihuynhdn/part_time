package com.example.kaihuynh.part_timejob;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.adapters.HomeViewPagerAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.reflect.Field;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    //Navigation drawer
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TextView mUserName, mUserEmail;
    private CustomViewPager viewPager;
    private View header;
    private User user;

    //Bottom navigation
    private BottomNavigationView mBottomNavigationView;
    public static HomePageActivity sInstance = null;

    //Firebase Instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //private DatabaseReference mUserRef;
    private FirebaseFirestore db;
    private CollectionReference mUserReference;

    //Google Instance variables
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        addComponents();
        initialize();
        addEvents();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        disableShiftMode(mBottomNavigationView);

    }

    private void initialize() {
        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(HomePageActivity.this.getSupportFragmentManager());
        adapter.addFragment(new JobListFragment());
        adapter.addFragment(new JobLikedFragment());
        adapter.addFragment(new JobAppliedFragment());
        adapter.addFragment(new Fragment());
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

        user = UserManager.getInstance().getUser();
        if (user.getFullName() != null && user.getEmail() != null) {
            mUserName.setText(user.getFullName());
            mUserEmail.setText(user.getEmail());
        }

        toolbar.setTitle("Việc Làm Part Time");
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(HomePageActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        db = FirebaseFirestore.getInstance();
        mUserReference = db.collection("users");

//        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mUserReference.document(UserManager.getInstance().getUser().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User u = documentSnapshot.toObject(User.class);
                    mUserEmail.setText(u.getEmail());
                    mUserName.setText(u.getFullName());
                    UserManager.getInstance().load(u);
                }
            }
        });
    }

    private void addComponents() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        mUserEmail = header.findViewById(R.id.tv_user_email);
        mUserName = header.findViewById(R.id.tv_user_name);
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager_home);
        sInstance = this;
    }

    private void addEvents() {
        navigationView.setNavigationItemSelectedListener(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavigationEvents(item);
                return true;
            }
        });
    }

    private void bottomNavigationEvents(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                viewPager.setCurrentItem(0);
                break;
            case R.id.action_like_jobs:
                viewPager.setCurrentItem(1);
                break;
            case R.id.action_applied_jobs:
                viewPager.setCurrentItem(2);
                break;
            case R.id.action_notification:
                viewPager.setCurrentItem(3);
                break;
        }

    }

    public BottomNavigationView getmBottomNavigationView() {
        return mBottomNavigationView;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        drawer.closeDrawer(GravityCompat.START);


        final Handler mHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.SystemClock.sleep(180);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (id == R.id.personal_info_menu) {
                            if (UserManager.getInstance().isUpdated()) {
                                startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
                            } else {
                                Intent intent = new Intent(HomePageActivity.this, RegisterPersonalInfoActivity.class);
                                intent.putExtra("activity", "HomePageActivity");
                                startActivity(intent);
                            }
                        } else if (id == R.id.recruitment_menu) {
                            if (UserManager.getInstance().isUpdated()) {
                                startActivity(new Intent(HomePageActivity.this, RecruitingActivity.class));
                            } else {
                                showDialog();
                            }
                        } else if (id == R.id.manage_recruitment_post_menu) {
                            startActivity(new Intent(HomePageActivity.this, ListRecruitmentActivity.class));
                        } else if (id == R.id.log_out_menu) {
                            signOut();
                        } else if (id == R.id.contact_menu) {

                        }
                    }
                });
            }
        }).start();

        return true;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn cần hoàn thiện hồ sơ cá nhân để đăng tuyển");
        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomePageActivity.this, RegisterPersonalInfoActivity.class);
                intent.putExtra("activity", "RecruitingActivity");
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

            }
        });

        LoginManager.getInstance().logOut();
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

    @SuppressLint("RestrictedApi")
    private void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public BottomNavigationView getBottomNavigation() {
        return this.mBottomNavigationView;
    }

    public static HomePageActivity getInstance() {
        if (sInstance == null) {
            sInstance = new HomePageActivity();
        }
        return sInstance;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
