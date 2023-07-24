package net.zhuruoling.omms.central.plugin.metadata;

import com.google.gson.annotations.SerializedName;
import net.zhuruoling.omms.central.plugin.UtilKt;
import net.zhuruoling.omms.central.plugin.depedency.PluginDependency;
import net.zhuruoling.omms.central.plugin.exception.PluginException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor;

public class PluginDependencyRequirement {
    @SerializedName(value = "id", alternate = "pluginId")
    String id;
    @SerializedName(value = "requirement", alternate = {"require"})
    String requirement;

    private String symbol;
    private ModuleDescriptor.@Nullable Version parsedVersion;

    public void parseRequirement() {
        if (id == null) {
            throw new PluginException("No `id` provided for PluginDependencyRequirement.");
        }
        if (requirement == null) requirement = "*";
        if (requirement.equals("*")){
            symbol = "*";
            parsedVersion = null;
            return;
        }
        var matcher = UtilKt.getVersionNamePattern().matcher(requirement);
        if (!matcher.matches()) throw new IllegalStateException("Unexpected value: " + requirement);
        var match = matcher.toMatchResult();
        symbol = match.group(1);
        parsedVersion = ModuleDescriptor.Version.parse(match.group(2));
    }

    public String getId() {
        return id;
    }

    public @NotNull Boolean requirementMatches(@NotNull PluginDependency dependency) {
        return UtilKt.requirementMatches(this, dependency);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(id, requirement);
    }

    public String getSymbol() {
        return symbol;
    }

    public ModuleDescriptor.Version getParsedVersion() {
        return parsedVersion;
    }

    public String getRequirement() {
        return requirement;
    }
}
