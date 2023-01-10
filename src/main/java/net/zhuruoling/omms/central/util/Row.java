package net.zhuruoling.omms.central.util;

import java.util.List;

public class Row {
    int maxWidth = 0;
    List<String> contents;
    public Row(String... contents){
        this.contents = List.of(contents);
        this.contents.forEach(s -> {
            maxWidth = Math.max(s.length(), maxWidth);
        });
    }
}
