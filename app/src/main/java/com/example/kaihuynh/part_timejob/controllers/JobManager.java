package com.example.kaihuynh.part_timejob.controllers;

import com.example.kaihuynh.part_timejob.models.Candidate;
import com.example.kaihuynh.part_timejob.models.Job;
import com.example.kaihuynh.part_timejob.models.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class JobManager {
    private static JobManager sInstance = null;
    private ArrayList<Job> mJobList;

    private JobManager(){
        this.mJobList = new ArrayList<>();
    }

    public void load(){
        for (int i = 0; i<10; i++){
            Job job = new Job();
            job.setId(i);
            job.setName("");
            job.setPostedDate(new Date());
            job.setBenefits("");
            job.setDescription("");
            job.setSalary("");
            job.setLocation("");
            job.setRequirement("");
            job.setStatus("");

            User u = new User();
            u.setFullName("");
            u.setAddress("");
            u.setEducation("");
            u.setDayOfBirth(new Date());
            u.setForeignLanguages("");
            u.setPhoneNumber("");
            u.setSkills("");
            u.setEmail("");
            u.setPassword("");

            job.setRecruiter(u);

            job.setSalary("");
            job.setName("");

            ArrayList<Candidate> mCandidateList = new ArrayList<>();

            for(int j = 0; j<5; j++){
                User user = new User();
                user.setFullName("");
                user.setGender("");
                user.setAddress("");
                user.setEducation("");
                user.setDayOfBirth(new Date());
                user.setForeignLanguages("");
                user.setPhoneNumber("");
                user.setSkills("");
                user.setPersonalDescription("");
                user.setEmail("");


                mCandidateList.add(new Candidate(user, "", new Date(),""));
            }

            job.setCandidateList(mCandidateList);

            mJobList.add(job);
        }
    }

    public ArrayList<Job> getJobs(){
        return this.mJobList;
    }

    public Job getJobByRecruiterId(int id){
        for(Job j : mJobList){
            if(j.getId() == id){
                return j;
            }
        }

        return null;
    }


    public ArrayList<Candidate> getCandidatesById(int id){
        for(Job j : mJobList){
            if(j.getId() == id){
                return j.getCandidateList();
            }
        }

        return null;
    }

    public static JobManager getInstance(){
        if(sInstance == null){
            sInstance = new JobManager();
        }

        return sInstance;
    }
}
