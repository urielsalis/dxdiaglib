package me.urielsalis.driverparser

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import me.urielsalis.driverparser.driver.AMD
import me.urielsalis.driverparser.driver.Intel
import me.urielsalis.driverparser.driver.Nvidia
import me.urielsalis.driverparser.postprocessor.AMDDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.IntelArkDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.IntelDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.NvidiaDriverPostProcessor
import me.urielsalis.dxdiaglib.DxdiagBuilder
import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import java.io.File
import java.net.URL

val result: MutableMap<String, Pair<String, Dxdiag?>> = mutableMapOf()

fun main(args: Array<String>) {
    Nvidia.init()
    AMD.init()
    Intel.init()
    val dxdiags = File("dxdiags").readLines()
    dxdiags.forEachIndexed { i, it ->
        val parts = it.split(" ")
        val date = parts[0]
        val link = parts[1]
        println("$i/${dxdiags.size}")
        try {
            val doc = Jsoup.parse(URL(link), 10000)
            val div = (doc.getElementsByClass("code").last().child(0).child(0).childNode(0) as TextNode).wholeText
            val dxdiag = DxdiagBuilder()
                    .of(div)
                    .withParser(SystemInfoParser())
                    .withParser(DisplayDevicesParser())
                    .withParser(SystemDevicesParser())
                    .withPostProcessor(NvidiaDriverPostProcessor())
                    .withPostProcessor(AMDDriverPostProcessor())
                    .withPostProcessor(IntelDriverPostProcessor())
                    .withPostProcessor(IntelArkDriverPostProcessor())
                    .parse()
            result[link] = date to dxdiag
        } catch (e: Exception) {
            result[link] = date to null
        }
    }
    val mapper = jacksonObjectMapper()
    mapper.writeValue(File("result.json"), result)
}