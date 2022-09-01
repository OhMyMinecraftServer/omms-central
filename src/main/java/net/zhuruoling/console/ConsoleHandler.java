package net.zhuruoling.console;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.zhuruoling.broadcast.Broadcast;
import net.zhuruoling.kt.TryKotlin;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.permission.IllegalPermissionNameException;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.permission.PermissionChange;
import net.zhuruoling.permission.PermissionManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static java.lang.System.getProperty;

public class ConsoleHandler {
    private static CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
    public static Logger logger;
    public static HashMap<String, PluginCommand> pluginCommandHashMap;
    private static ArrayList<String> literalSimplePluginCommands = new ArrayList<>();
    public static CommandDispatcher<CommandSourceStack> getDispatcher(){
        return dispatcher;
    }

    public static ArrayList<String> getLiteralSimplePluginCommands() {
        return literalSimplePluginCommands;
    }

    public ConsoleHandler(){
        init();
    }


    public static void init() {
        dispatcher = new CommandDispatcher<>();
        pluginCommandHashMap = new HashMap<>();
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("whitelist")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("get").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).executes(c -> {
                                            String name = getString(c, "whitelist");
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
                    if (RuntimeConstants.INSTANCE.getTest()) System.exit(0);
                    try {
                        logger.info("Stopping!");
                        PluginManager.INSTANCE.unloadAll();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getHttpServer()).interrupt();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getReciever()).interrupt();
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getUdpBroadcastSender()).setStopped(true);
                        Objects.requireNonNull(RuntimeConstants.INSTANCE.getSocketServer()).interrupt();
                        if (!RuntimeConstants.INSTANCE.getNoLock()){
                            logger.info("Releasing lock.");
                            Util.releaseLock(RuntimeConstants.INSTANCE.getLock());
                            Files.delete(Path.of(Util.LOCK_NAME));
                        }
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
                    //double totalMemory = ((heapMemoryUsage.getInit() + nonHeapMemoryUsage.getInit()) / 1024.0) / 1024.0;
                    double maxMemory = ((heapMemoryUsage.getMax() + nonHeapMemoryUsage.getMax()) / 1024.0) / 1024.0;
                    double usedMemory = ((heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed()) / 1024.0) / 1024.0;
                    logger.info("Memory usage: %.3fMiB/%.3fMiB".formatted(usedMemory, maxMemory));

