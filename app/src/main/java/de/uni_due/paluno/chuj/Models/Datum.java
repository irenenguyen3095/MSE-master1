package de.uni_due.paluno.chuj.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("Sender")
    @Expose
    private String sender;
    @SerializedName("Recipient")
    @Expose
    private String recipient;
    @SerializedName("Mimetype")
    @Expose
    private String mimetype;
    @SerializedName("Data")
    @Expose
    private String data;
    @SerializedName("DateTime")
    @Expose
    private String dateTime;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Datum(String sender, String recipient, String mimetype, String data, String dateTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.mimetype = mimetype;
        this.data = data;
        this.dateTime = dateTime;
    }
}
