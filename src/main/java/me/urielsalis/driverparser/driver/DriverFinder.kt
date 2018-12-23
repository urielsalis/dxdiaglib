package me.urielsalis.driverparser.driver

import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice

interface DriverFinder {
    fun init()
    fun refresh()
    fun search(device: DisplayDevice, os: String): DriverDownload?
}
