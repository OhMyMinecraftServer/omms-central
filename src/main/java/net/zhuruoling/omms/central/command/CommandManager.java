package net.zhuruoling.omms.central.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.zhuruoling.omms.central.console.BuiltinCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager {

    public static final CommandManager INSTANCE = new CommandManager();
    private final Logger logger = LoggerFactory.getLogger("CommandManager");
    private CommandDispatcher<CommandSourceStack> commandDispatcher = new CommandDispatcher<>();
    private final HashMap<String, List<LiteralArgumentBuilder<CommandSourceStack>>> pluginRegisteredCommandMap = new HashMap<>();


    public void registerPluginCommand(String pluginId, LiteralArgumentBuilder<CommandSourceStack> builder) {
        if (pluginRegisteredCommandMap.containsKey(pluginId)) {
            pluginRegisteredCommandMap.get(pluginId).add(builder);
        } else {
            var l = new ArrayList<LiteralArgumentBuilder<CommandSourceStack>>();
            l.add(builder);
            pluginRegisteredCommandMap.put(pluginId, l);
        }
        commandDispatcher.register(builder);
    }

    public void dispatchCommand(String command, CommandSourceStack commandSourceStack) {
        try {
            logger.info("%s issued a command: %s".formatted(commandSourceStack.getSource().toString(),command));
            commandDispatcher.execute(command, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (CommandSyntaxException e) {
            logger.error("Invalid Command Syntax: " + e.getLocalizedMessage());
        } catch (Throwable exception) {
            logger.error("An error occurred while dispatching command.", new RuntimeException(exception));
        }
    }

    public void reload() {
        commandDispatcher = new CommandDispatcher<>();
        pluginRegisteredCommandMap.forEach((s, literalArgumentBuilders) -> {
            literalArgumentBuilders.forEach(commandDispatcher::register);
        });
        BuiltinCommand.registerBuiltinCommand(commandDispatcher);
    }

    public void clear() {
        pluginRegisteredCommandMap.clear();
    }

    public void clearAllPluginCommand(String pluginId) {
        pluginRegisteredCommandMap.remove(pluginId);
    }

    public CommandDispatcher<CommandSourceStack> getCommandDispatcher() {
        return commandDispatcher;
    }
}
