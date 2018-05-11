package com.example.kaihuynh.part_timejob.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.Job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchJobAdapter extends RecyclerView.Adapter<SearchJobAdapter.SearchJobItemViewHolder>{

    private ArrayList<Job> mJobList;
    private Context context;
    private int layout;

    final private SearchJobAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex);
    }

    public SearchJobAdapter(Context context, int layout, ArrayList<Job> mJobList, ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;
    }

    @NonNull
    @Override
    public SearchJobItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new SearchJobItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchJobItemViewHolder holder, int position) {
        final Job job = mJobList.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(job.getTimestamp()));
        holder.mJobPostedDate.setText(getTime(Calendar.getInstance(), calendar));
        holder.mJobLocation.setText(job.getLocation());
        holder.mJobTitle.setText(job.getName());
        holder.mJobSalary.setText(job.getSalary());
        holder.mOption.setVisibility(View.GONE);
    }

    @SuppressLint("SimpleDateFormat")
    private String getTime(Calendar current, Calendar postingDate) {
        String s;
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if (minus < 2) {
            if (minus == 1) {
                s = "Hôm qua lúc " + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            } else if (minus == 0) {
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1 > 0) {
                    s = minus1 + " giờ trước";
                } else {
                    if (current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) == 0) {
                        s = "1 phút trước";
                    } else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " phút trước";
                    }
                }
            }else {
                s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
            }
        } else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    class SearchJobItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mJobLocation, mJobSalary, mJobPostedDate, mOption;

        public SearchJobItemViewHolder(View itemView) {
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
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
