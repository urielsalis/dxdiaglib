package me.urielsalis.driverparser.driver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.util.KotlinPairKeySerializerModule
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import org.jsoup.Jsoup
import java.io.File
import java.net.URL

object Intel : DriverFinder {
    var downloads = mutableMapOf<String, String>()

    override fun search(device: DisplayDevice, os: String): DriverDownload? {
        var fullName = device.fullName!!.replace("(R)", "").replace("®", "").substringBefore("(").trim()
        fullName = fullName.split(" ").map {
            if (it.contains("/")) {
                it.split("/")[0]
            } else {
                it
            }
        }.joinToString(" ")
        var url = downloads[fullName]
        if (url == null) {
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            fullName = fullName.replace("Intel ", "")
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            fullName = fullName.replace("HD ", "")
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            fullName = fullName.replace("Graphics ", "")
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            return null
        }
        return DriverDownload(device.fullName, os, url)
    }

    override fun init() {
        val mapper = jacksonObjectMapper().registerModule(KotlinPairKeySerializerModule())
        val file = File("intel.json")
        if (file.exists()) {
            downloads = mapper.readValue(file)
        } else {
            val dchCases = Jsoup
                    .parse(URL("https://www.intel.com/content/www/us/en/support/articles/000031275/graphics-drivers.html"), 10000)
                    .getElementById("blade-product-list-show-content")
                    .getElementsByTag("a")
                    .map { removeSpecialCharacters(it.text()) }

            val document = Jsoup.parse(URL("http://www.intel.com/content/www/us/en/support/graphics-drivers.html"), 10000)
            downloads = IntRange(0, 8)
                    .map { document.getElementById("childProduct_$it") }
                    .map { it.getElementsByTag("a").map { getLink(it.attr("href")) to removeSpecialCharacters(it.text()) } }
                    .flatten()
                    .map { it.second to it.first }
                    .map {
                        if (it.first in dchCases) {
                            it.first to it.second + " - DCH!"
                        } else {
                            it
                        }
                    }
                    .toMap().toMutableMap()
            mapper.writeValue(file, downloads)
        }
    }

    private fun getLink(text: String): String {
        return "https://downloadcenter.intel.com/product/" + text.split("/")[7]
    }

    private fun removeSpecialCharacters(text: String): String {
        return text.replace("®", "").replace("™", "").replace("Graphics Drivers for ", "").trim()
    }

    override fun refresh() {
        downloads.clear()
        File("intel.json").delete()
        init()
    }
}
