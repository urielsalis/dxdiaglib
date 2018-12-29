package me.urielsalis.driverparser.postprocessor

import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.model.DriverResults
import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevices
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo
import me.urielsalis.dxdiaglib.model.postprocessor.PostProcessor
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser

@RequiredParser(DisplayDevicesParser::class, SystemInfoParser::class)
abstract class GenericDriverPostProcessor : PostProcessor {
    override fun process(dxdiag: Dxdiag): Dxdiag {
        val displayDevices = dxdiag["Display Devices"] as DisplayDevices
        val systemInfo = dxdiag["System Information"] as SystemInfo
        val devices = displayDevices.devices.filter { matchesCondition(it.manufacturer!!) }
        if (devices.isEmpty()) {
            return dxdiag
        }
        //parse driver
        val drivers: Map<String, DriverDownload> = devices
                .map {
                    val driver = searchDriver(it, systemInfo.os!!) ?: return@map null
                    return@map Pair(it.fullName!!, driver)
                }
                .filterNotNull()
                .toMap()

        if (!drivers.isEmpty()) {
            dxdiag.extras[getName()] = DriverResults(drivers)
        }
        return dxdiag
    }

    abstract fun matchesCondition(manufacturer: String): Boolean
    abstract fun searchDriver(displayDevice: DisplayDevice, os: String): DriverDownload?
}