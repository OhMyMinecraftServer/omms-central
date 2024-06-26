
package icu.takeneko.omms.central.network.session.response;

import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private @NotNull Result responseCode;
    private Map<String, String> content;

    public Response(@NotNull Result result, Map<String, String> content) {
        this.responseCode = result;
        this.content = content;
    }

    public Response() {
        super();
        this.content = new HashMap<>();
        this.responseCode = Result.OK;
    }

    public static String serialize(Response response) {
        return (new GsonBuilder()).serializeNulls().create().toJson(response);
    }

    public static Response deserialize(String x) {
        return (new GsonBuilder()).serializeNulls().create().fromJson(x, Response.class);
    }

    public @NotNull Result getResponseCode() {
        return this.responseCode;
    }

    public void setResponseCode(@NotNull Result responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, String> getContent() {
        return this.content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }

    public @NotNull Response withResponseCode(@NotNull Result code) {
        this.setResponseCode(code);
        return this;
    }

    public @NotNull Response withContentPair(String a, String b) {
        this.content.put(a, b);
        return this;
    }

    public @NotNull String toString() {

        return "Response{code='" + this.responseCode + "', content=" + this.content + "}";
    }
}