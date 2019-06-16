package de.uni_due.paluno.chuj.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("MsgType")
    @Expose
    private Integer msgType;
    @SerializedName("Info")
    @Expose
    private String info;
    @SerializedName("Data ")
    @Expose
    private Data data;

    public Integer getMsgType() {
        return msgType;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public MessageResponse(Integer msgType, String info, Data data) {
        this.msgType = msgType;
        this.info = info;
        this.data = data;
    }
}