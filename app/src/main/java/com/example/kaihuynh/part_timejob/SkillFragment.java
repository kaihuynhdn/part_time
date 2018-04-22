package com.example.kaihuynh.part_timejob;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.example.kaihuynh.part_timejob.adapters.SkillAdapter;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.example.kaihuynh.part_timejob.others.NonScrollListView;
import com.example.kaihuynh.part_timejob.models.Skill;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SkillFragment extends Fragment {
    private float dpi;
    private CustomViewPager mViewPager;
    private Button mPreviousButton, mDoneButton;
    private NonScrollListView mSkillListView;
    private ArrayList<Skill> mSkills;
    private SkillAdapter mSkillAdapter;

    private static SkillFragment sInstance = null;

    public SkillFragment() {
        // Required empty public constructor
        sInstance = this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skill, container, false);

        addComponents(view);
        init();
        addEvents();

        return view;
    }

    private void addComponents(View view) {
        mDoneButton = view.findViewById(R.id.btn_next_skill);
        mPreviousButton = view.findViewById(R.id.btn_previous_skill);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
        mSkillListView = view.findViewById(R.id.lv_skill);
    }

    private void init() {
        dpi = getContext().getResources().getDisplayMetrics().density;
        mSkills = new ArrayList<>();

        String[] skillList = getResources().getStringArray(R.array.skill_array);
        mSkills.add(new Skill(skillList[0], true));
        for(int i = 1; i<skillList.length; i++){
            mSkills.add(new Skill(skillList[i], false));
        }

        mSkillAdapter = new SkillAdapter(getContext(), R.layout.skill_item, mSkills);
        mSkillListView.setAdapter(mSkillAdapter);
    }

    private void addEvents() {
        listViewEvents();
        previousButtonEvents();
        nextButtonEvents();
    }

    private void nextButtonEvents() {
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);

            }
        });
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
    }

    private void listViewEvents() {
        mSkillListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mSkills.get(i).isChecked()&& i != 0){
                    mSkills.get(i).setChecked(false);
                }else {
                    if (i == 0) {
                        for (Skill s : mSkills) {
                            s.setChecked(false);
                        }
                    } else {
                        mSkills.get(0).setChecked(false);
                    }
                    if(i!=mSkills.size()-1){
                        mSkills.get(i).setChecked(true);
                    }else{
                        showAddSkillDialog();
                        mSkillListView.setSelection(mSkills.size()-2);
                    }
                }
                mSkillAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddSkillDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm kĩ năng...");
        final EditText editText = new EditText(getContext());
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().toString()!="" && editText.getText().toString()!= null){
                    mSkills.add(mSkills.size()-1, new Skill(editText.getText().toString(), true));
                    mSkillAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int)(19*dpi), (int)(10*dpi), (int)(14*dpi), (int)(5*dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static SkillFragment getInstance(){
        if(sInstance == null){
            sInstance = new SkillFragment();
        }
        return sInstance;
    }

    public String getSkills(){
        String s = "";
        for (Skill f : mSkills){
            if(f.isChecked() && !f.getName().equals("Không")){
                s+=f.getName()+"\n";
            }
        }
        return s == "" ? "" : s.substring(0, s.length() - 1);
    }
}
