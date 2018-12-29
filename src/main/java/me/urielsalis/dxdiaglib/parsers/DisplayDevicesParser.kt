package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.Section
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevices


class DisplayDevicesParser : DxdiagParser {
    override fun parse(dxdiag: Dxdiag): Dxdiag {
        val devicesSections = (dxdiag["Display Devices"] as? Section) ?: return dxdiag

        val devices = devicesSections.subSections.map {
            DisplayDevice(it["Card name"],
                    it["Manufacturer"],
                    it["Chip type"],
                    it["Dedicated Memory"],
                    it["Driver Version"],
                    it["Driver Date/Size"],
                    it["Vendor ID"],
                    it["Device ID"],
                    it["SubSys ID"])
        }
        dxdiag.extras["Display Devices"] = DisplayDevices(devices)
        return dxdiag
    }
}
