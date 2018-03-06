package com.example.kaihuynh.part_timejob;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.Field;

public class HomePageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Navigation drawer
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    //Bottom navigation
    private BottomNavigationView mBottomNavigationView;
    public static HomePageActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        addComponents();
        addEvents();
        initial();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        disableShiftMode(mBottomNavigationView);
    }

    private void initial() {
        toolbar.setTitle("Danh Sách Công Việc");
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
        Fragment fragment = new JobListFragment();
        switch (item.getItemId()){
            case R.id.action_home:
                fragment = new JobListFragment();
                break;
            case R.id.action_like_jobs:
                fragment = new JobLikedFragment();
                break;
            case R.id.action_applied_jobs:
                fragment = new JobAppliedFragment();
                break;
            case R.id.action_notification:
                fragment = new Fragment();
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_home, fragment);
        transaction.commit();
    }

    private void addComponents() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mBottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_nav);
        sInstance = this;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_home, new JobListFragment());
        transaction.commit();
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
        int id = item.getItemId();

        if (id == R.id.personal_info_menu) {
            startActivity(new Intent(HomePageActivity.this, ProfileActivity.class));
        } else if (id == R.id.recruitment_menu) {
            startActivity(new Intent(HomePageActivity.this, RecruitingActivity.class));
        } else if (id == R.id.manage_recruitment_post_menu) {

        } else if (id == R.id.log_out_menu) {

        } else if (id == R.id.contact_menu) {

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public BottomNavigationView getBottomNavigation(){
        return this.mBottomNavigationView;
    }

    public static HomePageActivity getInstance(){
        if (sInstance == null) {
            sInstance = new HomePageActivity();
        }
        return sInstance;
    }
}
