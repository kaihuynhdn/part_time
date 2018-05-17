package com.example.kaihuynh.part_timejob;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.adapters.HomeViewPagerAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.fragments.JobAppliedFragment;
import com.example.kaihuynh.part_timejob.fragments.NotificationFragment;
import com.example.kaihuynh.part_timejob.models.User;
import com.example.kaihuynh.part_timejob.others.CircleTransform;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.PlusShare;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private final int NUM_TABS = 4;
    private final int REQUEST_FOR_GOOGLE_PLUS = 123;

    //Navigation drawer
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private TextView mUserName, mUserEmail;
    private ImageView imageView, share;
    private CustomViewPager viewPager;
    private HomeViewPagerAdapter adapter;
    private AlertDialog languageDialog;
    private View header;
    private User user;
    private ListenerRegistration listenerRegistration;
    public static boolean isDestroyed = false;

    //Bottom navigation
    private BottomNavigationView mBottomNavigationView;
    @SuppressLint("StaticFieldLeak")
    private static HomePageActivity sInstance = null;

    //Firebase Instance variables
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db;
    private CollectionReference mUserReference;

    //Google Instance variables
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getWidgets();
        initialize();
        setWidgetsListener();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        disableShiftMode(mBottomNavigationView);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "");
        if (!language.isEmpty()){
            setLanguage(language);
        }

    }

    private void getWidgets() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        mUserEmail = header.findViewById(R.id.tv_user_email);
        mUserName = header.findViewById(R.id.tv_user_name);
        imageView = header.findViewById(R.id.imageView);
        mBottomNavigationView = findViewById(R.id.bottom_nav);
        viewPager = findViewById(R.id.view_pager_home);
        sInstance = this;
    }

    private void initialize() {
        JobManager.getInstance().loadData();

        adapter = new HomeViewPagerAdapter(getSupportFragmentManager(), NUM_TABS);
        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(3);

        user = UserManager.getInstance().getUser();
        if (user.getFullName() != null && user.getEmail() != null) {
            mUserName.setText(user.getFullName());
            mUserEmail.setText(user.getEmail());
        }
    if (user.getImageURL() != null && !user.getImageURL().equals("")) {
            Picasso.get().load(user.getImageURL()).transform(new CircleTransform()).placeholder(R.drawable.loading_img).into(imageView);
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = getIntent();
        if (intent.getStringExtra("notification") != null) {
            viewPager.setCurrentItem(3);
            mBottomNavigationView.setSelectedItemId(R.id.action_notification);
        }
    }

    private void setWidgetsListener() {
        navigationView.setNavigationItemSelectedListener(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                bottomNavigationEvents(item);
                return true;
            }
        });
    }

    private void showPopupWindow(View view, int menu) {
        PopupMenu popup = new PopupMenu(this, view);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_share_fb:
                        ShareLinkContent content = new ShareLinkContent.Builder()
                                .setQuote("The content of status...")
                                .setContentUrl(Uri.parse("https://play.google.com/store"))
                                .build();
                        ShareDialog.show(HomePageActivity.this, content);
                        break;
                    case R.id.action_share_google:
                        Intent shareIntent = new PlusShare.Builder(HomePageActivity.this)
                                .setType("text/plain")
                                .setText("Link: \nhttp://play.google.com/store/apps/details?id=com.example.kaihuynh.part_timejob")
                                .setContentUrl(Uri.parse("https://play.google.com/store"))
                                .getIntent();
                        startActivity(shareIntent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    private void bottomNavigationEvents(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                viewPager.setCurrentItem(0);
                toolbar.setTitle("Việc làm Part Time");
                break;
            case R.id.action_like_jobs:
                viewPager.setCurrentItem(1);
                toolbar.setTitle(getResources().getString(R.string.favourite_job));
                break;
            case R.id.action_applied_jobs:
                viewPager.setCurrentItem(2);
                toolbar.setTitle(getResources().getString(R.string.apply_job));
                break;
            case R.id.action_notification:
                viewPager.setCurrentItem(3);
                toolbar.setTitle(getResources().getString(R.string.notify));
                break;
        }

    }

    public CustomViewPager getViewPager() {
        return viewPager;
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
        getMenuInflater().inflate(R.menu.home_page_menu, menu);
        share  = (ImageView) menu.findItem(R.id.action_share).getActionView();
        share.setImageResource(R.drawable.share);
        share.setPadding(0,0,20,0);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(share, R.menu.share_menu);

            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                            User u = UserManager.getInstance().getUser();
                            u.setToken("");
                            UserManager.getInstance().updateUser(u);
                            signOut();
                        } else if (id == R.id.contact_menu) {

                        } else if (id == R.id.language_menu){
                            showLanguageDialog();
                        }
                    }
                });
            }
        }).start();

        return true;
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"Tiếng Việt", "English"}, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        setLanguage("vn");
                        break;
                    case 1:
                        setLanguage("en");
                        break;
                }
                languageDialog.dismiss();
            }
        });
        builder.setTitle(getResources().getString(R.string.pick_education_dialog_title));

        languageDialog = builder.create();
        languageDialog.show();
    }

    private void setLanguage(String languages){
        Locale locale = new Locale (languages);
        Locale.setDefault(locale);
        getResources().getConfiguration().locale = locale;
        getResources().updateConfiguration(getResources().getConfiguration(), getResources().getDisplayMetrics());
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.activity_home_page_drawer);
        mBottomNavigationView.getMenu().clear();
        mBottomNavigationView.inflateMenu(R.menu.bottom_nav_menu);
        adapter = new HomeViewPagerAdapter(getSupportFragmentManager(), NUM_TABS);
        viewPager.setAdapter(adapter);
        disableShiftMode(mBottomNavigationView);
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", languages);
        editor.apply();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.register_info_dialog_title));
        builder.setMessage(getResources().getString(R.string.register_info_dialog_message));
        builder.setPositiveButton(getResources().getString(R.string.next_button_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(HomePageActivity.this, RegisterPersonalInfoActivity.class);
                intent.putExtra("activity", "RecruitingActivity");
                startActivity(intent);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
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

        listenerRegistration = mUserReference.document(UserManager.getInstance().getUser().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User u = documentSnapshot.toObject(User.class);
                    mUserEmail.setText(u.getEmail());
                    mUserName.setText(u.getFullName());
                    UserManager.getInstance().load(u);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NotificationFragment.getInstance().refreshData();
                            JobAppliedFragment.getInstance().refreshData();
                        }
                    }, 1000);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        JobManager.getInstance().removeListener();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
