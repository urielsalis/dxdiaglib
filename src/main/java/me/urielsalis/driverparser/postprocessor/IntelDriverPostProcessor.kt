package me.urielsalis.driverparser.postprocessor

import  me.urielsalis.driverparser.driver.Intel
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser

@RequiredParser(DisplayDevicesParser::class, SystemInfoParser::class)
class IntelDriverPostProcessor : GenericDriverPostProcessor() {
    override fun matchesCondition(manufacturer: String) = manufacturer.contains("intel", true)
    override fun searchDriver(displayDevice: DisplayDevice, os: String) = Intel.search(displayDevice, os)
    override fun getName(): String {
        return "Intel driver"
    }

}
