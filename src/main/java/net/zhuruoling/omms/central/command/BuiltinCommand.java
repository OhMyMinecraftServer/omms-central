package net.zhuruoling.omms.central.command;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import net.zhuruoling.omms.central.GlobalVariable;
import net.zhuruoling.omms.central.announcement.Announcement;
import net.zhuruoling.omms.central.announcement.AnnouncementManager;
import net.zhuruoling.omms.central.console.ConsoleUtil;
import net.zhuruoling.omms.central.console.ConsoleUtilKt;
import net.zhuruoling.omms.central.controller.ControllerManager;
import net.zhuruoling.omms.central.controller.console.ControllerConsole;
import net.zhuruoling.omms.central.controller.console.input.StdinInputSource;
import net.zhuruoling.omms.central.controller.console.output.StdOutPrintTarget;
import net.zhuruoling.omms.central.main.CentralServer;
import net.zhuruoling.omms.central.network.broadcast.Broadcast;
import net.zhuruoling.omms.central.network.http.routes.WebsocketRouteKt;
import net.zhuruoling.omms.central.network.pair.PairManager;
import net.zhuruoling.omms.central.permission.Permission;
import net.zhuruoling.omms.central.permission.PermissionChange;
import net.zhuruoling.omms.central.permission.PermissionManager;
import net.zhuruoling.omms.central.plugin.PluginInstance;
import net.zhuruoling.omms.central.plugin.PluginManager;
import net.zhuruoling.omms.central.plugin.metadata.PluginDependencyRequirement;
import net.zhuruoling.omms.central.plugin.metadata.PluginMetadata;
import net.zhuruoling.omms.central.util.Util;
import net.zhuruoling.omms.central.util.UtilKt;
import net.zhuruoling.omms.central.whitelist.PlayerAlreadyExistsException;
import net.zhuruoling.omms.central.whitelist.PlayerNotFoundException;
import net.zhuruoling.omms.central.whitelist.WhitelistManager;
import net.zhuruoling.omms.central.whitelist.WhitelistNotExistException;
import org.jetbrains.annotations.NotNull;
import org.jline.builtins.Completers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.util.*;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.*;
import static java.lang.System.getProperty;

