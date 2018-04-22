package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;

/**
 * Created by Kai on 2018-02-08.
 */

public class ForeignLanguage implements Serializable{
    private String name;
    private boolean isChecked;

    public ForeignLanguage(){

    }

    public ForeignLanguage(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
