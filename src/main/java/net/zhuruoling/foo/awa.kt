package net.zhuruoling.foo

import net.zhuruoling.util.Util
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J
import java.util.Scanner

fun main() {
    SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
    println(SomeThing("wdnmd").toString())
    println(SomeThing("114514").toString())
}

class SomeThing(private var text:String, var id:String = Util.randomStringGen(16)){
    override fun toString(): String {
        return "SomeThing(text='$text', id='$id')"
    }
}
