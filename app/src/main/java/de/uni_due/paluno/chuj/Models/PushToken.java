package de.uni_due.paluno.chuj.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PushToken {

    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("PushToken")
    @Expose
    private String pushToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public PushToken(String username, String password, String pushToken) {
        this.username = username;
        this.password = password;
        this.pushToken = pushToken;
    }
}

