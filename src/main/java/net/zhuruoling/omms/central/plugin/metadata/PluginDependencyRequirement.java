package net.zhuruoling.omms.central.plugin.metadata;

import kotlin.Pair;
import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency;

import java.lang.module.ModuleDescriptor;
import java.util.Comparator;

public class PluginDependencyRequirement {
    @SerializedName(value = "id", alternate = "pluginId")
    String id;
    @SerializedName(value = "requirement", alternate = {"require"})
    String requirement;

    public Pair<Comparator<ModuleDescriptor.Version>, ModuleDescriptor.Version> parseRequirement() {

        return null;
    }

    //>=  <=  <  >  ==
    public boolean satisfyRequirement(PluginDependency dependency) {
        var pair = parseRequirement();
        return pair.getFirst().compare(pair.component2(), dependency.version()) < 0;
    }

    public String getId() {
        return id;
    }
}
