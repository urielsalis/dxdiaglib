package me.urielsalis.driverparser.postprocessor

import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.model.DriverResults
import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevices
import me.urielsalis.dxdiaglib.model.extradata.NullData
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo
import me.urielsalis.dxdiaglib.model.postprocessor.PostProcessor

abstract class GenericDriverPostProcessor : PostProcessor {
    override fun process(dxdiag: Dxdiag): Dxdiag {
        val displayDevices = dxdiag.get("Display Devices") as DisplayDevices
        val systemInfo = dxdiag.get("System Information") as SystemInfo
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

        if (drivers.isEmpty()) {
            dxdiag.extraData[getName()] = NullData()
        } else {
            dxdiag.extraData[getName()] = DriverResults(drivers)
        }
        return dxdiag
    }

    abstract fun matchesCondition(manufacturer: String): Boolean
    abstract fun searchDriver(displayDevice: DisplayDevice, os: String): DriverDownload?
}