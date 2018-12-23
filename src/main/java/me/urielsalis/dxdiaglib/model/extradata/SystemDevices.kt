package me.urielsalis.dxdiaglib.model.extradata

data class SystemDevices(val systemDevices: List<SystemDevice>) : ExtraData
data class SystemDevice(val name: String?, val pciID: String?, val drivers: List<String>) : ExtraData
