package me.urielsalis.dxdiaglib.model.extradata

abstract class Section: ExtraData

data class KeyValueSection(val content: Map<String, List<String>>) : Section() {
    operator fun get(s: String): List<String> {
        return content[s].orEmpty()
    }
}

data class ListSection(val content: List<String>) : Section()