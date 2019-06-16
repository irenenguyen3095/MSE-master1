package de.uni_due.paluno.chuj.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetMessagesAntwort {

    @SerializedName("MsgType")
    @Expose
    private Integer msgType;
    @SerializedName("Info")
    @Expose
    private String info;
    @SerializedName("Data")
    @Expose
    private List<Datum> data = null;

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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public GetMessagesAntwort(Integer msgType, String info, List<Datum> data) {
        this.msgType = msgType;
        this.info = info;
        this.data = data;
    }
}
