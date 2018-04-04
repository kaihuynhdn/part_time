package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.HomePageActivity;
import com.example.kaihuynh.part_timejob.JobLikedFragment;
import com.example.kaihuynh.part_timejob.ListRecruitmentActivity;
import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.interfaces.LoadMore;
import com.example.kaihuynh.part_timejob.models.Job;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
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
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            Job job = mJobList.get(position);
            String s = "";
            Date date = new Date();
            date.setTime(job.getTimestamp());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if(calendar.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR)){
                s = calendar.get(Calendar.DAY_OF_MONTH) + " tháng " + (calendar.get(Calendar.MONTH)+1) + " năm " + calendar.get(Calendar.YEAR)
                        + " lúc " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            }else {
                s = calendar.get(Calendar.DAY_OF_MONTH) + " tháng " + (calendar.get(Calendar.MONTH)+1)
                        + " lúc " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
            }
            itemViewHolder.mJobPostedDate.setText(s);
            itemViewHolder.mJobLocation.setText(job.getLocation());
            itemViewHolder.mJobTitle.setText(job.getName());
            itemViewHolder.mJobSalary.setText(job.getSalary());

            if (HomePageActivity.getInstance().getBottomNavigation().getSelectedItemId() == R.id.action_applied_jobs) {
                itemViewHolder.mOption.setVisibility(View.GONE);
            } else if (ListRecruitmentActivity.getInstance() == context) {
                itemViewHolder.mOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupWindow(view, R.menu.list_recruitment_menu, position);
                    }
                });
            } else {
                itemViewHolder.mOption.setOnClickListener(new View.OnClickListener() {
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
        }else  if (holder instanceof LoadingViewHolder){
            LoadingViewHolder viewHolder = (LoadingViewHolder) holder;
            viewHolder.progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded(){
        isLoading = false;
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

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mJobTitle, mItemMenu, mJobLocation, mJobSalary, mJobPostedDate, mOption;

        public ItemViewHolder(View itemView) {
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

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pb_load_more);
        }
    }
}
