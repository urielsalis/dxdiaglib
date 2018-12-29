package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo

class SystemInfoParser : DxdiagParser {
    override fun parse(dxdiag: Dxdiag): Dxdiag {
        val section = (dxdiag["System Information"] as? Section) ?: return dxdiag
        val systemInfoSection = section.subSections.first()

        dxdiag.extras["System Information"] = SystemInfo(systemInfoSection["Time of this report"],
                systemInfoSection["Machine name"],
                systemInfoSection["Operating System"],
                systemInfoSection["Language"],
                systemInfoSection["System Manufacturer"],
                systemInfoSection["System Model"],
                systemInfoSection["Processor"],
                systemInfoSection["Memory"],
                systemInfoSection["Windows Dir"])
        return dxdiag
    }
}
