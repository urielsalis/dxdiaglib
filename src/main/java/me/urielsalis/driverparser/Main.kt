package me.urielsalis.driverparser

import me.urielsalis.driverparser.driver.AMD
import me.urielsalis.driverparser.driver.Intel
import me.urielsalis.driverparser.driver.Nvidia
import me.urielsalis.driverparser.model.DriverResults
import me.urielsalis.driverparser.postprocessor.AMDDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.IntelArkDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.IntelDriverPostProcessor
import me.urielsalis.driverparser.postprocessor.NvidiaDriverPostProcessor
import me.urielsalis.dxdiaglib.DxdiagBuilder
import me.urielsalis.dxdiaglib.model.extradata.DisplayDevices
import me.urielsalis.dxdiaglib.model.extradata.SystemInfo
import me.urielsalis.dxdiaglib.parsers.DisplayDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemDevicesParser
import me.urielsalis.dxdiaglib.parsers.SystemInfoParser
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import org.pircbotx.Configuration
import org.pircbotx.PircBotX
import org.pircbotx.hooks.ListenerAdapter
import org.pircbotx.hooks.types.GenericMessageEvent
import java.net.URL


fun main(args: Array<String>) {
    Nvidia.init()
    AMD.init()
    Intel.refresh()
    val configuration = Configuration.Builder()
            .setName("Urielsalads")
            .setAutoReconnect(true)
            .addServer("irc.esper.net")
            .setRealName("Urielsalads")
            .addAutoJoinChannel("#mchelptraining")
            .addListener(MyListener())
            .buildConfiguration()

    //Create our bot with the configuration
    val bot = PircBotX(configuration)
    //Connect to the server
    bot.startBot()
}

class MyListener : ListenerAdapter() {
    override fun onGenericMessage(event: GenericMessageEvent?) {
        if (event!!.message.startsWith("?dx")) {
            val link = event.message.substring(4)
            val doc = Jsoup.parse(URL(link), 10000)
            val div = (doc.getElementsByClass("code").last().child(0).child(0).childNode(0) as TextNode).wholeText
            try {
                val dxdiag = DxdiagBuilder()
                        .of(div)
                        .withParser(SystemDevicesParser())
                        .withParser(DisplayDevicesParser())
                        .withParser(SystemInfoParser())
                        .withPostProcessor(NvidiaDriverPostProcessor())
                        .withPostProcessor(AMDDriverPostProcessor())
                        .withPostProcessor(IntelDriverPostProcessor())
                        .withPostProcessor(IntelArkDriverPostProcessor())
                        .parse()
                val displayDevices = dxdiag.get("Display Devices") as DisplayDevices
                val systemInfo = dxdiag.get("System Information") as SystemInfo
                val nvidia = dxdiag.get("Nvidia driver")
                val amd = dxdiag.get("AMD driver")
                val intel = dxdiag.get("Intel driver")
                val intelark = dxdiag.get("Intel ARK driver")
                println(link)
                println(displayDevices)
                println(systemInfo)
                println(amd)
                println(intel)
                println(intelark)
                println(nvidia)
                println()
                event.respond("OS: ${systemInfo.os}, CPU: ${systemInfo.cpu}, OEM: ${systemInfo.oem}")
                displayDevices.devices.forEach {
                    event.respond("Name: ${it.fullName}, Chip: ${it.chipType}")
                }
                if (amd is DriverResults) {
                    val download = amd.drivers.values.firstOrNull()
                    if (download != null) {
                        event.respond("AMD: ${download.deviceName} - ${download.downloadUrl}")
                    }
                }
                if (nvidia is DriverResults) {
                    val download = nvidia.drivers.values.firstOrNull()
                    if (download != null) {
                        event.respond("Nvidia: ${download.deviceName} - ${download.downloadUrl}")
                    }
                }
                if (intel is DriverResults) {
                    val download = intel.drivers.values.firstOrNull()
                    if (download != null) {
                        event.respond("Intel: ${download.deviceName} - ${download.downloadUrl}")
                    }
                }
                if (intelark is DriverResults) {
                    val download = intelark.drivers.values.firstOrNull()
                    if (download != null) {
                        event.respond("ARK: ${download.deviceName} - ${download.downloadUrl}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                event.respond("Exception occurred: ${e::class.simpleName}")
            }
        }
    }


}
