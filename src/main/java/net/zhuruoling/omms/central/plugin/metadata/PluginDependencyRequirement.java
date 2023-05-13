package net.zhuruoling.omms.central.plugin.metadata;

import kotlin.Pair;
import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.plugin.UtilKt;
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency;

import java.lang.module.ModuleDescriptor;
import java.util.Comparator;
import java.util.function.Predicate;

public class PluginDependencyRequirement {
    @SerializedName(value = "id", alternate = "pluginId")
    String id;
    @SerializedName(value = "requirement", alternate = {"require"})
    String requirement;

    private String symbol;
    private ModuleDescriptor.Version version;
    public void parseRequirement() {
        var matcher = UtilKt.getVersionNamePattern().matcher(requirement);
        if (!matcher.matches()) throw new IllegalStateException("Unexpected value: " + requirement);
        var match = matcher.toMatchResult();
        symbol = match.group(1);
        version = ModuleDescriptor.Version.parse(match.group(2));
    }

    public String getId() {
        return id;
    }

    public Boolean requirementMatches(PluginDependency dependency) {
        return UtilKt.requirementMatches(this, dependency);
    }

    public String getSymbol() {
        return symbol;
    }

    public ModuleDescriptor.Version getVersion() {
        return version;
    }
}
