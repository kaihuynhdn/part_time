package com.example.kaihuynh.part_timejob;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kaihuynh.part_timejob.adapters.MyAdapter;
import com.example.kaihuynh.part_timejob.controllers.JobManager;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.interfaces.LoadMore;
import com.example.kaihuynh.part_timejob.models.Job;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class JobListFragment extends Fragment implements MyAdapter.ListItemClickListener {
    private final int REQUEST_CODE = 101;
    private MyAdapter mAdapter;
    private RecyclerView mJobRecyclerView;
    private ArrayList<Job> mJobArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button mLocationButton, mRemoveLocation;
    private CollectionReference mUserReference;
    private ListenerRegistration listenerRegistration;

    private boolean isLoaded = false;

    private static JobListFragment sInstance = null;

    public JobListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_list, container, false);

        addComponents(view);
        initialize();
        setWidgetListener();

        return view;
    }

    private void addComponents(View view) {
        mJobRecyclerView = view.findViewById(R.id.rv_jobs);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mLocationButton = view.findViewById(R.id.btn_location);
        mRemoveLocation = view.findViewById(R.id.btn_remove_location);
    }


    private void initialize() {
        mUserReference = FirebaseFirestore.getInstance().collection("users");

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));

        mJobArrayList = JobManager.getInstance().getJobs();
        mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        });
        mJobRecyclerView.setHasFixedSize(true);

        mAdapter = new MyAdapter(mJobRecyclerView, getContext(), R.layout.job_list_item, mJobArrayList, this);
        mJobRecyclerView.setAdapter(mAdapter);

        if (!isLoaded){
            mJobArrayList = JobManager.getInstance().getJobs();
            mAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                @Override
                public boolean canScrollVertically() {
                    return true;
                }
            });
            isLoaded = true;
        }

        enableLoadMore();

    }

    private void enableLoadMore(){
        mAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {
                if(mJobArrayList.size() <= JobManager.getInstance().getJobQuantity() && mJobArrayList.size()>=10){
                    if (mJobArrayList.size()>0 && mJobArrayList.get(mJobArrayList.size()-1)!= null){
                        mJobArrayList.add(null);
                    }
                    mAdapter.notifyItemInserted(mJobArrayList.size()-1);
                    final String s = mLocationButton.getText().toString();
                    if (s.equals("Địa điểm")){
                        JobManager.getInstance().loadMoreJob(mJobArrayList.get(mJobArrayList.size()-2).getTimestamp());
                    }else {
                        JobManager.getInstance().loadMoreJobByLocation(mJobArrayList.get(mJobArrayList.size()-2).getTimestamp(), s);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mJobArrayList.size()>0 && mJobArrayList.get(mJobArrayList.size()-1) == null){
                                mJobArrayList.remove(mJobArrayList.size()-1);
                                mAdapter.notifyItemRemoved(mJobArrayList.size());
                            }
                            if (s.equals("Địa điểm")){
                                mJobArrayList.addAll(JobManager.getInstance().getLoadMoreJobs());
                            }{
                                mJobArrayList.addAll(JobManager.getInstance().getMoreJobListByLocation());
                            }
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setLoaded();
                        }
                    }, 2000);
                }
            }
        });
    }


    private void setWidgetListener() {
        listenerRegistration = mUserReference.document(UserManager.getInstance().getUser().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                mAdapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                final String s = mLocationButton.getText().toString();
                if (s.equals("Địa điểm")){
                    JobManager.getInstance().refreshData();
                }else {
                    JobManager.getInstance().loadJobByLocation(s);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (s.equals("Địa điểm")){
                            mJobArrayList = JobManager.getInstance().getJobs();
                        }else {
                            mJobArrayList = JobManager.getInstance().getJobListByLocation();
                        }
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                            @Override
                            public boolean canScrollVertically() {
                                return true;
                            }
                        });
                        mAdapter.setLoaded();
                    }
                }, 2000);
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PickLocationActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mRemoveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
                mLocationButton.setText("Địa điểm");
                mRemoveLocation.setVisibility(View.GONE);
            }
        });
    }

    private void refresh(){
        swipeRefreshLayout.setRefreshing(true);
        mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        JobManager.getInstance().refreshData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mJobArrayList = JobManager.getInstance().getJobs();
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                    @Override
                    public boolean canScrollVertically() {
                        return true;
                    }
                });
                isLoaded = true;
            }
        }, 2000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            mLocationButton.setText(data.getStringExtra("location"));

            swipeRefreshLayout.setRefreshing(true);
            mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            JobManager.getInstance().loadJobByLocation(data.getStringExtra("location"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mJobArrayList.clear();
                    mJobArrayList.addAll(JobManager.getInstance().getJobListByLocation());
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    mJobRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                        @Override
                        public boolean canScrollVertically() {
                            return true;
                        }
                    });
                    mAdapter.setLoaded();
                }
            }, 2000);
        }
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Intent intent = new Intent(getContext(), JobDescriptionActivity.class);
        intent.putExtra("job", mJobArrayList.get(clickItemIndex));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocationButton.getText().equals("Địa điểm")){
            mRemoveLocation.setVisibility(View.GONE);
        }else {
            mRemoveLocation.setVisibility(View.VISIBLE);
        }

        listenerRegistration = mUserReference.document(UserManager.getInstance().getUser().getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listenerRegistration!=null){
            listenerRegistration.remove();
        }
    }

    public static JobListFragment getInstance(){
        if (sInstance == null) {
            sInstance = new JobListFragment();
        }
        return sInstance;
    }
}
