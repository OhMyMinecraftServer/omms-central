package net.zhuruoling.omms.central.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PluginUtils {

    public static @NotNull String getPluginIdByFileName(String fileName) {
        var pluginId = "";

        return pluginId;
    }

    public static @NotNull String getPluginFileNameById(String pluginId) {
        return "";
    }

    //SHITTY method
    public static @NotNull List<String> calculateLoadOrderByPluginDependencies(@NotNull List<PluginDependency> pluginDependencies) {
        HashMap<String, AtomicInteger> loadOrder = new HashMap<>();

        pluginDependencies.forEach(pluginDependency -> {
            pluginDependency.dependencies().forEach(dependency -> {
                if (loadOrder.containsKey(dependency.getId())) {
                    var o = loadOrder.get(dependency.getId());
                    o.addAndGet(1);
                } else {
                    loadOrder.put(dependency.getId(), new AtomicInteger(1));
                }
            });
        });
        ArrayList<Map.Entry<String, AtomicInteger>> list = new ArrayList<>(loadOrder.entrySet());
        list.sort(Comparator.<Map.Entry<String, AtomicInteger>>comparingInt(o -> o.getValue().get()).reversed());
        ArrayList<String> order = new ArrayList<>();
        list.forEach(stringAtomicIntegerEntry -> order.add(stringAtomicIntegerEntry.getKey()));
        return order;

    }
}
