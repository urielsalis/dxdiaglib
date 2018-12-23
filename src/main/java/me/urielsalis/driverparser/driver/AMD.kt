package me.urielsalis.driverparser.driver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import me.urielsalis.driverparser.model.DriverDownload
import me.urielsalis.driverparser.util.KotlinPairKeySerializerModule
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevice
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import java.io.File
import java.net.URL

object AMD : DriverFinder {
    var downloads = mutableMapOf<String, String>()

    override fun search(device: DisplayDevice, os: String): DriverDownload? {
        var fullName = device.fullName!!.substring(4)
        var url = downloads[fullName]
        if (url == null) {
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            fullName = fullName.substring(0, fullName.length - 1)
            url = downloads.filterKeys { it.contains(fullName, true) }.values.firstOrNull()
        }
        if (url == null) {
            fullName = fullName.replace("HD ", "")
            url = downloads.filterKeys { it.contains(fullName, true) && it.contains("ATI") && it.contains("Series") }.values.firstOrNull()
        }
        if (url == null) {
            return null
        }
        return DriverDownload(device.fullName, os, url)
    }

    override fun init() {
        val mapper = jacksonObjectMapper().registerModule(KotlinPairKeySerializerModule())
        val file = File("amd.json")
        if (file.exists()) {
            downloads = mapper.readValue(file)
        } else {
            // First, scrapping AMD to get a list of autocomplete ids
            val doc = Jsoup.parse(URL("https://www.amd.com/en/support"), 10000)
            val autocomplete = doc.getElementById("support_autocomplete")
            autocomplete
                    .children()
                    .map { it.attr("value") to (it.childNode(0) as TextNode).text() }
                    .filter { it.first.isNotEmpty() }
                    .forEach {
                        downloads[it.second] = "https://www.amd.com" + mapper.readTree(URL("https://www.amd.com/rest/support_alias/en/${it.first}"))["link"].textValue()
                    }
            mapper.writeValue(file, downloads)
        }
    }

    override fun refresh() {
        downloads.clear()
        init()
    }
}
