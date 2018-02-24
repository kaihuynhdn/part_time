package com.example.kaihuynh.part_timejob;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kaihuynh.part_timejob.adapters.SkillAdapter;
import com.example.kaihuynh.part_timejob.others.Skill;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SkillFragment extends Fragment {

    private ListView mSkillListView;
    private ArrayList<Skill> mSkills;
    private SkillAdapter mSkillAdapter;

    public SkillFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skill, container, false);

        addComponents(view);

        return view;
    }

    private void addComponents(View view) {
        mSkillListView = view.findViewById(R.id.lv_skill);
        mSkills = new ArrayList<>();

        String[] skillList = getResources().getStringArray(R.array.skill_array);
        for(String s : skillList){
            mSkills.add(new Skill(s, false));
        }

        mSkillAdapter = new SkillAdapter(getContext(), R.layout.skill_item, mSkills);
        mSkillListView.setAdapter(mSkillAdapter);
    }

}
