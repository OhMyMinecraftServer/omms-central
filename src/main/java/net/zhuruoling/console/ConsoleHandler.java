package net.zhuruoling.console;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.zhuruoling.broadcast.Broadcast;
import net.zhuruoling.kt.TryKotlin;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.permcode.PermissionManager;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.WhitelistManager;
import net.zhuruoling.whitelist.WhitelistReader;
import net.zhuruoling.whitelist.WhitelistResult;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static java.lang.System.getProperty;

public class ConsoleHandler {
    private static final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
    public static Logger logger;

    static {

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("whitelist")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("get").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("name", word()).executes(c -> {
                                            String name = getString(c, "name");
                                            var list = new WhitelistReader().getWhitelists();
                                            AtomicBoolean succeed = new AtomicBoolean(false);
                                            list.forEach(x -> {
                                                if (x.getName().equals(name) && !succeed.get()) {
                                                    logger.info(x.toString());
                                                    succeed.set(true);
                                                }
                                            });
                                            if (!succeed.get()) logger.error("Whitelist %s does not exist.".formatted(name));
                                            return 1;
                                        }
                                )
                        )
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(context -> {
                            var list = new WhitelistReader().getWhitelists();
                            ArrayList<String> arrayList = new ArrayList<>();
                            list.forEach(x -> arrayList.add(x.getName()));
                            logger.info("%d whitelists(%s) added to this server.".formatted(arrayList.size(), arrayList));
                            return 1;
                        })
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("query").then(
                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                            String player = getString(commandContext, "player");
                            var whitelists = new WhitelistReader().getWhitelists();
                            ArrayList<String> names = new ArrayList<>();
                            whitelists.forEach(x -> {
                                if (x.containsPlayer(player)) {
                                    names.add(x.getName());
                                }
                            });
                            if (names.isEmpty()) {
                                logger.info("Player %s does not exist in any whitelists.".formatted(player));
                                return 1;
                            }
                            logger.info("Player %s exists in whitelist:%s.".formatted(player, names));
                            return 1;
                        })
                ))
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("add").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                                                    String whitelist = getString(commandContext, "whitelist");
                                                    String player = getString(commandContext, "player");
                                                    var result = WhitelistManager.addToWhiteList(whitelist, player);
                                                    if (result.equals(WhitelistResult.OK)) {
                                                        logger.info("Successfully added %s to %s".formatted(player, whitelist));
                                                        return 0;
                                                    }
                                                    logger.error("Cannot add %s to %s,reason:%s".formatted(player, whitelist, result));
                                                    return 1;
                                                }
                                        )
                                )
                        )

                )
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("remove").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                                                    String whitelist = getString(commandContext, "whitelist");
                                                    String player = getString(commandContext, "player");
                                                    var result = WhitelistManager.removeFromWhiteList(whitelist, player);
                                                    if (result.equals(WhitelistResult.OK)) {
                                                        logger.info("Successfully removed %s from %s".formatted(player, whitelist));
                                                        return 0;
                                                    }
                                                    logger.error("Cannot remove %s from %s,reason:%s".formatted(player, whitelist, result));
                                                    return 1;
                                                }
                                        )
                                )
                        )

                )
        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("broadcast")
                .then(
                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("text", greedyString()).executes(
                                commandContext -> {
                                    String text = getString(commandContext, "text");
                                    logger.info("Sending message:" + text);
                                    Broadcast broadcast = new Broadcast();
                                    broadcast.setChannel("GLOBAL");
                                    broadcast.setContent(text);
                                    broadcast.setPlayer(Util.randomStringGen(8));
                                    broadcast.setServer("OMMS CENTRAL");
                                    Objects.requireNonNull(RuntimeConstants.INSTANCE.getUdpBroadcastSender()).addToQueue(Util.TARGET_CHAT, new Gson().toJson(broadcast, Broadcast.class));
                                    return 1;
                                }
                        )
                )
        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("stop").executes(context -> {
                    if (RuntimeConstants.INSTANCE.getTest())System.exit(0);
                    try {
                        logger.info("Stopping!");
                        PluginManager.INSTANCE.unloadAll();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getHttpServer()).interrupt();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getReciever()).interrupt();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getSocketServer()).interrupt();
                        logger.info("Releasing lock.");
                        Util.releaseLock(RuntimeConstants.INSTANCE.getLock());
                        Files.delete(Path.of(Util.LOCK_NAME));
                        logger.info("Bye");
                        System.exit(0);
                    } catch (Exception e) {
                        logger.error("Cannot stop server.", e);
                    }

                    return 0;
                })

        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("reload").executes(context -> {
                    PluginManager.INSTANCE.unloadAll();
                    PluginManager.INSTANCE.init();
                    PluginManager.INSTANCE.loadAll();
                    PermissionManager.INSTANCE.init();
                    System.exit(0);
                    return 0;
                })

        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("status").executes(context -> {
                    TryKotlin.INSTANCE.printOS();
                    RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
                    logger.info("Java VM Info: %s %s %s".formatted(runtime.getVmVendor(), runtime.getVmName(), runtime.getVmVersion()));
                    logger.info("Java VM Spec Info: %s %s %s".formatted(runtime.getSpecVendor(), runtime.getSpecName(), runtime.getSpecVersion()));
                    logger.info("Java version: %s".formatted(getProperty("java.version")));

                    double upTime = runtime.getUptime() / 1000.0;
                    logger.info("Uptime: %.3fS".formatted(upTime));

                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
                    MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
                    double totalMemory = ((heapMemoryUsage.getInit() + nonHeapMemoryUsage.getInit()) / 1024.0) / 1024.0;
                    double maxMemory = ((heapMemoryUsage.getMax() + nonHeapMemoryUsage.getMax()) / 1024.0) / 1024.0;
                    double usedMemory = ((heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed()) / 1024.0) / 1024.0;
                    logger.info("Memory usage: %.3fMiB/%.3fMiB".formatted(usedMemory, maxMemory));

                    var threadGroup = Thread.currentThread().getThreadGroup();
                    int count = threadGroup.activeCount();
                    Thread[] threads = new Thread[count];
                    threadGroup.enumerate(threads);
                    logger.info("Threads:");
                    for (Thread thread : threads) {
                        if (thread.isDaemon()) {
                            logger.info("\t+ %s %d DAEMON %s".formatted(thread.getName(), thread.getId(), thread.getState().name()));
                        }
                        logger.info("\t+ %s %d %s".formatted(thread.getName(), thread.getId(), thread.getState().name()));
                    }

                    logger.info("Java VM Arguments:");
                    runtime.getInputArguments().forEach(x -> logger.info("\t%s".formatted(x)));

                    return 0;
                })
        );

    }


    public void dispatchCommand(String command) {
        try {
            logger.info("CONSOLE issued a command:%s".formatted(command));
            ConsoleHandler.dispatcher.execute(command, new CommandSourceStack());
        } catch (CommandSyntaxException e) {
            logger.error("An error occurred while dispatching command.", e);
        }
    }

    public void handle(Terminal terminal) {
        WhitelistCompleter whitelistCompleter = new WhitelistCompleter();
        PlayerNameCompleter playerNameCompleter = new PlayerNameCompleter();
        var completer = new AggregateCompleter(
                new ArgumentCompleter(
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("list"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("get", "add"),
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
                        new StringsCompleter("whitelist"),
                        new StringsCompleter("query"),
                        NullCompleter.INSTANCE
                ),

                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("grant"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("list"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("query"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("permission"),
                        new StringsCompleter("remove"),
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
                )
        );

        try {
            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
            String line = lineReader.readLine();
            line = line.strip().stripIndent().stripLeading().stripTrailing();
            if (line.isEmpty()){
                return;
            }
            dispatchCommand(line);
        } catch (UserInterruptException | EndOfFileException ignored) {
            //DO NOTHING
        }
    }

}

