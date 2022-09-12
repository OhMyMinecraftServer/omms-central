package net.zhuruoling.network.session.request;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Request {
    public Request(String req, String[] load){
        this.request = req;
        this.load = load;
    }
    @SerializedName("cmd")
    String request = "";
    @SerializedName("load")
    String[] load;

    public String getRequest() {
        return request;
    }

    public String[] getLoad() {
        return load;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public void setLoad(String[] load) {
        this.load = load;
    }

    @Override
    public String toString() {
        return "Request{" +
                "request='" + request + '\'' +
                ", load=" + Arrays.toString(load) +
                '}';
    }
}