                    Util.listAll(logger);

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

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("help").executes(x -> {
            var usages = dispatcher.getAllUsage(dispatcher.getRoot(), new CommandSourceStack(CommandSourceStack.Source.INTERNAL), false);
            for (String usage : usages) {
                logger.info(usage);
            }
            return 0;
        }));

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("permission").then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(x -> {
                            var permissionMap = PermissionManager.INSTANCE.getPermissionTable();
                            logger.info("Listing permissions:");
                            permissionMap.forEach((i, p) -> {
                                logger.info("\tcode %d has got those permissions:".formatted(i));
                                p.forEach(permission -> {
                                    logger.info("\t\t- %s".formatted(permission.name()));
                                });
                            });
                            if (!PermissionManager.INSTANCE.getChangesTable().isEmpty()) {
                                logger.info("Changes listed below will be applied to permission files.");
                                var changes = PermissionManager.INSTANCE.getChangesTable();
                                changes.forEach(permissionChange -> {
                                    switch (permissionChange.getOperation()) {
                                        case ADD -> {


                                            logger.info("\tThose permissions will be added to code %d: %s".formatted(permissionChange.getCode(), makeChangesString(permissionChange)));
                                        }
                                        case CREATE -> {
                                            logger.info("\tPermission code %d will be created.".formatted(permissionChange.getCode()));
                                        }
                                        case DELETE -> {
                                            logger.info("\tPermission code %d will be deleted.".formatted(permissionChange.getCode()));
                                        }
                                        case REMOVE -> {
                                            makeChangesString(permissionChange);
                                            logger.info("\tThose permissions will be removed from code %d: %s".formatted(permissionChange.getCode(), makeChangesString(permissionChange)));

                                        }
                                        default -> {

                                        }
                                    }

                                });
                            }
                            return 0;
                        })
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("remove")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("code", integer(0)).then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("permission_name", string())
                                                .executes(x -> {
                                                    int code = IntegerArgumentType.getInteger(x, "code");
                                                    String permissionName = StringArgumentType.getString(x, "permission_name");
                                                    try {
                                                        Permission permission = Permission.valueOf(permissionName);
                                                        var p = PermissionManager.INSTANCE.getPermission(code);
                                                        if (p == null) {
                                                            logger.warn("Permission code %d does not exist.".formatted(code));
                                                            return -1;
                                                        }
                                                        ArrayList<Permission> permissions = new ArrayList<>(p);
                                                        if (!permissions.contains(permission)) {
                                                            logger.warn("Code %d has not got permission %s".formatted(code, permissionName));
                                                            return -1;
                                                        }
                                                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.REMOVE, code, List.of(permission)));

                                                    } catch (IllegalArgumentException e) {
                                                        logger.warn("%s is not a valid permission name".formatted(permissionName), new IllegalPermissionNameException(permissionName, e));
                                                    }
                                                    return 0;
                                                })
                                ))
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("get")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("code", integer(0))
                                        .executes(x -> {
                                            int code = IntegerArgumentType.getInteger(x, "code");
                                            List<Permission> permissions = PermissionManager.INSTANCE.getPermission(code);
                                            if (permissions == null) {
                                                logger.warn("Permission code %d does not exist.".formatted(code));
                                                return -1;
                                            }
                                            logger.info("Permission code %d has got those permissions:");
                                            permissions.forEach(permission -> logger.info("\t- " + permission));
                                            return 0;
                                        })
                                )
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("grant")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("code", integer(0)).then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("permission_name", string())
                                                .executes(x -> {
                                                    int code = IntegerArgumentType.getInteger(x, "code");
                                                    String permissionName = StringArgumentType.getString(x, "permission_name");

                                                    try {
                                                        Permission permission = Permission.valueOf(permissionName);
                                                        var p = PermissionManager.INSTANCE.getPermission(code);
                                                        if (p == null) {
                                                            logger.warn("Permission code %d does not exist.".formatted(code));
                                                            return -1;
                                                        }
                                                        ArrayList<Permission> permissions = new ArrayList<>(p);
                                                        if (permissions.contains(permission)) {
                                                            logger.warn("Code %d already got permission %s".formatted(code, permissionName));
                                                            return -1;
                                                        }
                                                        PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.ADD, code, List.of(permission)));

                                                    } catch (IllegalArgumentException e) {
                                                        logger.error("%s is not a valid permission name".formatted(permissionName), new IllegalPermissionNameException(permissionName, e));
                                                    }

                                                    return 0;
                                                })
                                ))
                ).then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("save").executes(x -> {
                            PermissionManager.INSTANCE.savePermissionFile();
                            return 0;
                        })
                )
        );

    }

    private static String makeChangesString(PermissionChange permissionChange) {
        var ref = new Object() {
            String affection = "";
        };
        permissionChange.getChanges().forEach(permission -> {
            ref.affection += permission.name();
            if (permissionChange.getChanges().lastIndexOf(permission) != permissionChange.getChanges().size() - 1 ){
                ref.affection += ", ";
            }
        });
        return ref.affection;
    }


    public void dispatchCommand(String command) {
        try {
            logger.info("CONSOLE issued a command:%s".formatted(command));
            ConsoleHandler.dispatcher.execute(command, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (CommandSyntaxException | NullPointerException exception) {
            logger.error("An error occurred while dispatching command.", new RuntimeException(exception));
        }
    }

    public void handle(Terminal terminal) {
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
                        new StringsCompleter("permission"),
                        new StringsCompleter("grant", "remove"),
                        permissionCodeCompleter,
                        permissionNameCompleter,
                        NullCompleter.INSTANCE
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
                        new PluginCommandCompleter(),
                        NullCompleter.INSTANCE
                )

        );

        try {
            LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(completer).build();
            String line = lineReader.readLine();
            line = line.strip().stripIndent().stripLeading().stripTrailing();
            if (line.isEmpty()) {
                return;
            }
            dispatchCommand(line);
        } catch (UserInterruptException | EndOfFileException ignored) {
            //DO NOTHING
        }
    }

}

