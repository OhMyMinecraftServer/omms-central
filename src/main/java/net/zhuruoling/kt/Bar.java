package net.zhuruoling.kt;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.zhuruoling.console.CommandSourceStack;
import net.zhuruoling.console.ConsoleHandler;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;

public class Bar {
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger("main");
        var node = LiteralArgumentBuilder.<CommandSourceStack>literal("command")
                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("param", greedyString()).executes(commandContext -> {
                        String arg = StringArgumentType.getString(commandContext, "param");
                        logger.info("Called another test with argument %s.".formatted(arg));
                        return 0;
                }))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("wdnmd").then(
                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("parameter", greedyString()).executes(commandContext -> {
                            return 0;
                        })
                        )
                .executes(commandContext -> 0))
                .executes(commandContext -> {
                    logger.info("Called another test with no argument.");
                    return 0;
                })
                .build();
        rec(node);
        new ConsoleHandler();
        rec(ConsoleHandler.getDispatcher().getRoot());
    }

    static void rec(CommandNode<CommandSourceStack> node){
        if (node.getChildren().isEmpty()){
            print(node.getName());
        }
        else {
            print(node.getName());
            node.getChildren().forEach(Bar::rec);
        }
    }

    static void print(String content){
        System.out.println(content);
    }
}
