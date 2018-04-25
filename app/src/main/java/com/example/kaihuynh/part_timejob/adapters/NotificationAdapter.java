package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.Notification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationItemViewHolder> {

    private ArrayList<Notification> mNotificationList;
    private Context context;
    private int layout;

    final private NotificationAdapter.ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex);
    }

    public NotificationAdapter(Context context, int layout, ArrayList<Notification> mNotificationList, NotificationAdapter.ListItemClickListener listener) {
        this.mNotificationList = mNotificationList;
        this.context = context;
        this.layout = layout;
        this.mOnClickListener = listener;
    }

    @NonNull
    @Override
    public NotificationItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new NotificationItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationItemViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);

        String s = notification.getContent();
        SpannableStringBuilder sb = new SpannableStringBuilder(s);
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, s.indexOf("đã"), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.BOLD), s.indexOf("công việc") +9, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        holder.mContent.setText(sb);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(notification.getDate()));
        holder.mTime.setText(getTime(Calendar.getInstance(), calendar));
    }


    private String getTime(Calendar current, Calendar postingDate){
        String s = "";
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if(minus<2){
            if (minus == 1){
                s = "Hôm qua lúc " + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            }else if(minus == 0){
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1>0){
                    s = minus1 + " giờ trước";
                }else {
                    if(current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE)==0){
                        s = "1 phút trước";
                    }else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " phút trước";
                    }
                }
            }
        }else {
            s = new SimpleDateFormat("hh:ss dd-MM-yyy").format(postingDate.getTime());
        }

        return s;
    }


    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    class NotificationItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mContent, mTime;
        ImageView imageView;

        public NotificationItemViewHolder(View itemView) {
            super(itemView);

            mContent = itemView.findViewById(R.id.tv_content_notification_item);
            mTime = itemView.findViewById(R.id.tv_time_notification_item);
            imageView = itemView.findViewById(R.id.img_notification_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
