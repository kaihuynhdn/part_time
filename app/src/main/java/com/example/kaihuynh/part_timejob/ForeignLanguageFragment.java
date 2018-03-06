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

import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.example.kaihuynh.part_timejob.others.ForeignLanguage;
import com.example.kaihuynh.part_timejob.others.NonScrollListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForeignLanguageFragment extends Fragment {
    private float dpi;
    private CustomViewPager mViewPager;
    private Button mPreviousButton, mNextButton;
    private NonScrollListView mForeignLanguageListView;
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
        init();
        addEvents();

        return view;
    }

    private void addComponents(View view) {
        mNextButton = view.findViewById(R.id.btn_next_foreignLanguage);
        mPreviousButton = view.findViewById(R.id.btn_previous_foreignLanguage);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
        mForeignLanguageListView = view.findViewById(R.id.lv_foreign_language);

    }

    private void init() {
        dpi = getContext().getResources().getDisplayMetrics().density;
        String[] foreignList = getResources().getStringArray(R.array.foreign_language);
        mForeignLanguages = new ArrayList<>();

        for(String s : foreignList){
            mForeignLanguages.add(new ForeignLanguage(s, false));
        }

        mForeignLanguageAdapter = new ForeignLanguageAdapter(getContext(), R.layout.foreign_language_item, mForeignLanguages);
        mForeignLanguageListView.setAdapter(mForeignLanguageAdapter);
    }

    private void addEvents() {
        listViewEvents();
        nextButtonEvents();
        previousButtonEvents();
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });
    }

    private void nextButtonEvents() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });
    }

    private void listViewEvents() {
        mForeignLanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mForeignLanguages.get(i).isChecked()){
                    mForeignLanguages.get(i).setChecked(false);
                }else {
                    if (i==0){
                        for(ForeignLanguage f : mForeignLanguages){
                            f.setChecked(false);
                        }
                    } else {
                        mForeignLanguages.get(0).setChecked(false);
                    }
                    if(i!= mForeignLanguages.size()-1){
                        mForeignLanguages.get(i).setChecked(true);
                    }else{
                        showAddLanguageDialog();
                    }
                }
                mForeignLanguageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm ngoại ngữ:");
        final EditText editText = new EditText(getContext());
        builder.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(editText.getText().toString()!="" && editText.getText().toString()!= null){
                    mForeignLanguages.add(mForeignLanguages.size()-1, new ForeignLanguage(editText.getText().toString(), true));
                    mForeignLanguageAdapter.notifyDataSetChanged();
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

}
