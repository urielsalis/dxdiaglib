package me.urielsalis.driverparser.postprocessor

import me.urielsalis.driverparser.driver.Nvidia
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser

@RequiredParser(DisplayDevicesParser::class, SystemInfoParser::class)
class NvidiaDriverPostProcessor : GenericDriverPostProcessor() {
    override fun matchesCondition(manufacturer: String) = manufacturer.contains("nvidia", true)
    override fun searchDriver(displayDevice: DisplayDevice, os: String) = Nvidia.search(displayDevice, os)
    override fun getName(): String {
        return "Nvidia driver"
    }

}
