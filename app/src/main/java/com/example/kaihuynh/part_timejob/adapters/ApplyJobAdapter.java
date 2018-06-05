package com.example.kaihuynh.part_timejob.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.ApplyJob;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ApplyJobAdapter extends RecyclerView.Adapter<ApplyJobAdapter.JobItemViewHolder> {
    private ArrayList<ApplyJob> mJobList;
    private Context context;
    private int layout;

    final private ApplyJobAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex, ArrayList<ApplyJob> applyJobs);
    }


    public ApplyJobAdapter(Context context, int layout, ArrayList<ApplyJob> mJobList, ApplyJobAdapter.ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ApplyJobAdapter.JobItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ApplyJobAdapter.JobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyJobAdapter.JobItemViewHolder holder, final int position) {
        ApplyJob job = mJobList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(job.getJob().getTimestamp()));
        holder.mJobPostedDate.setText(getTime(Calendar.getInstance(), calendar));
        holder.mJobLocation.setText(job.getJob().getLocation());
        holder.mJobTitle.setText(job.getJob().getName());
        holder.mJobSalary.setText(job.getJob().getSalary());

        if (HomePageActivity.getInstance().getBottomNavigation().getSelectedItemId() == R.id.action_applied_jobs) {
            holder.mOption.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(Calendar current, Calendar postingDate){
        String s;
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if(minus<2){
            if (minus == 1){
                s = context.getResources().getString(R.string.get_time_text_1) + " "  + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            }else if(minus == 0){
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1>0){
                    s = minus1 + " " + context.getResources().getString(R.string.get_time_text_2);
                }else {
                    if(current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE)==0){
                        s = context.getResources().getString(R.string.get_time_text_3);
                    }else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE)+ " " + context.getResources().getString(R.string.get_time_text_4);
                    }
                }
            }else {
                s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
            }
        }else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    class JobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mJobLocation, mJobSalary, mJobPostedDate, mOption;

        public JobItemViewHolder(View itemView) {
            super(itemView);

            mJobTitle = itemView.findViewById(R.id.tv_job_item_title);
            mJobSalary = itemView.findViewById(R.id.tv_job_item_salary);
            mJobLocation = itemView.findViewById(R.id.tv_job_item_location);
            mJobPostedDate = itemView.findViewById(R.id.tv_job_item_date);
            mOption = itemView.findViewById(R.id.tv_job_item_menu);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, mJobList);
        }
    }
}
