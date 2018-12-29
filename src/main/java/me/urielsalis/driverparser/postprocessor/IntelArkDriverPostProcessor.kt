package me.urielsalis.driverparser.postprocessor

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.urielsalis.driverparser.driver.Intel
import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.model.DriverResults
import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo
import me.urielsalis.dxdiaglib.model.postprocessor.PostProcessor
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser
import java.io.File
import java.net.URL

@RequiredParser(SystemInfoParser::class)
class IntelArkDriverPostProcessor : PostProcessor {
    override fun process(dxdiag: Dxdiag): Dxdiag {
        val systemInfo = dxdiag["System Information"] as SystemInfo
        val cpu = systemInfo.cpu!!
        val split = cpu.split(" ")
        val cpuName = split.filter { it.length > 2 }.last { it[0].isLetter() && it[1].isDigit() }
        val mapper = jacksonObjectMapper()
        val tree = mapper.readTree(URL("https://odata.intel.com/API/v1_0/Products/Processors()?api_key=${File("arkkey").readText()}&\$select=GraphicsModel&\$filter=substringof(%27$cpuName%27,ProductName)&\$format=json"))
        val graphicsModel = tree["d"][0]["GraphicsModel"]?.textValue()

        if (graphicsModel == null) {
            dxdiag.extras[getName()] = DriverResults(mapOf(cpuName to DriverDownload(cpuName, "", "No iGPU")))
            return dxdiag
        }
        val result = findDriver(graphicsModel) ?: return dxdiag
        val drivers: Map<String, DriverDownload> = mapOf(graphicsModel to result)
        dxdiag.extras[getName()] = DriverResults(drivers)
        return dxdiag
    }

    private fun findDriver(graphicsModel: String): DriverDownload? {
        return Intel.search(DisplayDevice(graphicsModel), "")
    }

    override fun getName(): String {
        return "Intel ARK driver"
    }

}
