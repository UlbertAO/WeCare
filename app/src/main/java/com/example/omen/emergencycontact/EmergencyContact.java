package com.example.omen.emergencycontact;

public class EmergencyContact {

    private String phoneNumber;
    private String name;

    public EmergencyContact() {}

    public EmergencyContact(String name, String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }


    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

}
