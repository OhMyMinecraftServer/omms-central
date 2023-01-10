package net.zhuruoling.omms.central.util;

import java.util.HashMap;

public class Column {
    HashMap<Row,Integer> rowIntegerTreeMap = new HashMap<>();
    public Column(Row... rows) {
        for (Row row : rows) {
            rowIntegerTreeMap.put(row,row.maxWidth);
        }

    }
}
