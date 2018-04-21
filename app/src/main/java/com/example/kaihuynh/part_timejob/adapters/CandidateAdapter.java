package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.models.Candidate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kai on 2018-03-10.
 */

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateItemViewHolder>{

    private ArrayList<Candidate> mCandidateList;
    private Context context;
    private int layout;

    final private ListItemClickListener mOnClickListener;


    public interface ListItemClickListener {
        void onListItemClick(int clickItemIndex, ArrayList<Candidate> mCandidates);
    }

    public CandidateAdapter(Context context, int layout, ArrayList<Candidate> mCandidateList, ListItemClickListener listener){
        this.mCandidateList = mCandidateList;
        this.context = context;
        this.mOnClickListener = listener;
        this.layout = layout;
    }

    @Override
    public CandidateItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new CandidateItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CandidateItemViewHolder holder, int position) {
        Candidate candidate = mCandidateList.get(position);
        holder.mName.setText(candidate.getUser().getFullName());
        holder.mEmail.setText(candidate.getUser().getEmail());
        holder.mPhoneNumber.setText(candidate.getUser().getPhoneNumber());
        holder.mGender.setText(candidate.getUser().getGender());
        Date date = new Date(mCandidateList.get(position).getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        holder.mDate.setText(getTime(Calendar.getInstance(), calendar));
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
        return mCandidateList.size();
    }

    class CandidateItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mName, mEmail, mPhoneNumber, mGender, mDate;

        public CandidateItemViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.tv_candidate_name);
            mEmail = itemView.findViewById(R.id.tv_candidate_email);
            mPhoneNumber = itemView.findViewById(R.id.tv_candidate_phone);
            mGender = itemView.findViewById(R.id.tv_candidate_gender);
            mDate = itemView.findViewById(R.id.tv_candidate_applied);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition, mCandidateList);
        }
    }
}
