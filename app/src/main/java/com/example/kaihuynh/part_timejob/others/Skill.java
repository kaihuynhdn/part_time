package com.example.kaihuynh.part_timejob.others;

/**
 * Created by Kai on 2018-02-08.
 */

public class Skill {
    private String name;
    private boolean isChecked;

    public Skill(){

    }

    public Skill(String name, boolean isChecked) {
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
