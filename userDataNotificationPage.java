package com.example.bestbrofinderlogin;

public class userDataNotificationPage {

    String phoneNumber;
    String firstName;
    String imageUrl;

    public userDataNotificationPage(String phoneNumber, String firstName, String imageUrl) {
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.imageUrl = imageUrl;

    }

    @Override
    public String toString() {
        return "userDataNotificationPage{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getImageUrl() {return imageUrl;}

    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
}
