package com.example.bestbrofinderlogin;

import java.util.ArrayList;

/*
    This class is responsible for parsing user data for easy access and use
    I would say don't touch this class without talking to tommy first
    Firestore as specific requirements for uploading classes and I don't want something to get screwed up.
    Like you can probably add methods and it would be fine but definitely don't
    add variables without discussing first.
 */
public class UserInformation {
    private String firstName;
    private String lastName;
    private String location;
    private String schedule;
    private String gender;
    private String bio;
    private String phoneNumber;




    private ArrayList<String> currentMatches;
    private int age;
    private String imageRef;
    public UserInformation(){

    }





    public UserInformation(String firstName, String lastName, String location, String schedule, String gender, String bio, int age, String phoneNumber, ArrayList<String> currentMatches, String imageRef) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.schedule = schedule;
        this.gender = gender;
        this.age = age;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
        this.currentMatches = currentMatches;
        this.imageRef = imageRef;
    }
    public ArrayList<String> getCurrentMatches() {
        return currentMatches;
    }
    public void addCurrentMatches(String id){currentMatches.add(id);}
    public String getPhoneNumber(){ return phoneNumber; }

    public void setPhoneNumber(String phoneNumber){ this.phoneNumber = phoneNumber; }
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserInformation{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", location='" + location + '\'' +
                ", schedule='" + schedule + '\'' +
                ", gender='" + gender + '\'' +
                ", bio='" + bio + '\'' +
                ", age=" + age + '\''+
                ", imageRef"+ imageRef+
                '}';
    }

    public String getFirstLast(){
        return firstName + " " + lastName;
    }
    public String getBioStuff(){
        return  "Location: " + location +
                "\nSchedule: " + schedule +
                "\nAge: " + age +
                "\tGender: " + gender +
                "\nBio: " + bio;

    }
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
}

