package net.zhuruoling.omms.central.console;

import net.zhuruoling.omms.central.controller.ControllerManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConsoleUtil {
    public static @NotNull List<String> parseControllerArgument(@NotNull String in){
        var res = kotlin.collections.CollectionsKt.<String>mutableListOf();
        if (Objects.equals(in, "all")){
            res.addAll(ControllerManager.INSTANCE.getControllers().keySet());
            return res;
        }
        var t = in.replace(" ","");
        if (t.contains(",")){
            res.addAll(Arrays.stream(t.split(",")).toList());
        }else{
            res.add(in);
        }
        return res;
    }
}
