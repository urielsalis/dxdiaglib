package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.extradata.KeyValueSection
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.ExtraData
import me.urielsalis.dxdiaglib.model.extradata.NullData
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo

class SystemInfoParser : DxdiagParser {
    override fun parse(sections: Map<String, List<Section>>): ExtraData {
        val systemInfoSection = sections["System Information"]?.first() as KeyValueSection? ?: return NullData()
        return SystemInfo(systemInfoSection["Time of this report"].firstOrNull(),
                systemInfoSection["Machine name"].firstOrNull(),
                systemInfoSection["Operating System"].firstOrNull(),
                systemInfoSection["Language"].firstOrNull(),
                systemInfoSection["System Manufacturer"].firstOrNull(),
                systemInfoSection["System Model"].firstOrNull(),
                systemInfoSection["Processor"].firstOrNull(),
                systemInfoSection["Memory"].firstOrNull(),
                systemInfoSection["Windows Dir"].firstOrNull())
    }

    override fun getName(): String {
        return "System Information"
    }

}
