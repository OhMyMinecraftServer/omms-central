package net.zhuruoling.omms.central.announcement;

import com.google.gson.GsonBuilder;
import kotlinx.serialization.Serializable;
import net.zhuruoling.omms.central.util.Util;

import java.util.Arrays;

@Serializable
public class Announcement {
    private String id;
    private long timeMillis;
    private String title;
    private String[] content;

    public Announcement(String id, long timeMillis, String title, String[] content) {
        this.id = id;
        this.timeMillis = timeMillis;
        this.title = title;
        this.content = content;
    }

    public Announcement(String id, String title, String[] content) {
        this.id = id;
        this.timeMillis = System.currentTimeMillis();
        this.title = title;
        this.content = content;
    }

    public Announcement(String title, String[] content) {
        this.id = Util.randomStringGen(16);
        this.timeMillis = System.currentTimeMillis();
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id='" + id + '\'' +
                ", timeMillis=" + timeMillis +
                ", title='" + title + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
    public String toJson(){
        return new GsonBuilder().serializeNulls().create().toJson(this);
    }

}
