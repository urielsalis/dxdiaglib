package me.urielsalis.dxdiaglib.model.extradata

data class SystemInfo(
        val reportTime: String?,
        val machineName: String?,
        val os: String?,
        val language: String?,
        val oem: String?,
        val model: String?,
        val cpu: String?,
        val ram: String?,
        val windowsDir: String?
) : ExtraData
