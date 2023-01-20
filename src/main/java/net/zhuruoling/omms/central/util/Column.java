package net.zhuruoling.omms.central.util;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Column {
    @NotNull HashMap<Row,Integer> rowIntegerTreeMap = new HashMap<>();
    public Column(Row @NotNull ... rows) {
        for (Row row : rows) {
            rowIntegerTreeMap.put(row,row.maxWidth);
        }

    }
}
