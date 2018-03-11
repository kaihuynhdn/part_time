package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.JobLikedFragment;
import com.example.kaihuynh.part_timejob.ListRecruitmentActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.Job;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-12.
 */

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobItemViewHolder> {

    private ArrayList<Job> mJobList;
    private Context context;
    private int layout;

    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex);
    }

    public JobAdapter(Context context, int layout, ArrayList<Job> mJobList, ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;
    }

    @Override
    public JobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new JobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobItemViewHolder holder, final int position) {
        Job job = mJobList.get(position);
        holder.mJobPostedDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(job.getPostedDate()));
        holder.mJobLocation.setText(job.getLocation());
        holder.mJobTitle.setText(job.getName());
        holder.mJobSalary.setText(job.getSalary());

        if (HomePageActivity.getInstance().getBottomNavigation().getSelectedItemId() == R.id.action_applied_jobs) {
            holder.mOption.setVisibility(View.GONE);
        } else if(ListRecruitmentActivity.getInstance() == context){
            holder.mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupWindow(view, R.menu.list_recruitment_menu, position);
                }
            });
        } else {
            holder.mOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (HomePageActivity.getInstance().getBottomNavigation().getSelectedItemId()) {
                        case R.id.action_home:
                            showPopupWindow(view, R.menu.job_item_menu, position);
                            break;
                        case R.id.action_like_jobs:
                            showPopupWindow(view, R.menu.job_liked_menu, position);
                            break;
                        case R.id.action_applied_jobs:
                            break;
                    }
                }
            });
        }

    }

    private void showPopupWindow(View view, int menu, final int position) {
        PopupMenu popup = new PopupMenu(this.context, view);
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
                    case R.id.action_delete_job_liked:
                        JobLikedFragment.getInstance().getJobArrayList().remove(position);
                        JobLikedFragment.getInstance().getAdapter().notifyDataSetChanged();
                }
                return true;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    class JobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mItemMenu, mJobLocation, mJobSalary, mJobPostedDate, mOption;

        public JobItemViewHolder(View itemView) {
            super(itemView);

            mJobTitle = itemView.findViewById(R.id.tv_job_item_title);
            mItemMenu = itemView.findViewById(R.id.tv_job_item_menu);
            mJobSalary = itemView.findViewById(R.id.tv_job_item_salary);
            mJobLocation = itemView.findViewById(R.id.tv_job_item_location);
            mJobPostedDate = itemView.findViewById(R.id.tv_job_item_date);
            mOption = itemView.findViewById(R.id.tv_job_item_menu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
