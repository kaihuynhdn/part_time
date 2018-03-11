package com.example.kaihuynh.part_timejob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.kaihuynh.part_timejob.adapters.CandidateAdapter;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.User;

import java.util.ArrayList;
import java.util.Date;

public class ListCandidateActivity extends AppCompatActivity implements CandidateAdapter.ListItemClickListener{

    private CandidateAdapter mAdapter;
    private RecyclerView mListCandidateRecyclerView;
    private ArrayList<Candidate> mCandidateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addComponents();
        loadDataToRecyclerView();
    }

    private void loadDataToRecyclerView() {
        for(int j = 0; j<5; j++){
            User user = new User();
            user.setFullName("User" + j);
            user.setGender("Nam");
            user.setAddress("");
            user.setEducation("");
            user.setDayOfBirth(new Date());
            user.setForeignLanguages("");
            user.setPhoneNumber("123456789");
            user.setSkills("");
            user.setPersonalDescription("");
            user.setEmail("a@b");

            mCandidateList.add(new Candidate(user, "", new Date(),""));
        }

        mAdapter = new CandidateAdapter(this, R.layout.candidate_rv_item, mCandidateList, this);
        mListCandidateRecyclerView.setAdapter(mAdapter);

    }

    private void addComponents() {
        mListCandidateRecyclerView = findViewById(R.id.rv_list_candidate);
        mCandidateList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListCandidateRecyclerView.setLayoutManager(layoutManager);
        mListCandidateRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent intent = new Intent(ListCandidateActivity.this, CandidateActivity.class);
        intent.putExtra("candidate", mCandidateList.get(clickItemIndex));
        startActivity(intent);
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
}
