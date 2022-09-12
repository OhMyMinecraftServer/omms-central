package net.zhuruoling.console;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class PluginCommand {
    private final String pluginId;
    private final LiteralArgumentBuilder<CommandSourceStack> commandNode;

    public PluginCommand(String pluginId, LiteralArgumentBuilder<CommandSourceStack> commandNode) {
        this.pluginId = pluginId;
        this.commandNode = commandNode;
    }

    public String getPluginId() {
        return pluginId;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return commandNode;
    }

}
