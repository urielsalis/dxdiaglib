package me.urielsalis.driverparser.postprocessor

import me.urielsalis.driverparser.driver.AMD
import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser

@RequiredParser(DisplayDevicesParser::class, SystemInfoParser::class)
class AMDDriverPostProcessor : GenericDriverPostProcessor() {
    override fun matchesCondition(manufacturer: String): Boolean =
            manufacturer.equals("amd", true) || manufacturer.equals("advanced micro devices, inc.", true)

    override fun searchDriver(displayDevice: DisplayDevice, os: String): DriverDownload? = AMD.search(displayDevice, os)

    override fun getName(): String {
        return "AMD driver"
    }

}
