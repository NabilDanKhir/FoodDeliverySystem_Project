package com.example.android.g4_project;

public class Account {

    public String userName, password, date, email, displayName;

    public Account(){} // Default Constructor

    public Account(String userName, String password, String date, String email, String displayName)
    {
        this.userName = userName;
        this.password = password;
        this.date = date;
        this.email = email;
        this.displayName = displayName;
    }
}