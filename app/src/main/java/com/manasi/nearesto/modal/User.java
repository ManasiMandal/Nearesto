package com.manasi.nearesto.modal;

public class User {

    String name, email, phone, password, profile_url;

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile_url = "https://firebasestorage.googleapis.com/v0/b/nearesto.appspot.com/o/nearesto%2Fdefault_profile.png?alt=media&token=dc6b1f76-e1ed-4023-9852-279fd13c4611";
    }

    public User(String name, String email, String password, String profile_url) {
        this(name, email, password);
        this.profile_url = profile_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
}

