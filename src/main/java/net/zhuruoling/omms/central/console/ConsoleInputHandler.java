package net.zhuruoling.omms.central.console;

import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.command.CommandManager;
import net.zhuruoling.omms.central.command.CommandSourceStack;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public class ConsoleInputHandler extends Thread {
    private Terminal terminal;

    public void prepareTerminal(){
        try {
            terminal = TerminalBuilder.builder().system(true).dumb(true).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    ConsoleInputHandler() {

    }

    public static @NotNull ConsoleInputHandler INSTANCE = new ConsoleInputHandler();

    public void handle() {
        WhitelistCompleter whitelistCompleter = new WhitelistCompleter();
        PlayerNameCompleter playerNameCompleter = new PlayerNameCompleter();
        PermissionCodeCompleter permissionCodeCompleter = new PermissionCodeCompleter();
        PermissionNameCompleter permissionNameCompleter = new PermissionNameCompleter();

        var completer = new AggregateCompleter(
                new ArgumentCompleter(
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("list", "query"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("get", "add", "search"),
                        whitelistCompleter,
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("remove"),
                        whitelistCompleter,
                        playerNameCompleter,
                        NullCompleter.INSTANCE
                ),


                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("grant", "remove"),
                        permissionCodeCompleter,
                        permissionNameCompleter
                ),
                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("list", "create", "save"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("get", "delete"),
                        permissionCodeCompleter,
                        NullCompleter.INSTANCE
                ),


                new ArgumentCompleter(
                        new StringsCompleter("stop"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("broadcast"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("status"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("help"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("reload"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("controller"),
                        new StringsCompleter("execute", "status", "console"),
                        new ControllerCompleter(),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new PluginCommandCompleter(),
                        NullCompleter.INSTANCE
                )

        );
        //console complete may not work in intellij idea console
        try {
            LineReader lineReader = LineReaderBuilder.builder().history(GlobalVariable.INSTANCE.getConsoleHistory()).terminal(terminal).completer(completer).build();
            String line = lineReader.readLine();
            line = line.strip().stripIndent().stripLeading().stripTrailing();
            if (line.isEmpty()) {
                return;
            }
            CommandManager.INSTANCE.reload();
            CommandManager.INSTANCE.dispatchCommand(line, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (UserInterruptException | EndOfFileException ignored) {

        }
    }

    public Terminal getTerminal() {
        return terminal;
    }
}
