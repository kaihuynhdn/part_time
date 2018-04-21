package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.JobLikedFragment;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.controllers.UserManager;
import com.example.kaihuynh.part_timejob.interfaces.LoadMore;
import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Job> mJobList;
    private Context context;
    private int layout;

    private final int VIEW_TYPE_LOADING = 1, VIEW_TYPE_ITEM = 0;
    boolean isLoading;
    int visibleThreshold = 4;
    int lastVisibleItem, totalItemCount;
    LoadMore loadMore;

    final private MyAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex);
    }


    public MyAdapter(RecyclerView recyclerView, Context context, int layout, ArrayList<Job> mJobList, MyAdapter.ListItemClickListener listener) {
        this.mJobList = mJobList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return mJobList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(LoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new MyAdapter.ItemViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_loading_item, parent, false);
            return new MyAdapter.LoadingViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Job job = mJobList.get(position);
            Calendar current = Calendar.getInstance();
            Date date = new Date(job.getTimestamp());
            Calendar postingDate = Calendar.getInstance();
            postingDate.setTime(date);
            itemViewHolder.mJobPostedDate.setText(getTime(current, postingDate));
            itemViewHolder.mJobLocation.setText(job.getLocation());
            itemViewHolder.mJobTitle.setText(job.getName());
            itemViewHolder.mJobSalary.setText(job.getSalary());
            if (UserManager.getInstance().isLikeJob(job.getId())){
                itemViewHolder.imgLike.setImageResource(R.drawable.liked);
            }else {
                itemViewHolder.imgLike.setImageResource(R.drawable.like);
            }

            itemViewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemViewHolder.imgLike.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.liked).getConstantState())){
                        UserManager.getInstance().removeFavouriteJob(job.getId());
                        itemViewHolder.imgLike.setImageResource(R.drawable.like);

                    }else if (itemViewHolder.imgLike.getDrawable().getConstantState().equals(ContextCompat.getDrawable(context, R.drawable.like).getConstantState())){
                        itemViewHolder.imgLike.setImageResource(R.drawable.liked);
                        User u = UserManager.getInstance().getUser();
                        ArrayList<Job> arrayList = new ArrayList<>();
                        Job j = job;
                        j.setCandidateList(new ArrayList<Candidate>());
                        j.setRecruiter(null);
                        arrayList.add(j);
                        if (u.getFavouriteJobList() != null) {
                            arrayList.addAll(u.getFavouriteJobList());
                        }
                        u.setFavouriteJobList(arrayList);
                        UserManager.getInstance().updateUser(u);
                    }

                    JobLikedFragment.getInstance().refreshData();
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder viewHolder = (LoadingViewHolder) holder;
            viewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    private String getTime(Calendar current, Calendar postingDate) {
        String s = "";
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mItemMenu, mJobLocation, mJobSalary, mJobPostedDate;
        ImageView imgLike;

        public ItemViewHolder(View itemView) {
            super(itemView);

            mJobTitle = itemView.findViewById(R.id.tv_job_item_title);
            mItemMenu = itemView.findViewById(R.id.tv_job_item_menu);
            mJobSalary = itemView.findViewById(R.id.tv_job_item_salary);
            mJobLocation = itemView.findViewById(R.id.tv_job_item_location);
            mJobPostedDate = itemView.findViewById(R.id.tv_job_item_date);
            imgLike = itemView.findViewById(R.id.image_like);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pb_load_more);
        }
    }
}
