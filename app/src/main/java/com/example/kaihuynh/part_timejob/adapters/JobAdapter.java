package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.Job;

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


    public interface ListItemClickListener{
        void onListItemClick(int clickItemIndex);
    }

    public JobAdapter(Context context, int layout, ArrayList<Job> mJobList, ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener =listener;
        this.layout = layout;
    }

    @Override
    public JobItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new JobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(JobItemViewHolder holder, int position) {
        Job job = mJobList.get(position);
        holder.mJobPostedDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(job.getPostedDate()));
        holder.mJobLocation.setText(job.getLocation());
        holder.mJobTitle.setText(job.getName());
        holder.mJobSalary.setText(job.getSalary());

        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    class JobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mJobTitle, mItemMenu, mJobLocation, mJobSalary, mJobPostedDate;
        ImageButton mLikeButton;

        public JobItemViewHolder(View itemView) {
            super(itemView);

            mLikeButton = itemView.findViewById(R.id.btn_like_item);
            mJobTitle = itemView.findViewById(R.id.tv_job_item_title);
            mItemMenu = itemView.findViewById(R.id.tv_job_item_menu);
            mJobSalary = itemView.findViewById(R.id.tv_job_item_salary);
            mJobLocation = itemView.findViewById(R.id.tv_job_item_location);
            mJobPostedDate = itemView.findViewById(R.id.tv_job_item_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
