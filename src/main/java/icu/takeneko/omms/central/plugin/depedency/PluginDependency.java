package icu.takeneko.omms.central.plugin.depedency;

import java.lang.module.ModuleDescriptor;

public record PluginDependency(ModuleDescriptor.Version version, String id) {
}
