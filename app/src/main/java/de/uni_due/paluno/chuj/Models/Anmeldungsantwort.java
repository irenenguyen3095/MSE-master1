package de.uni_due.paluno.chuj.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Anmeldungsantwort {

    @SerializedName("MsgType")
    @Expose
    private Integer msgType;
    @SerializedName("Info")
    @Expose
    private String info;
    @SerializedName("Data")
    @Expose
    private List<String> data;

    public Integer getMsgType() {
        return msgType;
    }

    public Anmeldungsantwort(Integer msgType, String info, List<String> data) {
        this.msgType = msgType;
        this.info = info;
        this.data = data;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

}
