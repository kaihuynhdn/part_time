package com.example.kaihuynh.part_timejob.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.others.Skill;

import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-08.
 */

public class SkillAdapter extends ArrayAdapter<Skill> {

    private Context mContext;
    private int itemLayout;
    private ArrayList<Skill> mSkills;

    public SkillAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Skill> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.itemLayout = resource;
        this.mSkills = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayout, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.mSkillName = convertView.findViewById(R.id.tv_skill_name);
            viewHolder.mCheckedImage = convertView.findViewById(R.id.img_checked_skill);
            viewHolder.view = convertView.findViewById(R.id.view_skill_item);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Skill mSkill = mSkills.get(position);

        viewHolder.mSkillName.setText(mSkill.getName());
        if (mSkill.isChecked()){
            viewHolder.mCheckedImage.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mCheckedImage.setVisibility(View.INVISIBLE);
        }

        if(position == mSkills.size()-1){
            viewHolder.view.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.view.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

    static class ViewHolder{
        TextView mSkillName;
        ImageView mCheckedImage;
        View view;
    }
}
