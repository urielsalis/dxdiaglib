package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.extradata.KeyValueSection
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.ExtraData
import me.urielsalis.dxdiaglib.model.extradata.NullData
import me.urielsalis.dxdiaglib.model.extradata.SystemDevice
import me.urielsalis.dxdiaglib.model.extradata.SystemDevices

class SystemDevicesParser : DxdiagParser {
    override fun parse(sections: Map<String, List<Section>>): ExtraData {
        val systemDevicesSection = (sections["System Devices"] as List<KeyValueSection>?) ?: return NullData()
        val systemDevices = systemDevicesSection.map {
            SystemDevice(it["Name"].firstOrNull(), it["Device ID"].firstOrNull(), it["Driver"])
        }
        return SystemDevices(systemDevices)
    }

    override fun getName(): String {
        return "System Devices"
    }

}
