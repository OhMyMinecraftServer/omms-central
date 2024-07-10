package icu.takeneko.omms.central.fundation

import net.bytebuddy.agent.ByteBuddyAgent
import java.lang.instrument.Instrumentation

object InstrumentationAccess {
    val instrumentation: Instrumentation by lazy {
        ByteBuddyAgent.install()
    }
}