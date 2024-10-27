package icu.takeneko.omms.central.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import icu.takeneko.omms.central.command.builtin.BuiltinCommandKt;
import icu.takeneko.omms.central.plugin.callback.CommandRegistrationCallback;
import icu.takeneko.omms.central.foundation.Manager;
import icu.takeneko.omms.central.script.ScriptCommand;
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
    private final HashMap<String, List<ScriptCommand>> scriptRegisteredCommandMap = new HashMap<>();

    @Override
    public void init() {

    }

    public void registerScriptCommand(String script, ScriptCommand builder) {
        if (scriptRegisteredCommandMap.containsKey(script)) {
            scriptRegisteredCommandMap.get(script).add(builder);
        } else {
            var l = new ArrayList<ScriptCommand>();
            l.add(builder);
            scriptRegisteredCommandMap.put(script, l);
        }
        commandDispatcher.register(builder.getCommand());
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
        scriptRegisteredCommandMap.forEach((s, scriptCommands) ->
                scriptCommands.forEach(scriptCommand ->
                        commandDispatcher.register(scriptCommand.getCommand())
                )
        );
        BuiltinCommandKt.registerBuiltinCommand(commandDispatcher);
        CommandRegistrationCallback.INSTANCE.invokeAll(this);
    }

    public void clear() {
        scriptRegisteredCommandMap.clear();
    }

    public void clearAllScriptCommand(String scriptId) {
        scriptRegisteredCommandMap.remove(scriptId);
    }

    public @NotNull CommandDispatcher<CommandSourceStack> getCommandDispatcher() {
        return commandDispatcher;
    }
}
