package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.extradata.KeyValueSection
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevices
import me.urielsalis.dxdiaglib.model.extradata.ExtraData
import me.urielsalis.dxdiaglib.model.extradata.NullData


class DisplayDevicesParser : DxdiagParser {
    override fun parse(sections: Map<String, List<Section>>): ExtraData {
        val devicesSections = (sections["Display Devices"] as List<KeyValueSection>?) ?: return NullData()
        val devices = devicesSections.map {
            DisplayDevice(it["Card name"].firstOrNull(),
                    it["Manufacturer"].firstOrNull(),
                    it["Chip type"].firstOrNull(),
                    it["Dedicated Memory"].firstOrNull(),
                    it["Driver Version"].firstOrNull(),
                    it["Driver Date/Size"].firstOrNull(),
                    it["Vendor ID"].firstOrNull(),
                    it["Device ID"].firstOrNull(),
                    it["SubSys ID"].firstOrNull())
        }
        if (devices.isEmpty()) return NullData()
        return DisplayDevices(devices)
    }

    override fun getName(): String {
        return "Display Devices"
    }

}
