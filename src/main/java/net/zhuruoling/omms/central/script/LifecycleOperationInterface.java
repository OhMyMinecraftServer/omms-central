package net.zhuruoling.omms.central.script;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.zhuruoling.omms.central.command.CommandManager;
import net.zhuruoling.omms.central.command.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class LifecycleOperationInterface extends StageOperationInterface {


    public LifecycleOperationInterface(String name) {
        super(null, name);
    }

    public void registerCommand(@NotNull String literalName, @NotNull Consumer<List<String>> consumer) {
        var node = LiteralArgumentBuilder.<CommandSourceStack>literal(literalName).then(
                RequiredArgumentBuilder.<CommandSourceStack, String>argument("params", greedyString()).executes(commandContext -> {
                    var p = Arrays.stream(StringArgumentType.getString(commandContext, "params").split(" ")).toList();
                    consumer.accept(p);
                    return 0;
                })
        );
        CommandManager.INSTANCE.registerScriptCommand(getPluginName(),node);
    }

    public void registerCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> commandSourceStackLiteralArgumentBuilder) {
        CommandManager.INSTANCE.registerScriptCommand(getPluginName(), commandSourceStackLiteralArgumentBuilder);
    }

    public ScriptMain require(@NotNull String id) throws ScriptNotExistException, ScriptNotLoadedException {
        return ScriptManager.INSTANCE.getPluginInstance(id).getInstance();
    }


}