package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.ExtraData

interface DxdiagParser {
    fun parse(sections: Map<String, List<Section>>): ExtraData
    fun getName(): String
}
