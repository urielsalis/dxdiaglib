package me.urielsalis.driverparser.model

import me.urielsalis.dxdiaglib.model.extradata.ExtraData

data class DriverResults(val drivers: Map<String, DriverDownload>) : ExtraData
