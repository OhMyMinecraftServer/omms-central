package icu.takeneko.omms.central.controller

@kotlinx.serialization.Serializable
data class ControllerData(
    val name: String,
    val displayName: String = name,
    val type: String,
    val httpQueryAddress: String,
    val statusQueryable: Boolean
){
    fun inflate():Controller{
        return ControllerImpl(
            name,
            displayName,
            type,
            httpQueryAddress,
            statusQueryable
        )
    }
}
