package icu.takeneko.omms.central.network.session.request;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Request {

    public Request() {
    }

    public Request(String req) {
        this.request = req;
    }

    @SerializedName("request")
    String request = "";

    @SerializedName("content")
    HashMap<String, String> content = new HashMap<>();

    public String getContent(String key) {
        return content.get(key);
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }


    public @NotNull Request withContentKeyPair(String key, String pair) {
        content.put(key, pair);
        return this;
    }

    @Override
    public @NotNull String toString() {
        return "Request{" +
                "request='" + request + '\'' +
                ", content=" + content +
                '}';
    }
}
