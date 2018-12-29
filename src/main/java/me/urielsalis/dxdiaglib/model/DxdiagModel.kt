package me.urielsalis.dxdiaglib.model

import me.urielsalis.dxdiaglib.model.extradata.ExtraData

data class Section(val sectionName: String, val subSections: MutableList<SubSection>) : ExtraData
data class SubSection(val variables: MutableMap<String, String>) : ExtraData {
    operator fun get(s: String): String? {
        return variables[s]
    }
}