public class BuiltinCommand {
    private static final Logger logger = LoggerFactory.getLogger("BuiltinCommand");
    static LiteralArgumentBuilder<CommandSourceStack> whitelistCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("whitelist")
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("get").then(
                            RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).executes(c -> {
                                        String name = getString(c, "whitelist");
                                        var whitelist = WhitelistManager.INSTANCE.getWhitelist(name);
                                        if (whitelist == null) {
                                            logger.error("Whitelist %s does not exist.".formatted(name));
                                        } else {
                                            c.getSource().sendFeedback("Whitelist %s :".formatted(whitelist.getName()));
                                            whitelist.getPlayers().forEach(x -> c.getSource().sendFeedback("\t-%s".formatted(x)));
                                        }
                                        return 1;
                                    }
                            )
                    )
            )
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(context -> {
                        var names = WhitelistManager.INSTANCE.getWhitelistNames();
                        context.getSource().sendFeedback("%d whitelists(%s) added to this server.".formatted(names.size(), names));
                        return 1;
                    })
            )
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("query").then(
                    RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                        String player = getString(commandContext, "player");
                        List<String> names = WhitelistManager.INSTANCE.queryInAllWhitelist(player);
                        if (names.isEmpty()) {
                            commandContext.getSource().sendFeedback("Player %s does not exist in any whitelists.".formatted(player));
                            return 1;
                        }
                        commandContext.getSource().sendFeedback("Player %s exists in whitelist:%s.".formatted(player, names));
                        return 1;
                    })
            ))
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("add").then(
                            RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word()).then(
                                    RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(commandContext -> {
                                                String whitelist = getString(commandContext, "whitelist");
                                                String player = getString(commandContext, "player");
                                                try {
                                                    WhitelistManager.INSTANCE.addToWhiteList(whitelist, player);
                                                    commandContext.getSource().sendFeedback("Successfully added %s to %s".formatted(player, whitelist));
                                                    return 0;
                                                } catch (WhitelistNotExistException e) {
                                                    commandContext.getSource().sendError("Whitelist %s not exist".formatted(e.getWhitelistName()));
                                                } catch (PlayerAlreadyExistsException e) {
                                                    commandContext.getSource().sendError("Player %s already added to %s exist".formatted(e.getPlayer(), e.getWhitelist()));
                                                }
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
                                                try {
                                                    WhitelistManager.INSTANCE.removeFromWhiteList(whitelist, player);
                                                    commandContext.getSource().sendFeedback("Successfully added %s to %s".formatted(player, whitelist));
                                                    return 0;
                                                } catch (PlayerNotFoundException e) {
                                                    commandContext.getSource().sendError("Player %s not exist.".formatted(e.getPlayer()));
                                                } catch (WhitelistNotExistException e) {
                                                    commandContext.getSource().sendError("Whitelist %s not found.".formatted(e.getWhitelistName()));
                                                }
                                                return 1;
                                            }
                                    )
                            )
                    )

            )
            .then(LiteralArgumentBuilder.<CommandSourceStack>literal("search")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("whitelist", word())
                            .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word())
                                    .executes(commandContext -> {
                                        var whitelistName = StringArgumentType.getString(commandContext, "whitelist");
                                        var player = StringArgumentType.getString(commandContext, "player");
                                        if (Objects.equals(whitelistName, "all")) {
                                            WhitelistManager.INSTANCE.getWhitelistNames().forEach(s -> {
                                                searchWhitelist(player, s, commandContext);
                                            });
                                            return 0;
                                        }
                                        if (!WhitelistManager.INSTANCE.hasWhitelist(whitelistName)) {
                                            logger.error("Specified whitelist does not exist.");
                                        }
                                        searchWhitelist(player, whitelistName, commandContext);
                                        return 0;
                                    })
                            )
                    )
            ).then(LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(commandContext -> {
                        WhitelistManager.INSTANCE.forEach(stringEntry -> {
                            Arrays.stream(UtilKt.whitelistPrettyPrinting(stringEntry.getValue()).split("\n")).forEach(commandContext.getSource()::sendFeedback);
                            return Unit.INSTANCE;
                        });
                        return 0;
                    })
            ).then(LiteralArgumentBuilder.<CommandSourceStack>literal("create")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("name", StringArgumentType.word())
                            .executes(commandContext -> {
                                var src = commandContext.getSource();
                                var name = StringArgumentType.getString(commandContext, "name");
                                if (WhitelistManager.INSTANCE.hasWhitelist(name)) {
                                    src.sendError("Whitelist %s already exists".formatted(name));
                                    return 1;
                                }
                                try {
                                    WhitelistManager.INSTANCE.createWhitelist(name);
                                }catch (Exception e){
                                    src.sendError(ExceptionUtil.stacktraceToString(e));
                                }
                                src.sendFeedback("Done.");
                                return 0;
                            })
                    )
            ).then(LiteralArgumentBuilder.<CommandSourceStack>literal("delete")
                    .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("name", StringArgumentType.word())
                            .executes(commandContext -> {
                                var src = commandContext.getSource();
                                var name = StringArgumentType.getString(commandContext, "name");
                                if (!WhitelistManager.INSTANCE.hasWhitelist(name)) {
                                    src.sendError("Whitelist %s not exist".formatted(name));
                                    return 1;
                                }
                                try {
                                    WhitelistManager.INSTANCE.deleteWhiteList(name);
                                } catch (WhitelistNotExistException e) {
                                    src.sendError(ExceptionUtil.stacktraceToString(e));
                                }
                                return 0;
                            })
                    )
            );

    static LiteralArgumentBuilder<CommandSourceStack> broadcastCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("broadcast")
            .then(
                    RequiredArgumentBuilder.<CommandSourceStack, String>argument("text", greedyString()).executes(
                            commandContext -> {
                                if (Objects.requireNonNull(GlobalVariable.INSTANCE.getConfig()).getChatbridgeImplementation() == null) {
                                    commandContext.getSource().sendFeedback("Chatbridge disabled.");
                                    return 0;
                                }
                                String text = getString(commandContext, "text");
                                commandContext.getSource().sendFeedback("Sending message:" + text);
                                Broadcast broadcast = new Broadcast();
                                broadcast.setChannel("GLOBAL");
                                broadcast.setContent(text);
                                broadcast.setPlayer(Util.randomStringGen(8));
                                broadcast.setServer("OMMS CENTRAL");
                                switch (GlobalVariable.INSTANCE.getConfig().getChatbridgeImplementation()) {
                                    case UDP -> Objects.requireNonNull(GlobalVariable.INSTANCE.getUdpBroadcastSender())
                                            .addToQueue(Util.TARGET_CHAT, new Gson().toJson(broadcast, Broadcast.class));
                                    case WS -> WebsocketRouteKt.sendToAllWS(broadcast);
                                }
                                //Objects.requireNonNull(GlobalVariable.INSTANCE.getUdpBroadcastSender()).addToQueue(Util.TARGET_CHAT, new Gson().toJson(broadcast, Broadcast.class));
                                return 1;
                            }
                    )
            );

    static LiteralArgumentBuilder<CommandSourceStack> stopCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("stop").executes(context -> {
        CentralServer.stop();
        return 0;
    });

    static LiteralArgumentBuilder<CommandSourceStack> reloadCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("reload").executes(context -> {
        CommandManager.INSTANCE.clear();
        PermissionManager.INSTANCE.init();
        ControllerManager.INSTANCE.init();
        AnnouncementManager.INSTANCE.init();
        WhitelistManager.INSTANCE.init();
        CommandManager.INSTANCE.reload();
        return 0;
    });

    static LiteralArgumentBuilder<CommandSourceStack> statusCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("status").executes(context -> {
        UtilKt.printRuntimeEnv();
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        context.getSource().sendFeedback("Java VM Info: %s %s %s".formatted(runtime.getVmVendor(), runtime.getVmName(), runtime.getVmVersion()));
        context.getSource().sendFeedback("Java VM Spec Info: %s %s %s".formatted(runtime.getSpecVendor(), runtime.getSpecName(), runtime.getSpecVersion()));
        context.getSource().sendFeedback("Java version: %s".formatted(getProperty("java.version")));

        double upTime = runtime.getUptime() / 1000.0;
        context.getSource().sendFeedback("Uptime: %.3fS".formatted(upTime));

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        //double totalMemory = ((heapMemoryUsage.getInit() + nonHeapMemoryUsage.getInit()) / 1024.0) / 1024.0;
        double maxMemory = ((heapMemoryUsage.getMax() + nonHeapMemoryUsage.getMax()) / 1024.0) / 1024.0;
        double usedMemory = ((heapMemoryUsage.getUsed() + nonHeapMemoryUsage.getUsed()) / 1024.0) / 1024.0;
        context.getSource().sendFeedback("Memory usage: %.3fMiB/%.3fMiB".formatted(usedMemory, maxMemory));

        Util.listAllByCommandSource(context.getSource());

        var threadGroup = Thread.currentThread().getThreadGroup();
        int count = threadGroup.activeCount();
        Thread[] threads = new Thread[count];
        threadGroup.enumerate(threads);
        context.getSource().sendFeedback("Thread Count: %d".formatted(count));
        context.getSource().sendFeedback("Threads:");
        for (Thread thread : threads) {
            if (thread.isDaemon()) {
                context.getSource().sendFeedback("\t+ %s %d DAEMON %s".formatted(thread.getName(), thread.getId(), thread.getState().name()));
                continue;
                }
            context.getSource().sendFeedback("\t+ %s %d %s".formatted(thread.getName(), thread.getId(), thread.getState().name()));
        }

        context.getSource().sendFeedback("Java VM Arguments:");
        runtime.getInputArguments().forEach(x -> context.getSource().sendFeedback("\t%s".formatted(x)));
        context.getSource().sendFeedback("main() Arguments:");
        GlobalVariable.INSTANCE.getArgs().forEach(x -> context.getSource().sendFeedback("\t%s".formatted(x)));
        return 0;
    });

    static LiteralArgumentBuilder<CommandSourceStack> helpCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("help").executes(x -> {
        var dispatcher = CommandManager.INSTANCE.getCommandDispatcher();
        var usages = dispatcher.getAllUsage(dispatcher.getRoot(), new CommandSourceStack(CommandSourceStack.Source.INTERNAL), false);
        for (String usage : usages) {
            x.getSource().sendFeedback(usage);
        }
        return 0;
    });




    static LiteralArgumentBuilder<CommandSourceStack> banCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("ban").then(
            RequiredArgumentBuilder.<CommandSourceStack, String>argument("player", word()).executes(x -> {
                String player = StringArgumentType.getString(x, "player");
                var list = WhitelistManager.INSTANCE.getWhitelistNames();
                var whitelistNames = new ArrayList<>(list);
                whitelistNames.forEach(s -> {
                    try {
                        x.getSource().sendFeedback("Removing player from whitelist " + s);
                        WhitelistManager.INSTANCE.removeFromWhiteList(s, player);
                    } catch (Exception ignored) {
                    }
                });
                ControllerManager.INSTANCE.getControllers().forEach((s, controllerInstance) -> {
                    x.getSource().sendFeedback("kicking player %s from %s".formatted(player, s));
                    ControllerManager.INSTANCE.sendCommand(s, "kick " + player);
                });
                return 0;
            })
    );

    static LiteralArgumentBuilder<CommandSourceStack> permissionCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("permission").then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(x -> {
                        var permissionMap = PermissionManager.INSTANCE.getPermissionTable();
                        x.getSource().sendFeedback("Listing permissions:");
                        permissionMap.forEach((i, p) -> {
                            x.getSource().sendFeedback("\tcode %d has got those permissions:".formatted(i));
                            p.forEach(permission -> x.getSource().sendFeedback("\t\t- %s".formatted(permission.name())));
                        });
                        if (!PermissionManager.INSTANCE.getChangesTable().isEmpty()) {
                            x.getSource().sendFeedback("Changes listed below will be applied to permission files.");
                            var changes = PermissionManager.INSTANCE.getChangesTable();
                            changes.forEach(permissionChange -> {
                                switch (permissionChange.getOperation()) {
                                    case GRANT ->
                                            x.getSource().sendFeedback("\tThose permissions will be added to code %d: %s".formatted(permissionChange.getCode(), buildChangesString(permissionChange)));
                                    case CREATE ->
                                            x.getSource().sendFeedback("\tPermission code %d will be created.".formatted(permissionChange.getCode()));
                                    case DELETE ->
                                            x.getSource().sendFeedback("\tPermission code %d will be deleted.".formatted(permissionChange.getCode()));
                                    case DENY -> {
                                        buildChangesString(permissionChange);
                                        x.getSource().sendFeedback("\tThose permissions will be removed from code %d: %s".formatted(permissionChange.getCode(), buildChangesString(permissionChange)));

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
                                                if (!checkedPermissions.isEmpty()) {
                                                    PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.DENY, code, checkedPermissions));
                                                } else {
                                                    x.getSource().sendFeedback("No changes will be made.");
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
                                        x.getSource().sendFeedback("Permission code %d has got those permissions:");
                                        permissions.forEach(permission -> x.getSource().sendFeedback("\t- " + permission));
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
                                                        if (!checkedPermissions.isEmpty()) {
                                                            PermissionManager.INSTANCE.submitPermissionChanges(new PermissionChange(PermissionChange.Operation.GRANT, code, checkedPermissions));
                                                        } else {
                                                            x.getSource().sendFeedback("No changes will be made.");
                                                        }
                                                    } else {
                                                        x.getSource().sendFeedback("Permission names contains invalid permission.");
                                                    }
                                                } else {
                                                    x.getSource().sendFeedback("Permission names contains invalid permission.");
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
            );


    static LiteralArgumentBuilder<CommandSourceStack> controllerCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("controller")
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(commandContext -> {
                        ControllerManager.INSTANCE.getControllers().forEach((s, controller) -> {
                            var result = UtilKt.controllerPrettyPrinting(controller);
                            Arrays.stream(result.split("\n")).forEach(commandContext.getSource()::sendFeedback);
                        });
                        return 0;
                    })
            )
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("execute").then(
                            RequiredArgumentBuilder.<CommandSourceStack, String>argument("controller", word()).then(
                                    RequiredArgumentBuilder.<CommandSourceStack, String>argument("command", greedyString()).executes(commandContext -> {
                                        var controllerName = getString(commandContext, "controller");
                                        var controller = ControllerManager.INSTANCE.getControllerByName(controllerName);
                                        var command = getString(commandContext, "command");
                                        if (controllerName.equals("all")) {
                                            ControllerManager.INSTANCE.getControllers().forEach((controllerId, controllerInstance) -> {
                                                commandContext.getSource().sendFeedback("Sending command %s to %s.".formatted(command, controllerId));
                                                var output = ControllerManager.INSTANCE.sendCommand(controllerInstance.getName(), command);
                                                for (String line : output) {
                                                    commandContext.getSource().sendFeedback("[%s] %s".formatted(controllerId, line));
                                                }
                                            });
                                            return 0;
                                        }
                                        if (controller != null) {
                                            commandContext.getSource().sendFeedback("Sending command %s to %s.".formatted(command, controllerName));
                                            var out = ControllerManager.INSTANCE.sendCommand(controller.getName(), command);
                                            for (String line : out) {
                                                commandContext.getSource().sendFeedback("[%s] %s".formatted(controllerName, line));
                                            }
                                            return 0;
                                        }
                                        logger.error("Specified controller %s does not exist.".formatted(controllerName));
                                        return -1;
                                    })
                            )
                    )
            ).then(LiteralArgumentBuilder.<CommandSourceStack>literal("status").then(
                    RequiredArgumentBuilder.<CommandSourceStack, String>argument("controller", greedyString()).executes(commandContext -> {
                        ControllerManager.INSTANCE
                                .getControllerStatus(ConsoleUtil.parseControllerArgument(getString(commandContext, "controller")))
                                .forEach((s, status) -> {
                                    ConsoleUtilKt.printControllerStatus(commandContext.getSource(), s, status);
                                });
                        return 0;
                    }))
            ).then(LiteralArgumentBuilder.<CommandSourceStack>literal("console").then(
                            RequiredArgumentBuilder.<CommandSourceStack, String>argument("controller", StringArgumentType.greedyString())
                                    .requires(commandSourceStack -> commandSourceStack.getSource() == CommandSourceStack.Source.CONSOLE)
                                    .executes(commandContext -> {
                                        var name = StringArgumentType.getString(commandContext, "controller");
                                        if (name.equals("all")) {
                                            commandContext.getSource().sendFeedback("Controller ALL cannot be used there.");
                                            return 1;
                                        }
                                        var controllers = ConsoleUtil.parseControllerArgument(name);
                                        if (controllers.size() > 1) {
                                            commandContext.getSource().sendFeedback("Multiple controller is not supported by this command.");
                                            return 1;
                                        }
                                        var controller = controllers.get(0);
                                        if (!ControllerManager.INSTANCE.contains(controller)) {
                                            commandContext.getSource().sendFeedback("Controller %s not found.".formatted(controller));
                                        }
                                        commandContext.getSource().sendFeedback("Attatching console to controller, exit console using \":q\"");
                                        SysOutOverSLF4J.stopSendingSystemOutAndErrToSLF4J();
                                        StdOutPrintTarget stdOutPrintTarget = new StdOutPrintTarget();
                                        ControllerConsole controllerConsoleImpl = Objects.requireNonNull(ControllerManager.INSTANCE.getControllerByName(controller)).startControllerConsole(new StdinInputSource().withHistory(UtilKt.getOrCreateControllerHistroy(controller)), stdOutPrintTarget, controller);
                                        controllerConsoleImpl.start();
                                        while (controllerConsoleImpl.isAlive()) {
                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException ignored) {
                                            }
                                        }
                                        commandContext.getSource().sendFeedback("Exiting console.");
                                        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
                                        return 0;
                                    })
                    )
            );

    static LiteralArgumentBuilder<CommandSourceStack> announcementCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("announcement")
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(commandContext -> {
                        return 0;
                    })
            )
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("create").requires(commandSourceStack -> commandSourceStack.getSource() == CommandSourceStack.Source.CONSOLE)
                            .executes(commandContext -> {
                                Scanner scanner = new Scanner(System.in);
                                commandContext.getSource().sendFeedback("Input announcement title:");
                                String title = scanner.nextLine();
                                commandContext.getSource().sendFeedback("Input announcement content, double return to end:");
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
                                commandContext.getSource().sendFeedback(announcement.toString());
                                AnnouncementManager.INSTANCE.create(announcement);
                                return 0;
                            })
            );

    static LiteralArgumentBuilder<CommandSourceStack> pairCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("pair")
            .requires(commandSourceStack -> commandSourceStack.getSource() == CommandSourceStack.Source.CONSOLE).executes(commandContext -> {
                PairManager.INSTANCE.consoleMakePair(commandContext.getSource());
                return 0;
            });

    static LiteralArgumentBuilder<CommandSourceStack> pluginCommand = LiteralArgumentBuilder.<CommandSourceStack>literal("plugin")
            .then(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("list").executes(commandContext -> {
                        listPlugins(commandContext.getSource());
                        return 0;
                    })
            );

    public static void registerBuiltinCommand(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(whitelistCommand);
        dispatcher.register(broadcastCommand);
        dispatcher.register(stopCommand);
        dispatcher.register(reloadCommand);
        dispatcher.register(statusCommand);
        dispatcher.register(helpCommand);
        dispatcher.register(banCommand);
        dispatcher.register(permissionCommand);
        dispatcher.register(controllerCommand);
        dispatcher.register(announcementCommand);
        dispatcher.register(pairCommand);
        dispatcher.register(pluginCommand);
    }

    private static void listPlugins(@NotNull CommandSourceStack commandSourceStack) {
        for (PluginInstance instance : PluginManager.INSTANCE) {
            PluginMetadata metadata = instance.getPluginMetadata();
            commandSourceStack.sendFeedback(":: %s%s ::".formatted(
                    metadata.getId(),
                    metadata.getVersion() != null ? " %s".formatted(metadata.getVersion()) : ""
            ));
            if (metadata.getAuthor() != null) {
                commandSourceStack.sendFeedback("    - Author: %s".formatted(metadata.getAuthor()));
            }
            if (metadata.getLink() != null) {
                commandSourceStack.sendFeedback("    - Link: %s".formatted(metadata.getLink()));
            }
            if (metadata.getPluginDependencies() != null && !metadata.getPluginDependencies().isEmpty()) {
                commandSourceStack.sendFeedback("    - Requires: %s".formatted(joinToDependencyString(metadata.getPluginDependencies())));
            }
            if (metadata.getPluginMainClass() != null) {
                commandSourceStack.sendFeedback("    - Plugin Initializer: %s".formatted(metadata.getPluginMainClass()));
            }
            if (metadata.getPluginRequestHandlers() != null && !metadata.getPluginRequestHandlers().isEmpty()) {
                commandSourceStack.sendFeedback("    - Plugin Request Handlers: %s".formatted(net.zhuruoling.omms.central.network.http.UtilKt.joinToString(metadata.getPluginRequestHandlers(), " ")));
            }

        }
    }

    private static void searchWhitelist(@NotNull String player, @NotNull String s, @NotNull CommandContext<? extends CommandSourceStack> context) {
        var result = WhitelistManager.INSTANCE.searchInWhitelist(s, player);
        if (result == null) {
            context.getSource().sendFeedback("No valid results in whitelist %s.".formatted(s));
        } else {
            context.getSource().sendFeedback("Search result in whitelist %s:".formatted(s));
            result.forEach(searchResult -> context.getSource().sendFeedback("\t%s".formatted(searchResult.getPlayerName())));
        }
    }


    private static @NotNull String joinToDependencyString(@NotNull List<? extends PluginDependencyRequirement> pluginDependencyRequirements) {
        StringBuilder stringBuilder = new StringBuilder();
        pluginDependencyRequirements.forEach(pluginDependencyRequirement -> stringBuilder.append(pluginDependencyRequirement.toString()).append(" "));
        return stringBuilder.toString();
    }

    private static @NotNull String buildChangesString(@NotNull PermissionChange permissionChange) {
        return CollectionsKt.joinToString(permissionChange.getChanges(), ", ", "", "", 2147483647, "", null);
    }

    @NotNull
    Completers.TreeCompleter walkCommandTree(@NotNull CommandNode<CommandSourceStack> node) {
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
}

