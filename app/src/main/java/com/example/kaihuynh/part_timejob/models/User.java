package com.example.kaihuynh.part_timejob.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Kai on 2018-02-03.
 */

public class User implements Serializable{
    private String id;
    private String fullName;
    private String gender;
    private Date dayOfBirth;
    private String address;
    private String phoneNumber;
    private String skills;
    private String education;
    private String foreignLanguages;
    private String personalDescription;
    private String email;
    private String token;
    private String imageURL;
    private ArrayList<Notification> mNotificationList;
    private ArrayList<Job> mFavouriteJobList;
    private ArrayList<ApplyJob> mAppliedJobList;
    private ArrayList<Job> mRecruitmentList;

    public User(){

    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String id, String fullName, String gender, Date dayOfBirth, String address, String phoneNumber, String skills, String education, String foreignLanguages, String personalDescription, String email) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.dayOfBirth = dayOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.skills = skills;
        this.education = education;
        this.foreignLanguages = foreignLanguages;
        this.personalDescription = personalDescription;
        this.email = email;
    }

    public User(String id, String fullName, String gender, Date dayOfBirth, String address, String phoneNumber,
                String skills, String education, String foreignLanguages, String personalDescription, String email, ArrayList<Notification> mNotificationList) {
        this.id = id;
        this.fullName = fullName;
        this.gender = gender;
        this.dayOfBirth = dayOfBirth;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.skills = skills;
        this.education = education;
        this.foreignLanguages = foreignLanguages;
        this.personalDescription = personalDescription;
        this.email = email;
        this.mNotificationList = mNotificationList;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPersonalDescription() {
        return personalDescription;
    }

    public void setPersonalDescription(String personalDescription) {
        this.personalDescription = personalDescription;
    }

    public ArrayList<Job> getRecruitmentList() {
        return mRecruitmentList;
    }

    public void setRecruitmentList(ArrayList<Job> mRecruitmentList) {
        this.mRecruitmentList = mRecruitmentList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(Date dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getForeignLanguages() {
        return foreignLanguages;
    }

    public void setForeignLanguages(String foreignLanguages) {
        this.foreignLanguages = foreignLanguages;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Notification> getNotificationList() {
        return mNotificationList;
    }

    public void setNotificationList(ArrayList<Notification> mNotificationList) {
        this.mNotificationList = mNotificationList;
    }

    public ArrayList<Job> getFavouriteJobList() {
        return mFavouriteJobList;
    }

    public void setFavouriteJobList(ArrayList<Job> mFavouriteJobList) {
        this.mFavouriteJobList = mFavouriteJobList;
    }

    public ArrayList<ApplyJob> getAppliedJobList() {
        return mAppliedJobList;
    }

    public void setAppliedJobList(ArrayList<ApplyJob> mAppliedJobList) {
        this.mAppliedJobList = mAppliedJobList;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
