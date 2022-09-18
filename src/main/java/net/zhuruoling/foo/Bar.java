package net.zhuruoling.foo;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.zhuruoling.console.CommandSourceStack;
import net.zhuruoling.console.ConsoleHandler;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.util.List;


public class Bar {
    public static void main(String[] args) throws CommandSyntaxException {
        var logger = LoggerFactory.getLogger("main");
        ConsoleHandler.init();
        ConsoleHandler.setLogger(logger);
        new ConsoleHandler().dispatchCommand("help");

    }

    Unsafe unsafe = Unsafe.getUnsafe();
    static List<String> suggest(CommandNode<CommandSourceStack> node) {
        return null;
    }

    static void rec(CommandNode<CommandSourceStack> node) {
        if (node.getChildren().isEmpty()) {
            print(node.getName());
        } else {
            //noinspection StatementWithEmptyBody
            if(node instanceof ArgumentCommandNode<?,?>){

            }
            print(node.getName());
            node.getChildren().forEach(Bar::rec);
        }
    }

    static void print(String content) {
        System.out.println(content);
    }
}
