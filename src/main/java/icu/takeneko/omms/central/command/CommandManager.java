package icu.takeneko.omms.central.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import icu.takeneko.omms.central.command.builtin.BuiltinCommandKt;
import icu.takeneko.omms.central.plugin.callback.CommandRegistrationCallback;
import icu.takeneko.omms.central.util.Manager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager extends Manager {

    public static final CommandManager INSTANCE = new CommandManager();
    private final Logger logger = LoggerFactory.getLogger("CommandManager");
    private @NotNull CommandDispatcher<CommandSourceStack> commandDispatcher = new CommandDispatcher<>();
    private final HashMap<String, List<LiteralArgumentBuilder<CommandSourceStack>>> scriptRegisteredCommandMap = new HashMap<>();

    @Override
    public void init(){

    }

    public void registerScriptCommand(String pluginId, LiteralArgumentBuilder<CommandSourceStack> builder) {
        if (scriptRegisteredCommandMap.containsKey(pluginId)) {
            scriptRegisteredCommandMap.get(pluginId).add(builder);
        } else {
            var l = new ArrayList<LiteralArgumentBuilder<CommandSourceStack>>();
            l.add(builder);
            scriptRegisteredCommandMap.put(pluginId, l);
        }
        commandDispatcher.register(builder);
    }

    public void dispatchCommand(String command, @NotNull CommandSourceStack commandSourceStack) {
        try {
            logger.info("%s issued a command: %s".formatted(commandSourceStack.getSource().toString(), command));
            commandDispatcher.execute(command, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (CommandSyntaxException e) {
            logger.error("Invalid Command Syntax: " + e.getLocalizedMessage());
        } catch (Throwable exception) {
            logger.error("An error occurred while dispatching command.", new RuntimeException(exception));
        }
    }

    public void reload() {
        commandDispatcher = new CommandDispatcher<>();
        scriptRegisteredCommandMap.forEach((s, literalArgumentBuilders) -> {
            literalArgumentBuilders.forEach(commandDispatcher::register);
        });
        //BuiltinCommand.registerBuiltinCommand(commandDispatcher);
        BuiltinCommandKt.registerBuiltinCommand(commandDispatcher);
        CommandRegistrationCallback.INSTANCE.invokeAll(this);
    }

    public void clear() {
        scriptRegisteredCommandMap.clear();
    }

    public void clearAllPluginCommand(String pluginId) {
        scriptRegisteredCommandMap.remove(pluginId);
    }

    public @NotNull CommandDispatcher<CommandSourceStack> getCommandDispatcher() {
        return commandDispatcher;
    }
}
