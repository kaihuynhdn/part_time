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
import com.example.kaihuynh.part_timejob.others.ForeignLanguage;

import java.util.ArrayList;

/**
 * Created by Kai on 2018-02-08.
 */

public class ForeignLanguageAdapter extends ArrayAdapter<ForeignLanguage> {

    private Context mContext;
    private int itemLayout;
    private ArrayList<ForeignLanguage> mForeignLanguages;

    public ForeignLanguageAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ForeignLanguage> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.itemLayout = resource;
        this.mForeignLanguages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(itemLayout, null);
            viewHolder = new ViewHolder();

            viewHolder.mForeignLanguageName = convertView.findViewById(R.id.tv_foreignLanguage);
            viewHolder.mCheckedImage = convertView.findViewById(R.id.img_checked);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ForeignLanguage mForeignLanguage = mForeignLanguages.get(position);

        viewHolder.mForeignLanguageName.setText(mForeignLanguage.getName());
        if (mForeignLanguage.isChecked()){
            viewHolder.mCheckedImage.setVisibility(View.VISIBLE);
        }else {
            viewHolder.mCheckedImage.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    static class ViewHolder{
        TextView mForeignLanguageName;
        ImageView mCheckedImage;
    }
}
