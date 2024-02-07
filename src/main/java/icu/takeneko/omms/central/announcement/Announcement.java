
package icu.takeneko.omms.central.announcement;

import com.google.gson.GsonBuilder;
import java.util.Arrays;
import kotlinx.serialization.Serializable;
import icu.takeneko.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
public class Announcement {
    private String id;
    private long timeMillis;
    private String title;
    private String[] content;
    @Nullable
    ContentType contentType = ContentType.STRING;

    public Announcement(String id, long timeMillis, String title, String[] content, @Nullable ContentType contentType) {
        this.id = id;
        this.timeMillis = timeMillis;
        this.title = title;
        this.content = content;
        this.contentType = contentType;
    }

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
        this.id = Util.generateRandomString(16);
        this.timeMillis = System.currentTimeMillis();
        this.title = title;
        this.content = content;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeMillis() {
        return this.timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getContent() {
        return this.content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }

    public @NotNull String toString() {
        return "Announcement{id='" + this.id + "', timeMillis=" + this.timeMillis + ", title='" + this.title + "', content=" + Arrays.toString(this.content) + "}";
    }

    public String toJson() {
        return (new GsonBuilder()).serializeNulls().create().toJson(this);
    }
}
