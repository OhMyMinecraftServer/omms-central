package icu.takeneko.omms.central.command.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import icu.takeneko.omms.central.controller.Controller;
import icu.takeneko.omms.central.controller.ControllerManager;
import icu.takeneko.omms.central.whitelist.WhitelistManager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;

public class ControllerArgumentType implements ArgumentType<Controller> {


    private final Predicate<Controller> filter;

    public ControllerArgumentType(Predicate<Controller> filter) {
        this.filter = filter;
    }

    public ControllerArgumentType() {
        filter = ControllerArgumentType::emptyFilter;
    }

    @Override
    public Controller parse(StringReader stringReader) throws CommandSyntaxException {
        var name = stringReader.readUnquotedString();
        if (ControllerManager.INSTANCE.contains(name)){
            var controller = ControllerManager.INSTANCE.getControllerByName(name);
            if (filter.test(controller)){
                return controller;
            }
            throw new DynamicCommandExceptionType(o ->
                    new LiteralMessage("Controller requirement mismatch.")
            ).createWithContext(stringReader, name);
        }else {
            throw new DynamicCommandExceptionType(o ->
                    new LiteralMessage("Controller " + o + " does not exist.")
            ).createWithContext(stringReader, name);
        }
    }

    public static ControllerArgumentType controller(){return new ControllerArgumentType();}
    public static ControllerArgumentType queryable(){return new ControllerArgumentType(Controller::isStatusQueryable);}

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var s = builder.getRemaining();
        if (s.isEmpty()) {
            ControllerManager.INSTANCE.getControllers()
                    .values()
                    .stream()
                    .filter(filter)
                    .map(Controller::getName)
                    .forEach(builder::suggest);
        } else {
            ControllerManager.INSTANCE.getControllers()
                    .values()
                    .stream()
                    .filter(filter)
                    .map(Controller::getName)
                    .filter(it -> it.startsWith(s))
                    .forEach(builder::suggest);
        }
        return builder.buildFuture();
    }

    public static boolean emptyFilter(Controller controller){
        return true;
    }
}
