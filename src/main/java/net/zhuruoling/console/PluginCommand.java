package net.zhuruoling.console;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class PluginCommand {
    String pluginId;
    LiteralArgumentBuilder<CommandSourceStack> commandNode;
    String literalCommand;

    public PluginCommand(String pluginId, LiteralArgumentBuilder<CommandSourceStack> commandNode) {
        this.pluginId = pluginId;
        this.commandNode = commandNode;
    }

    public PluginCommand(String pluginId, String literalCommand) {
        this.pluginId = pluginId;
        this.literalCommand = literalCommand;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return commandNode;
    }

    public void setCommandNode(LiteralArgumentBuilder<CommandSourceStack> commandNode) {
        this.commandNode = commandNode;
    }

    public String getLiteralCommand() {
        return literalCommand;
    }

    public void setLiteralCommand(String literalCommand) {
        this.literalCommand = literalCommand;
    }

}
