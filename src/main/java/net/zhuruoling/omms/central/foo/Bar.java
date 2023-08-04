package net.zhuruoling.omms.central.foo;


import net.zhuruoling.omms.central.command.BuiltinCommand;
import net.zhuruoling.omms.central.command.CommandManager;
import net.zhuruoling.omms.central.command.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class Bar {
    public static void main(String[] args) {
        CommandManager.INSTANCE.init();
        BuiltinCommand.registerBuiltinCommand(CommandManager.INSTANCE.getCommandDispatcher());
        var dispatcher = CommandManager.INSTANCE.getCommandDispatcher();
        var command = "permissio";
        var src = new CommandSourceStack(CommandSourceStack.Source.CONSOLE);
        var result = dispatcher.parse(command, src);
        dispatcher.getCompletionSuggestions(result).thenAccept(suggestions -> {
            suggestions.getList().forEach(suggestion -> {
                System.out.println(suggestion.toString());
            });
        });
        for (String s : dispatcher.getAllUsage(dispatcher.getRoot(), src, false)) {
            System.out.println(s);
        }

    }

}
