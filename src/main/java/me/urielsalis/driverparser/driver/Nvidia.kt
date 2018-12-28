package me.urielsalis.driverparser.driver

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.model.nvidia.LookupValue
import me.urielsalis.driverparser.model.nvidia.LookupValueSearch
import me.urielsalis.driverparser.util.KotlinPairKeySerializerModule
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import java.io.File
import java.net.URL

object Nvidia : DriverFinder {
    var downloads = mutableMapOf<Pair<String, String>, String>()

    override fun search(device: DisplayDevice, os: String): DriverDownload? {
        val parsedOs = parseOS(os)
        val name = if (device.chipType!!.endsWith("GB")) {
            val split = device.chipType.split(" ")
            split.subList(0, split.size - 1).joinToString(" ")
        } else {
            device.chipType
        }
        val url = downloads[name to parsedOs] ?: return null
        return DriverDownload(device.chipType, parsedOs, url)
    }

    private fun parseOS(os: String): String {
        var name = "10"
        when {
            os.contains("XP") -> name = "XP"
            os.contains("Vista") -> name = "Vista"
            os.contains("7") -> name = "7"
            os.contains("8") -> name = "8"
            os.contains("8.1") -> name = "8.1"
            os.contains("10") -> name = "10"
        }
        return if (os.contains("64-bit")) {
            "Windows $name 64-bit"
        } else if (os.contains("32-bit")) {
            "Windows $name 32-bit"
        } else if (name == "XP") {
            "Windows XP"
        } else {
            ""
        }
    }

    override fun init() {
        val mapper = jacksonObjectMapper().registerModule(KotlinPairKeySerializerModule())
        val file = File("nvidia.json")
        if (file.exists()) {
            downloads = mapper.readValue(file)
        } else {
            //First step: Reading types(Geforce/Quadro/etc)
            "https://www.nvidia.com/Download/API/lookupValueSearch.aspx?TypeID=1".parseAndLoop {
                //Second step: Reading series
                "https://www.nvidia.com/Download/API/lookupValueSearch.aspx?TypeID=2&ParentID=${it.value}".parseAndLoop {
                    //Third step: Get OS supported by GPU
                    val supportedOS = "https://www.nvidia.com/Download/API/lookupValueSearch.aspx?TypeID=4&ParentID=${it.value}"
                            .parse()
                            .map { Pair(it.name, it.value) }
                            .toMap()

                    //Fourth step: Getting specific GPUs in series
                    "https://www.nvidia.com/Download/API/lookupValueSearch.aspx?TypeID=3&ParentID=${it.value}".parseAndLoop {
                        //Last step: Get download link
                        supportedOS.forEach { osName, osid ->
                            downloads[Pair(it.name, osName)] = URL("https://www.nvidia.com/Download/processDriver.aspx?pfid=${it.value}&osid=$osid&lang=en-us")
                                    .openConnection()
                                    .getInputStream()
                                    .bufferedReader()
                                    .readText()
                        }
                    }
                }
            }
            mapper.writeValue(file, downloads)
        }
    }

    override fun refresh() {
        downloads.clear()
        init()
    }

    private fun String.parse(): List<LookupValue> {
        val xmlMapper = XmlMapper()
        return try {
            val lookupValueSearch = xmlMapper.readValue(URL(this).readText(), LookupValueSearch::class.java)
            lookupValueSearch.lookupValues
        } catch (e: MismatchedInputException) {
            emptyList()
        }
    }

    private fun String.parseAndLoop(function: (LookupValue) -> Unit) {
        this.parse().forEach { function(it) }
    }
}