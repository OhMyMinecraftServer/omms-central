package net.zhuruoling.network.session.response;

import com.google.gson.GsonBuilder;
import net.zhuruoling.util.Result;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Response {
//no god plz no
    //这坨是给Gson看的(
    // gson can handle enums
    //allright(逃
    @NotNull
    private Result code;
    private HashMap<String, String> content = new HashMap<>();

    public Response(@NotNull Result result, HashMap<String, String> content) {
        this.code = result;
        this.content = content;
    }

    public Response() {
        this.code = Result.OK;
    }

    public static String serialize(Response response) {
        return new GsonBuilder().serializeNulls().create().toJson(response);
    }

    public static Response deserialize(String x) {
        return new GsonBuilder().serializeNulls().create().fromJson(x, Response.class);
    }

    public @NotNull Result getCode() {
        return code;
    }

    public void setCode(@NotNull Result code) {
        this.code = code;
    }

    public HashMap<String, String> getContent() {
        return content;
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    }

    public Response withResponseCode(Result code){
        setCode(code);
        return this;
    }

    public Response withContentPair(String a, String b) {
        content.put(a, b);
        return this;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code='" + code + '\'' +
                ", content=" + content +
                '}';
    }
}
