package net.zhuruoling.omms.central.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.zhuruoling.omms.central.command.arguments.WhitelistArgumentType
import net.zhuruoling.omms.central.whitelist.Whitelist

typealias S = CommandSourceStack

fun CommandContext<S>.sendFeedback(component: String) {
    this.source.sendFeedback(component)
}

fun CommandContext<S>.sendFeedback(format: String, vararg obj:Any) {
    this.source.sendFeedback(String.format(format,*obj))
}
fun CommandContext<S>.sendError(format: String, vararg obj:Any) {
    this.source.sendError(String.format(format,*obj))
}

fun CommandContext<S>.sendError(component: String) {
    this.source.sendError(component)
}

fun CommandContext<S>.getStringArgument(name: String): String = StringArgumentType.getString(this, name)
fun CommandContext<S>.getIntegerArgument(name: String) = IntegerArgumentType.getInteger(this, name)


class LiteralCommand(root: String) {
    val node: LiteralArgumentBuilder<S> = LiteralArgumentBuilder.literal(root)
}

class ArgumentCommand<T>(name: String, argumentType: ArgumentType<T>) {
    val node: RequiredArgumentBuilder<S, T> =
        RequiredArgumentBuilder.argument(name, argumentType)
}


fun LiteralCommand(literal: String, function: LiteralCommand.() -> Unit): LiteralCommand {
    return LiteralCommand(literal).apply(function)
}

fun LiteralCommand.literal(literal: String, function: LiteralCommand.() -> Unit) {
    this.node.then(LiteralCommand(literal).apply(function).node)
}

fun LiteralCommand.requires(predicate:(S) ->Boolean, function: LiteralCommand.() -> Unit){
    this.apply {
        node.requires(predicate)
        function(this)
    }
}

fun <T> ArgumentCommand<T>.requires(predicate: (S) -> Boolean, function: ArgumentCommand<T>.() -> Unit){
    this.apply {
        node.requires(predicate)
        function(this)
    }
}

fun LiteralCommand.execute(function: CommandContext<S>.() -> Int) {
    this.node.executes {
        function(it)
    }
}

fun LiteralCommand.requires(function: S.() -> Boolean): LiteralCommand {
    this.node.requires(function)
    return this
}

fun <T> LiteralCommand.argument(name: String, argumentType: ArgumentType<T>, function: ArgumentCommand<T>.() -> Unit) {
    this.node.then(ArgumentCommand<T>(name, argumentType).apply(function).node)
}


fun <T> ArgumentCommand<T>.literal(literal: String, function: LiteralCommand.() -> Unit) {
    this.node.then(LiteralCommand(literal).apply(function).node)
}

fun <T> ArgumentCommand<T>.execute(function: CommandContext<S>.() -> Int) {
    this.node.executes {
        function(it)
    }
}

fun <T> ArgumentCommand<T>.requires(function: S.() -> Boolean): ArgumentCommand<T> {
    this.node.requires(function)
    return this
}

fun <T> ArgumentCommand<T>.argument(
    name: String,
    argumentType: ArgumentType<T>,
    function: ArgumentCommand<T>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, argumentType).apply(function).node)
}

fun ArgumentCommand<Int>.integerArgument(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    function: ArgumentCommand<Int>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, IntegerArgumentType.integer(min, max)).apply(function).node)
}

fun ArgumentCommand<String>.wordArgument(
    name: String,
    function: ArgumentCommand<String>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, StringArgumentType.word()).apply(function).node)
}

fun ArgumentCommand<String>.whitelistArgument(
    name: String,
    function: ArgumentCommand<Whitelist>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, WhitelistArgumentType.whitelist()).apply(function).node)
}


fun ArgumentCommand<String>.greedyStringArgument(
    name: String,
    function: ArgumentCommand<String>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, StringArgumentType.greedyString()).apply(function).node)
}


fun LiteralCommand.integerArgument(
    name: String,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    function: ArgumentCommand<Int>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, IntegerArgumentType.integer(min, max)).apply(function).node)
}

fun LiteralCommand.wordArgument(
    name: String,
    function: ArgumentCommand<String>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, StringArgumentType.word()).apply(function).node)
}

fun LiteralCommand.whitelistArgument(
    name: String,
    function: ArgumentCommand<Whitelist>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, WhitelistArgumentType.whitelist()).apply(function).node)
}


fun LiteralCommand.greedyStringArgument(
    name: String,
    function: ArgumentCommand<String>.() -> Unit
) {
    this.node.then(ArgumentCommand(name, StringArgumentType.greedyString()).apply(function).node)
}


