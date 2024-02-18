package icu.takeneko.omms.central.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import icu.takeneko.omms.central.permission.PermissionManager;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class PermissionCodeArgumentType implements ArgumentType<Integer> {

    private PermissionCodeArgumentType() {
    }

    @Override
    public Integer parse(StringReader stringReader) throws CommandSyntaxException {
        var i = stringReader.readInt();
        if (PermissionManager.INSTANCE.getPermission(i) == null) {
            throw new DynamicCommandExceptionType(o ->
                    new LiteralMessage("Permission code " + o + " not exist.")
            ).createWithContext(stringReader, i);
        }
        return i;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var prefix = builder.getRemaining();
        if (!prefix.isEmpty()) {
            PermissionManager.INSTANCE.getPermissionTable()
                    .keySet()
                    .stream()
                    .map(Objects::toString)
                    .filter(it -> it.startsWith(prefix))
                    .forEach(builder::suggest);
        } else {
            PermissionManager.INSTANCE.getPermissionTable()
                    .keySet()
                    .stream()
                    .map(Objects::toString)
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    public static PermissionCodeArgumentType code() {
        return new PermissionCodeArgumentType();
    }

    public static <S> int getPermissionCode(CommandContext<S> context, String name) {
        return context.getArgument(name, Integer.class);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
