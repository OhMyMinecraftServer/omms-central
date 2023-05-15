package net.zhuruoling.omms.central.plugin.metadata;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.plugin.UtilKt;
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency;

import java.lang.module.ModuleDescriptor;

public class PluginDependencyRequirement {
    @SerializedName(value = "id", alternate = "pluginId")
    String id;
    @SerializedName(value = "requirement", alternate = {"require"})
    String requirement;

    private String symbol;
    private ModuleDescriptor.Version parsedVersion;
    public void parseRequirement() {
        var matcher = UtilKt.getVersionNamePattern().matcher(requirement);
        if (!matcher.matches()) throw new IllegalStateException("Unexpected value: " + requirement);
        var match = matcher.toMatchResult();
        symbol = match.group(1);
        parsedVersion = ModuleDescriptor.Version.parse(match.group(2));
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

    public ModuleDescriptor.Version getParsedVersion() {
        return parsedVersion;
    }
}
