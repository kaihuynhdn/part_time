package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class Job implements Serializable{
    private int id;
    private User recruiter;
    private String name;
    private String salary;
    private String location;
    private Date postedDate;
    private String description;
    private String requirement;
    private String benefits;
    private ArrayList<Candidate> mCandidateList;

    public Job(){

    }


    public Job(int id, User recruiter, String name, String salary, String location, Date postedDate,
               String description, String requirement, String benefits, ArrayList<Candidate> mCandidateList) {
        this.id = id;
        this.recruiter = recruiter;
        this.name = name;
        this.salary = salary;
        this.location = location;
        this.postedDate = postedDate;
        this.description = description;
        this.requirement = requirement;
        this.benefits = benefits;
        this.mCandidateList = mCandidateList;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getRecruiter() {
        return recruiter;
    }

    public void setRecruiter(User recruiter) {
        this.recruiter = recruiter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public ArrayList<Candidate> getCandidateList() {
        return mCandidateList;
    }

    public void setCandidateList(ArrayList<Candidate> mCandidateList) {
        this.mCandidateList = mCandidateList;
    }
}
