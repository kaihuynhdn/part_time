package com.example.kaihuynh.part_timejob;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.adapters.SearchJobAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.models.Job;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements SearchJobAdapter.ListItemClickListener{

    private RecyclerView mRecyclerView;
    private SearchJobAdapter adapter;
    private ArrayList<Job> mArrayList;
    private RelativeLayout mEmptyLayout;
    private ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    private static SearchActivity sInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getWidgets();
        init();
        setWidgetsListener();

    }

    private void getWidgets() {
        mRecyclerView = findViewById(R.id.rv_search_job);
        progressBar = findViewById(R.id.pb_search_job);
        mEmptyLayout = findViewById(R.id.rl_empty_search);
    }

    private void init() {
        mArrayList = new ArrayList<>();
        adapter = new SearchJobAdapter(SearchActivity.this, R.layout.rv_job_item, mArrayList, this);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
    }

    private void setWidgetsListener() {

    }

    private void loadData(){
        JobManager.getInstance().loadAllJob();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (JobManager.isLoadedAllJob){
                    mArrayList.clear();
                    mArrayList.addAll(JobManager.getInstance().getAllJob());
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(SearchActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, 1300);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                progressBar.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
                mEmptyLayout.setVisibility(View.GONE);
                loadData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        ArrayList<Job> list = new ArrayList<>();
                        list.addAll(mArrayList);
                        query.toLowerCase(Locale.getDefault());
                        mArrayList.clear();
                        if (query.length() == 0) {
                            mArrayList.addAll(list);
                        } else {
                            for (Job j : list) {
                                if (j.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                                    mArrayList.add(j);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (mArrayList.size()==0){
                            mEmptyLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1300);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static SearchActivity getInstance() {
        if (sInstance == null){
            sInstance = new SearchActivity();
        }

        return sInstance;
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent intent = new Intent(SearchActivity.this, JobDescriptionActivity.class);
        intent.putExtra("job", mArrayList.get(clickItemIndex));
        startActivity(intent);
    }
}
