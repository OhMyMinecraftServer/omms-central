package net.zhuruoling.console;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.zhuruoling.announcement.Announcement;
import net.zhuruoling.announcement.AnnouncementManager;
import net.zhuruoling.controller.ControllerManager;
import net.zhuruoling.foo.Foo;
import net.zhuruoling.main.MainKt;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.network.TestKt;
import net.zhuruoling.network.broadcast.Broadcast;
import net.zhuruoling.permission.IllegalPermissionNameException;
import net.zhuruoling.permission.Permission;
import net.zhuruoling.permission.PermissionChange;
import net.zhuruoling.permission.PermissionManager;
import net.zhuruoling.plugin.PluginManager;
import net.zhuruoling.util.Result;
import net.zhuruoling.util.Util;
import net.zhuruoling.whitelist.WhitelistManager;
import org.jline.builtins.Completers;
import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static java.lang.System.getProperty;

public class ConsoleCommandHandler {
    private static Logger logger;

    private static CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();

    public ConsoleCommandHandler() {
        init();
    }

    public static CommandDispatcher<CommandSourceStack> getDispatcher() {
        return dispatcher;
    }

    public static void init() {
        RuntimeConstants.pluginCommandHashMap.forEach(pluginCommand -> dispatcher.register(pluginCommand.getCommandNode()));
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("whitelist")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("get").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).executes(c -> {
                                            String name = getString(c, "whitelist");
                                            var whitelist = WhitelistManager.INSTANCE.getWhitelist(name);
                                            if (whitelist == null) {
                                                logger.error("Whitelist %s does not exist.".formatted(name));
                                            } else {
                                                logger.info("Whitelist %s :".formatted(whitelist.getName()));
                                                Arrays.stream(whitelist.getPlayers()).toList().forEach(x -> logger.info("\t-%s".formatted(x)));
                                            }
                                            return 1;
                                        }
                                )
                        )
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(context -> {
                            var names = WhitelistManager.INSTANCE.getWhitelistNames();
                            logger.info("%d whitelists(%s) added to this server.".formatted(names.size(), names));
                            return 1;
                        })
                )

                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("query").then(
                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                            String player = getString(commandContext, "player");
                            List<String> names = WhitelistManager.INSTANCE.queryInAllWhitelist(player);
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
                                                    var result = WhitelistManager.INSTANCE.addToWhiteList(whitelist, player);
                                                    if (result.equals(Result.OK)) {
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
                                                    var result = WhitelistManager.INSTANCE.removeFromWhiteList(whitelist, player);
                                                    if (result.equals(Result.OK)) {
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
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("search")
                                .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word())
                                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word())
                                                .executes(commandContext -> {
                                                    var whitelistName = StringArgumentType.getString(commandContext, "whitelist");
                                                    var player = StringArgumentType.getString(commandContext, "player");
                                                    if (Objects.equals(whitelistName, "all")) {
                                                        WhitelistManager.INSTANCE.getWhitelistNames().forEach(s -> {
                                                            searchWhitelist(player, s);
                                                        });
                                                        return 0;
                                                    }
                                                    if (!WhitelistManager.INSTANCE.hasWhitelist(whitelistName)) {
                                                        logger.error("Specified whitelist does not exist.");
                                                    }
                                                    searchWhitelist(player, whitelistName);
                                                    return 0;
                                                })
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
                    MainKt.stop();
                    return 0;
                })

        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("reload").executes(context -> {
                    dispatcher = new CommandDispatcher<>();
                    RuntimeConstants.pluginCommandHashMap = new ArrayList<>();
                    PluginManager.INSTANCE.unloadAll();
                    PluginManager.INSTANCE.init();
                    PluginManager.INSTANCE.loadAll();
                    PermissionManager.INSTANCE.init();
                    ControllerManager.INSTANCE.init();
                    AnnouncementManager.INSTANCE.init();
                    init();
                    return 0;
                })

        );
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("status").executes(context -> {
                    Foo.INSTANCE.bar();
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

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("rua").executes(x -> {
            TestKt.testAuth("OMOGUS", "whitelist list");
            return 0;
        }));

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("?").executes(x -> {
            var usages = dispatcher.getAllUsage(dispatcher.getRoot(), new CommandSourceStack(CommandSourceStack.Source.INTERNAL), false);
            for (String usage : usages) {
                logger.info(usage);
            }
            return 0;
        }));

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("ban").then(
                RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(x -> {
                    String player = StringArgumentType.getString(x, "player");
                    WhitelistManager.INSTANCE.forEach(stringEntry -> {
                        WhitelistManager.INSTANCE.removeFromWhiteList(stringEntry.getKey(), player);
                        return null;
                    });
                    ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
                        ControllerManager.INSTANCE.sendInstruction(s, "kick " + player);
                    });
                    return 0;
                })
        ));


        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("permission").then(
                                LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(x -> {
                                    var permissionMap = PermissionManager.INSTANCE.getPermissionTable();
                                    logger.info("Listing permissions:");
                                    permissionMap.forEach((i, p) -> {
                                        logger.info("\tcode %d has got those permissions:".formatted(i));
                                        p.forEach(permission -> logger.info("\t\t- %s".formatted(permission.name())));
                                    });
                                    if (!PermissionManager.INSTANCE.getChangesTable().isEmpty()) {
                                        logger.info("Changes listed below will be applied to permission files.");
                                        var changes = PermissionManager.INSTANCE.getChangesTable();
                                        changes.forEach(permissionChange -> {
                                            switch (permissionChange.getOperation()) {
                                                case GRANT ->
                                                        logger.info("\tThose permissions will be added to code %d: %s".formatted(permissionChange.getCode(), makeChangesString(permissionChange)));
                                                case CREATE ->
                                                        logger.info("\tPermission code %d will be created.".formatted(permissionChange.getCode()));
                                                case DELETE ->
                                                        logger.info("\tPermission code %d will be deleted.".formatted(permissionChange.getCode()));
                                                case DENY -> {
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
                                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("permission_name", greedyString())
                                                        .executes(x -> {
                                                            int code = IntegerArgumentType.getInteger(x, "code");
                                                            String permissionName = StringArgumentType.getString(x, "permission_name");
                                                            var permissions = PermissionManager.getPermissionsFromString(permissionName);
                                                            ArrayList<Permission> checkedPermissions = new ArrayList<>();
                                                            var p = PermissionManager.INSTANCE.getPermission(code);
                                                            if (p == null) {
                                                                logger.warn("Permission code %d does not exist.".formatted(code));
                                                                return -1;
                                                            }
                                                            Objects.requireNonNull(permissions).forEach(permission -> {
                                                                if (p.contains(permission)) {
                                                                    logger.warn("Code %d already got permission %s".formatted(code, permissionName));
                                                                } else {
                                                                    checkedPermissions.add(permission);
                                                                }
                                                            });
                                                            PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.DENY, code, checkedPermissions));

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
                                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("permission_name", greedyString())
                                                        .executes(x -> {
                                                            int code = IntegerArgumentType.getInteger(x, "code");
                                                            String permissionName = StringArgumentType.getString(x, "permission_name");
                                                            var p_ = PermissionManager.getPermissionsFromString(permissionName);
                                                            if (p_ != null) {
                                                                if (!p_.isEmpty()) {
                                                                    ArrayList<Permission> permissions = new ArrayList<>(p_);
                                                                    ArrayList<Permission> checkedPermissions = new ArrayList<>();
                                                                    var p = PermissionManager.INSTANCE.getPermission(code);
                                                                    if (p == null) {
                                                                        logger.warn("Permission code %d does not exist.".formatted(code));
                                                                        return -1;
                                                                    }
                                                                    permissions.forEach(permission -> {
                                                                        if (p.contains(permission)) {
                                                                            logger.warn("Code %d already got permission %s".formatted(code, permissionName));
                                                                        } else {
                                                                            checkedPermissions.add(permission);
                                                                        }
                                                                    });
                                                                    permissions.clear();
                                                                    checkedPermissions.forEach(permission -> {
                                                                        if (!checkedPermissions.contains(permission))
                                                                            checkedPermissions.add(permission);
                                                                    });
                                                                    PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.GRANT, code, permissions));

                                                                }
                                                            }
                                                            return 0;
                                                        })
                                        ))
                        )
                        .then(
                                LiteralArgumentBuilder.<CommandSourceStack>literal("save").executes(x -> {
                                    PermissionManager.INSTANCE.savePermissionFile();
                                    return 0;
                                })
                        )
        );


        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("controller")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("execute").then(
                                RequiredArgumentBuilder.<CommandSourceStack, String>argument("controller", word()).then(
                                        RequiredArgumentBuilder.<CommandSourceStack, String>argument("command", greedyString()).executes(commandContext -> {
                                            var controllerName = StringArgumentType.getString(commandContext, "controller");
                                            var controller = ControllerManager.INSTANCE.getControllerByName(controllerName);
                                            var command = StringArgumentType.getString(commandContext, "command");
                                            if (controllerName.equals("all")) {
                                                ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
                                                    logger.info("Sending command %s to %s.".formatted(command, s));
                                                    ControllerManager.INSTANCE.sendInstruction(controllerInstance, command);
                                                });
                                                return 0;
                                            }
                                            if (controller != null) {
                                                logger.info("Sending command %s to %s.".formatted(command, controllerName));
                                                ControllerManager.INSTANCE.sendInstruction(controller, command);
                                                return 0;
                                            }
                                            logger.error("Specified controller %s does not exist.".formatted(controllerName));
                                            return -1;
                                        })
                                )
                        )
                )

        );

        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("announcement")
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(commandContext -> {
                            return 0;
                        })
                )
                .then(
                        LiteralArgumentBuilder.<CommandSourceStack>literal("create").executes(commandContext -> {
                            Scanner scanner = new Scanner(System.in);
                            logger.info("Input announcement title:");
                            String title = scanner.nextLine();
                            logger.info("Input announcement content, double return to end:");
                            ArrayList<String> lines = new ArrayList<>();
                            while (true) {
                                String line = scanner.nextLine();
                                if (!lines.isEmpty()) {
                                    if (lines.get(lines.size() - 1).isEmpty() && line.isEmpty()) {
                                        break;
                                    }
                                }
                                lines.add(line);
                            }
                            lines.remove(lines.size() - 1);
                            logger.debug(title);
                            lines.forEach(logger::debug);
                            Announcement announcement = new Announcement(title, lines.toArray(new String[]{}));
                            logger.info(announcement.toString());
                            AnnouncementManager.INSTANCE.create(announcement);
                            return 0;
                        })
                )
        );
    }

    private static void searchWhitelist(String player, String s) {
        var result = WhitelistManager.INSTANCE.searchInWhitelist(s, player);
        if (result == null) {
            logger.info("No valid results in whitelist %s.".formatted(s));
        } else {
            logger.info("Search result in whitelist %s:".formatted(s));
            result.forEach(searchResult -> logger.info("\t%s".formatted(searchResult.getPlayerName())));
        }
    }


    private static String makeChangesString(PermissionChange permissionChange) {
        var ref = new Object() {
            String affection = "";
        };
        permissionChange.getChanges().forEach(permission -> {
            ref.affection += permission.name();
            if (permissionChange.getChanges().lastIndexOf(permission) != permissionChange.getChanges().size() - 1) {
                ref.affection += ", ";
            }
        });
        return ref.affection;
    }

    public void setLogger(Logger logger) {
        ConsoleCommandHandler.logger = logger;
    }

    Completers.TreeCompleter walkCommandTree(CommandNode<CommandSourceStack> node) {
        var completer = new Completers.TreeCompleter();
        Completers.TreeCompleter.node();
        if (node.getChildren().isEmpty()) {
            node.getName();
        } else {
            node.getName();
            node.getChildren().forEach(this::walkCommandTree);
        }
        return completer;
    }

    public void dispatchCommand(String command) {
        try {
            logger.info("CONSOLE issued a command: %s".formatted(command));
            ConsoleCommandHandler.dispatcher.execute(command, new CommandSourceStack(CommandSourceStack.Source.CONSOLE));
        } catch (CommandSyntaxException e) {
            logger.error("Invalid Command Syntax: " + e.getLocalizedMessage());
        } catch (Throwable exception) {
            logger.error("An error occurred while dispatching command.", new RuntimeException(exception));
        }
    }

}

