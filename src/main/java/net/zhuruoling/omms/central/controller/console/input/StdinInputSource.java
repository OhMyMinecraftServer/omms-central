package net.zhuruoling.omms.central.controller.console.input;

import net.zhuruoling.omms.central.console.ConsoleInputHandler;
import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;

public class StdinInputSource extends InputSource {

    private static final @NotNull AggregateCompleter simpleMinecraftServerCompleter;

    static {
        simpleMinecraftServerCompleter = new AggregateCompleter(//not all minecraft server command are here.
                new ArgumentCompleter(
                        new StringsCompleter("help"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter(
                                "give", "ban", "ban-ip",
                                "tp", "teleport", "summon",
                                "tell", "tellraw", "clear",
                                "experience", "xp", "experience","pardon","pardon-ip","stop"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("advancement"),
                        new StringsCompleter("grant", "revoke"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("execute"),
                        new StringsCompleter("run", "if", "unless", "as", "at", "store", "positioned", "rotated", "facing", "align", "anchored", "in")
                ),
                new ArgumentCompleter(
                        new StringsCompleter("gamemode"),
                        new StringsCompleter("survival", "creative", "adventure", "spectator"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("weather"),
                        new StringsCompleter("clear","rain","thunder"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("defaultgamemode"),
                        new StringsCompleter("survival", "creative", "adventure", "spectator"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("data"),
                        new StringsCompleter("merge","get","remove","modify"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("function"),
                        NullCompleter.INSTANCE
                ),
                new ArgumentCompleter(
                        new StringsCompleter("gamerule"),
                        new StringsCompleter("announceAdvancements",
                                "commandBlockOutput", "disableElytraMovementCheck", "disableRaids", "doDaylightCycle",
                                "doEntityDrops", "doFireTick", "doImmediateRespawn", "doInsomnia",
                                "doLimitedCrafting", "doMobLoot", "doMobSpawning", "doPatrolSpawning",
                                "doTileDrops", "doTraderSpawning", "doWardenSpawning", "doWeatherCycle",
                                "drowningDamage", "fallDamage", "fireDamage", "forgiveDeadPlayers",
                                "freezeDamage", "keepInventory", "logAdminCommands", "maxCommandChainLength",
                                "maxEntityCramming", "mobGriefing", "naturalRegeneration", "playersSleepingPercentage",
                                "randomTickSpeed", "reducedDebugInfo", "sendCommandFeedback", "showDeathMessages",
                                "spawnRadius", "spectatorsGenerateChunks", "universalAnger"),
                        new StringsCompleter("true", "false"),
                        NullCompleter.INSTANCE
                )

        );
    }

    @NotNull LineReader lineReader = LineReaderBuilder.builder().completer(simpleMinecraftServerCompleter).terminal(ConsoleInputHandler.INSTANCE.getTerminal()).build();
    @Override
    public String getLine() {
        return lineReader.readLine().stripTrailing();
    }

    public @NotNull StdinInputSource withHistory(DefaultHistory history){
        lineReader = LineReaderBuilder.builder().history(history).completer(simpleMinecraftServerCompleter).terminal(ConsoleInputHandler.INSTANCE.getTerminal()).build();
        return this;
    }
}
