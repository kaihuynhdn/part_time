package com.example.kaihuynh.part_timejob.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
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
import com.example.kaihuynh.part_timejob.others.CircleTransform;
import com.squareup.picasso.Picasso;

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
        Picasso.get().load(notification.getAvatarSender()).transform(new CircleTransform()).placeholder(R.drawable.loading_img).into(holder.imageView);

        if (notification.getStatus() == Notification.STATUS_NOT_SEEN){
            holder.mCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.notificationUnSeen));
        }else if (notification.getStatus() == Notification.STATUS_SEEN){
            holder.mCardView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
    }


    @SuppressLint("SimpleDateFormat")
    private String getTime(Calendar current, Calendar postingDate){
        String s;
        int minus = current.get(Calendar.DAY_OF_MONTH) - postingDate.get(Calendar.DAY_OF_MONTH);
        if(minus<2){
            if (minus == 1){
                s = context.getResources().getString(R.string.get_time_text_1) + " "
                        + new SimpleDateFormat("hh:mm").format(postingDate.getTime());
            }else if(minus == 0){
                int minus1 = current.get(Calendar.HOUR_OF_DAY) - postingDate.get(Calendar.HOUR_OF_DAY);
                if (minus1>0){
                    s = minus1 + " " + context.getResources().getString(R.string.get_time_text_2);
                }else {
                    if(current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE)==0){
                        s = context.getResources().getString(R.string.get_time_text_3);
                    }else {
                        s = current.get(Calendar.MINUTE) - postingDate.get(Calendar.MINUTE) + " "
                                + context.getResources().getString(R.string.get_time_text_4);
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
        return mNotificationList.size();
    }

    class NotificationItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mContent, mTime;
        ImageView imageView;
        CardView mCardView;

        public NotificationItemViewHolder(View itemView) {
            super(itemView);

            mContent = itemView.findViewById(R.id.tv_content_notification_item);
            mTime = itemView.findViewById(R.id.tv_time_notification_item);
            imageView = itemView.findViewById(R.id.img_notification_item);
            mCardView = itemView.findViewById(R.id.cardview_notification_item);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
