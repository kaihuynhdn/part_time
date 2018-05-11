package com.example.kaihuynh.part_timejob.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.ListCandidateActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.adapters.NotificationAdapter;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.models.Notification;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment implements NotificationAdapter.ListItemClickListener{

    private NotificationAdapter mAdapter;
    private RecyclerView mNotificationRecyclerView;
    private ArrayList<Notification> mNotificationArrayList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout mEmptyView;

    @SuppressLint("StaticFieldLeak")
    private static NotificationFragment sInstance = null;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        addComponents(view);
        initialize();
        setWidgetListeners();

        return  view;
    }

    private void addComponents(View view) {
        mEmptyView = view.findViewById(R.id.rl_empty_notification);
        mNotificationRecyclerView = view.findViewById(R.id.rv_notification);
        swipeRefreshLayout = view.findViewById(R.id.sw_notification);
    }

    private void initialize() {
        sInstance = this;

        mNotificationArrayList = new ArrayList<>();
        if (UserManager.getInstance().getUser().getNotificationList()!=null){
            mNotificationArrayList.addAll(UserManager.getInstance().getUser().getNotificationList());
        }
        mAdapter = new NotificationAdapter(getContext(), R.layout.rv_notification_item, mNotificationArrayList, this);
        mNotificationRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mNotificationRecyclerView.setLayoutManager(layoutManager);
        mNotificationRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.lightBlue_700));

        refreshData();

    }

    private void setWidgetListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshData();
                        swipeRefreshLayout.setRefreshing(false);
                        mNotificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()){
                            @Override
                            public boolean canScrollVertically() {
                                return true;
                            }
                        });
                    }
                }, 2000);
            }
        });
    }

    public void refreshData(){
        mNotificationArrayList = new ArrayList<>();
        if (UserManager.getInstance().getUser().getNotificationList()!=null){
            mNotificationArrayList.addAll(UserManager.getInstance().getUser().getNotificationList());
        }
        if (mNotificationArrayList==null || mNotificationArrayList.size()<1){
            mEmptyView.setVisibility(View.VISIBLE);
        }else {
            mEmptyView.setVisibility(View.GONE);
        }
        mAdapter = new NotificationAdapter(getContext(), R.layout.rv_notification_item, mNotificationArrayList, this);
        mNotificationRecyclerView.setAdapter(mAdapter);
    }

    public static NotificationFragment getInstance(){
        if (sInstance == null) {
            sInstance = new NotificationFragment();
        }
        return sInstance;
    }

    @Override
    public void onListItemClick(int clickItemIndex) {
        Notification notification = mNotificationArrayList.get(clickItemIndex);
        if (notification.getTypes() == Notification.TO_CANDIDATE) {
            HomePageActivity.getInstance().getBottomNavigation().setSelectedItemId(R.id.action_applied_jobs);
            HomePageActivity.getInstance().getViewPager().setCurrentItem(2);
        }else if(notification.getTypes() == Notification.TO_RECRUITER){
            Intent intent = new Intent(getContext(), ListCandidateActivity.class);
            intent.putExtra("job", notification.getJob());
            startActivity(intent);
        }

        if (notification.getStatus() == Notification.STATUS_NOT_SEEN){
            mNotificationArrayList.get(clickItemIndex).setStatus(Notification.STATUS_SEEN);
            mAdapter.notifyDataSetChanged();
            UserManager.getInstance().getUser().setNotificationList(mNotificationArrayList);
            UserManager.getInstance().updateUser(UserManager.getInstance().getUser());
        }
    }
}
