package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.Section
import me.urielsalis.dxdiaglib.model.extradata.SystemDevice
import me.urielsalis.dxdiaglib.model.extradata.SystemDevices

class SystemDevicesParser : DxdiagParser {
    override fun parse(dxdiag: Dxdiag): Dxdiag {
        val systemDevicesSection = (dxdiag["System Devices"] as? Section) ?: return dxdiag

        val systemDevices = systemDevicesSection.subSections.map {
            SystemDevice(it["Name"], it["Device ID"])
        }
        dxdiag.extras["System Devices"] = SystemDevices(systemDevices)
        return dxdiag
    }
}
