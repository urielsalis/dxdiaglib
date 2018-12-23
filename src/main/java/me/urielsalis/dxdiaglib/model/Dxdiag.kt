package me.urielsalis.dxdiaglib.model

import me.urielsalis.dxdiaglib.model.extradata.ExtraData
import me.urielsalis.dxdiaglib.model.extradata.NullData
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.SectionList
import me.urielsalis.dxdiaglib.parsers.DxdiagParser

data class Dxdiag(
        internal val sections: Map<String, List<Section>>,
        internal val extraData: MutableMap<String, ExtraData>,
        private val parsers: MutableList<DxdiagParser>
) {
    operator fun get(section: String): ExtraData {
        if (extraData.containsKey(section)) {
            return extraData[section]!!
        } else {
            val sectionList = sections[section] ?: return NullData()
            return SectionList(sectionList)
        }
    }

    fun parse(parser: DxdiagParser): Dxdiag {
        parsers.add(parser)
        extraData[parser.getName()] = parser.parse(sections)
        return this
    }
}
