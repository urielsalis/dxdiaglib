package me.urielsalis.dxdiaglib.model.extradata

data class SubSection(val variables: MutableMap<String, String>) : ExtraData {
    operator fun get(s: String): String? {
        return variables[s]
    }
}