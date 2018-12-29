package me.urielsalis.dxdiaglib.model

import me.urielsalis.dxdiaglib.model.extradata.ExtraData
import me.urielsalis.dxdiaglib.model.extradata.Section

data class Dxdiag(val sections: Map<String, Section>, val extras: MutableMap<String, ExtraData>) {
    operator fun get(value: String) = if (extras.containsKey(value)) {
        extras[value]
    } else {
        sections[value]
    }
}