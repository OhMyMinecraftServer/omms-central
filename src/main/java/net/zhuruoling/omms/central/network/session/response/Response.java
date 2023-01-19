package net.zhuruoling.omms.central.network.session.response;

import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Response {
    @NotNull
    private Result responseCode;
    private HashMap<String, String> content = new HashMap<>();

    public Response(@NotNull Result result, HashMap<String, String> content) {
        this.responseCode = result;
        this.content = content;
    }

    public Response() {
        this.responseCode = Result.OK;
    }

    public static String serialize(Response response) {
        return new GsonBuilder().serializeNulls().create().toJson(response);
    }

    public static Response deserialize(String x) {
        return new GsonBuilder().serializeNulls().create().fromJson(x, Response.class);
    }

    public @NotNull Result getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(@NotNull Result responseCode) {
        this.responseCode = responseCode;
    }

    public HashMap<String, String> getContent() {
        return content;
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    }

    public Response withResponseCode(Result code){
        setResponseCode(code);
        return this;
    }

    public Response withContentPair(String a, String b) {
        content.put(a, b);
        return this;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code='" + responseCode + '\'' +
                ", content=" + content +
                '}';
    }
}
