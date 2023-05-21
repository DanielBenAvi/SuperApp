package superapp.miniapps.datingMiniApp;

import superapp.miniapps.Gender;

import java.util.ArrayList;
import java.util.List;


public class PublicDatingProfile {

    private String nickName;
    private Gender gender;
    private int age; // calculate by dob in private dating profile
    private String bio;
    private List<Gender> sexOrientation;
    private List<String> pictures;

    //Optional attr : Personal details(color eyes, hair… ), zodiac, education, etc.

    public PublicDatingProfile() {

        this.sexOrientation = new ArrayList<>();
        this.pictures = new ArrayList<>();
    }

    public String getNickName() {
        return nickName;
    }

    public PublicDatingProfile setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public PublicDatingProfile setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public int getAge() {
        return age;
    }

    public PublicDatingProfile setAge(int age) {
        this.age = age;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public PublicDatingProfile setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public List<Gender> getSexOrientation() {
        return sexOrientation;
    }

    public PublicDatingProfile setSexOrientation(List<Gender> sexOrientation) {
        this.sexOrientation = sexOrientation;
        return this;
    }


    public List<String> getPictures() {
        return pictures;
    }

    public PublicDatingProfile setPictures(List<String> pictures) {
        this.pictures = pictures;
        return this;
    }

    @Override
    public String toString() {
        return "PublicDatingProfile{" +
                "nickName='" + nickName + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", bio='" + bio + '\'' +
                ", sexOrientation=" + sexOrientation +
                ", pictures=" + pictures +
                '}';
    }
}

