package net.zhuruoling.plugin;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.zhuruoling.console.CommandSourceStack;
import net.zhuruoling.console.ConsoleCommandHandler;
import net.zhuruoling.console.PluginCommand;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.network.session.request.Request;
import net.zhuruoling.network.session.request.RequestManager;
import net.zhuruoling.network.session.handler.PluginRequestHandler;
import net.zhuruoling.network.session.response.Response;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class LifecycleServerInterface extends ServerInterface {


    public LifecycleServerInterface(String name) {
        super(null, name);
    }

    public void registerRequestCode(@NotNull String code, @NotNull String functionName) {
        //this.getLogger().info("Registering %s -> %s".formatted(code, functionName));
        RequestManager.INSTANCE.registerPluginRequest(code, this.getPluginName(), new PluginRequestHandler(this.getPluginName(), code, functionName), false);
    }

    public void registerRequestCode(@NotNull String code, @NotNull BiFunction<RequestServerInterface, Request, Response> consumer) {
        //this.getLogger().info("Registering %s".formatted(code));
        RequestManager.INSTANCE.registerPluginRequest(code, this.getPluginName(), new PluginRequestHandler(this.getPluginName(), code, consumer), false);
    }

    public void registerCommand(@NotNull String literalName, @NotNull Consumer<List<String>> consumer) {
        var node = LiteralArgumentBuilder.<CommandSourceStack>literal(literalName).then(
                RequiredArgumentBuilder.<CommandSourceStack, String>argument("params", greedyString()).executes(commandContext -> {
                    var p = Arrays.stream(StringArgumentType.getString(commandContext, "params").split(" ")).toList();
                    consumer.accept(p);
                    return 0;
                })
        );
        ConsoleCommandHandler.getDispatcher().register(node);
        RuntimeConstants.pluginCommandHashMap.add(new PluginCommand(this.getPluginName(), node));
    }

    public void registerCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> commandSourceStackLiteralArgumentBuilder) {
        ConsoleCommandHandler.getDispatcher().register(commandSourceStackLiteralArgumentBuilder);
        RuntimeConstants.pluginCommandHashMap.add(new PluginCommand(this.getPluginName(), commandSourceStackLiteralArgumentBuilder));
    }

    public PluginMain require(String id) throws PluginNotExistException,PluginNotLoadedException {
        return PluginManager.INSTANCE.getPluginInstance(id).getInstance();
    }


}
