package net.zhuruoling.omms.central.plugin.callback;

import net.zhuruoling.omms.central.command.CommandManager;

import java.util.function.Consumer;

public class CommandRegistrationCallback extends Callback<CommandManager>{

    public static final CommandRegistrationCallback INSTANCE = new CommandRegistrationCallback();
    @Override
    public void register(Consumer<CommandManager> consumer) {
        this.consumers.add(consumer);
    }
}
