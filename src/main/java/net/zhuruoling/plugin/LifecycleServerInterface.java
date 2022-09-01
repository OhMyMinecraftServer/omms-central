package net.zhuruoling.plugin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.zhuruoling.console.CommandSourceStack;
import net.zhuruoling.console.ConsoleHandler;
import net.zhuruoling.request.Request;
import net.zhuruoling.request.RequestManager;
import net.zhuruoling.handler.PluginRequestHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class LifecycleServerInterface extends ServerInterface {


    public LifecycleServerInterface(String name) {
        super(null, name);
    }

    public void registerRequestCode(@NotNull String code, @NotNull String functionName) {
        this.getLogger().info("Registering %s -> %s".formatted(code, functionName));
        RequestManager.INSTANCE.registerRequest(code, new PluginRequestHandler(this.getPluginName(), code, functionName));
    }

    public void registerRequestCode(@NotNull String code, @NotNull BiConsumer<RequestServerInterface, Request> consumer) {
        this.getLogger().info("Registering %s".formatted(code));
        RequestManager.INSTANCE.registerRequest(code, new PluginRequestHandler(this.getPluginName(), code, consumer));
    }

    public void registerCommand(@NotNull String literalName, @NotNull Consumer<List<String>> consumer) {
        ConsoleHandler.getLiteralSimplePluginCommands().add(literalName);
        ConsoleHandler.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal(literalName).then(
                RequiredArgumentBuilder.<CommandSourceStack, String>argument("params", greedyString()).executes(commandContext -> {
                    var p = Arrays.stream(StringArgumentType.getString(commandContext, "params").split(" ")).toList();
                    consumer.accept(p);
                    return 0;
                })
        ));

    }

    public void registerCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> commandSourceStackLiteralArgumentBuilder) {
        ConsoleHandler.getDispatcher().register(commandSourceStackLiteralArgumentBuilder);
    }

}
