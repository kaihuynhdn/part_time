package com.example.kaihuynh.part_timejob.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.example.kaihuynh.part_timejob.R;
import com.example.kaihuynh.part_timejob.RegisterPersonalInfoActivity;
import com.example.kaihuynh.part_timejob.adapters.ForeignLanguageAdapter;
import com.example.kaihuynh.part_timejob.others.CustomViewPager;
import com.example.kaihuynh.part_timejob.models.ForeignLanguage;
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

    @SuppressLint("StaticFieldLeak")
    private static ForeignLanguageFragment sInstance = null;

    public ForeignLanguageFragment() {
        // Required empty public constructor
        sInstance = this;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_foreign_language, container, false);

        getWidgets(view);
        initialize();
        setWidgetsListener();

        return view;
    }

    private void getWidgets(View view) {
        mNextButton = view.findViewById(R.id.btn_next_foreignLanguage);
        mPreviousButton = view.findViewById(R.id.btn_previous_foreignLanguage);
        mViewPager = RegisterPersonalInfoActivity.getInstance().findViewById(R.id.viewPage_register);
        mForeignLanguageListView = view.findViewById(R.id.lv_foreign_language);

    }

    private void initialize() {
        dpi = getContext().getResources().getDisplayMetrics().density;
        String[] foreignList = getResources().getStringArray(R.array.foreign_language);
        mForeignLanguages = new ArrayList<>();
        mForeignLanguages.add(new ForeignLanguage(foreignList[0], true));
        for (int i =1; i<foreignList.length; i++) {
            mForeignLanguages.add(new ForeignLanguage(foreignList[i], false));
        }

        mForeignLanguageAdapter = new ForeignLanguageAdapter(getContext(), R.layout.foreign_language_item, mForeignLanguages);
        mForeignLanguageListView.setAdapter(mForeignLanguageAdapter);
    }

    private void setWidgetsListener() {
        listViewEvents();
        nextButtonEvents();
        previousButtonEvents();
    }

    private void previousButtonEvents() {
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            }
        });
    }

    private void nextButtonEvents() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
        });
    }

    private void listViewEvents() {
        mForeignLanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mForeignLanguages.get(i).isChecked() && i != 0) {
                    mForeignLanguages.get(i).setChecked(false);
                } else {
                    if (i == 0) {
                        for (ForeignLanguage f : mForeignLanguages) {
                            f.setChecked(false);
                        }
                    } else {
                        mForeignLanguages.get(0).setChecked(false);
                    }
                    if (i != mForeignLanguages.size() - 1) {
                        mForeignLanguages.get(i).setChecked(true);
                    } else {
                        showAddLanguageDialog();
                    }
                }
                mForeignLanguageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.add_new_language_title));
        final EditText editText = new EditText(getContext());
        builder.setPositiveButton(getResources().getString(R.string.add_new_language_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().equals("") && editText.getText().toString() != null) {
                    mForeignLanguages.add(mForeignLanguages.size() - 1, new ForeignLanguage(editText.getText().toString(), true));
                    mForeignLanguageAdapter.notifyDataSetChanged();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.negative_btn_dialog), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(editText, (int) (19 * dpi), (int) (10 * dpi), (int) (14 * dpi), (int) (5 * dpi));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public static ForeignLanguageFragment getInstance() {
        if (sInstance == null) {
            sInstance = new ForeignLanguageFragment();
        }
        return sInstance;
    }

    public String getLanguages(){
        StringBuilder s = new StringBuilder();
        for (ForeignLanguage f : mForeignLanguages){
            if(f.isChecked() && !f.getName().equals("Không")){
                s.append(f.getName()).append("\n");
            }
        }
        return s.toString().equals("") ? "" : s.substring(0, s.length() - 1);
    }

}
