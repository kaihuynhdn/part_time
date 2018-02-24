package com.example.kaihuynh.part_timejob;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.others.ForeignLanguage;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForeignLanguageFragment extends Fragment {

    private ListView mForeignLanguageListView;
    private ArrayList<ForeignLanguage> mForeignLanguages;
    private ForeignLanguageAdapter mForeignLanguageAdapter;

    public ForeignLanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_foreign_language, container, false);

        addComponents(view);

        return view;
    }

    private void addComponents(View view) {
        mForeignLanguageListView = view.findViewById(R.id.lv_foreign_language);
        String[] foreignList = getResources().getStringArray(R.array.foreign_language);
        mForeignLanguages = new ArrayList<>();

        for(String s : foreignList){
            mForeignLanguages.add(new ForeignLanguage(s, false));
        }

        mForeignLanguageAdapter = new ForeignLanguageAdapter(getContext(), R.layout.foreign_language_item, mForeignLanguages);
        mForeignLanguageListView.setAdapter(mForeignLanguageAdapter);
    }

}
