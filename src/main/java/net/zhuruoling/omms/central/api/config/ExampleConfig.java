package net.zhuruoling.omms.central.api.config;

import net.zhuruoling.omms.central.api.config.annotation.Exclude;
import net.zhuruoling.omms.central.api.config.annotation.SerializedName;
import org.jetbrains.annotations.NotNull;

public class ExampleConfig {
    @NotNull String name = "what";
    int level = 114514;
    @SerializedName("exp")
    int experience = 0;

    @NotNull InnerClass inner = new InnerClass("114514", "1919810");

    @Exclude(serialize = true, deserialize = true)
    @NotNull
    //this will be excluded
    InnerClass excludeInner = new InnerClass("114514", "1919810");


    public static class InnerClass {
        @SerializedName("val")
        String value;
        String id;

        public InnerClass(String value, String id) {
            this.value = value;
            this.id = id;
        }
    }
}
/*
Serialize:
name=what
level=114514
exp=0
inner.val=114514
inner.id=1919810
 */
