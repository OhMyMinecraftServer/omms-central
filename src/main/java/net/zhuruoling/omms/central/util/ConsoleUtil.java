package net.zhuruoling.omms.central.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConsoleUtil {
    public static @NotNull List<String> buildGridPrint(@NotNull Column column){
        ArrayList<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        Set<Row> keySet = column.rowIntegerTreeMap.keySet();
        keySet.forEach(row -> {
            
        });
        return result;
    }

}
/*
+-----------+--------+--------+--------+
| something | thing1 | thing2 | thing3 |
+-----------+--------+--------+--------+
| id        | 1      | 2      | 3      |
+-----------+--------+--------+--------+

 */