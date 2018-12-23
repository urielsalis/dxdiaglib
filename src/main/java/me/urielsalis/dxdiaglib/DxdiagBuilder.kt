package me.urielsalis.dxdiaglib

import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.extradata.KeyValueSection
import me.urielsalis.dxdiaglib.model.extradata.ListSection
import me.urielsalis.dxdiaglib.model.extradata.Section
import me.urielsalis.dxdiaglib.model.postprocessor.PostProcessor
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParserNotFound
import me.urielsalis.dxdiaglib.parsers.DxdiagParser

class DxdiagBuilder {
    var content = ""
    var parsers = mutableListOf<DxdiagParser>()
    var postProcessors = mutableListOf<PostProcessor>()

    fun of(text: String): DxdiagBuilder {
        this.content = text
        return this
    }

    fun withParser(parser: DxdiagParser): DxdiagBuilder {
        parsers.add(parser)
        return this
    }

    fun parse(): Dxdiag {
        val sections = parseFormat()
        var dxdiag = Dxdiag(sections = sections, extraData = mutableMapOf(), parsers = parsers)

        parsers.forEach { dxdiag.extraData[it.getName()] = it.parse(dxdiag.sections) }
        val parsersClasses = parsers.map { it::class }

        postProcessors.forEach {
            val required = it::class.annotations.filterIsInstance<RequiredParser>().firstOrNull()?.value?.toList().orEmpty()
            if (parsersClasses.containsAll(required)) {
                dxdiag = it.process(dxdiag)
            } else {
                throw RequiredParserNotFound()
            }
        }
        return dxdiag
    }

    private fun parseFormat(): MutableMap<String, MutableList<Section>> {
        var parsingSectionName = false
        var endSection = false
        var parsingSection = false
        var sectionName = ""
        var sectionContent = mutableListOf<String>()
        var subsections = mutableListOf<Section>()
        val sections = mutableMapOf<String, MutableList<Section>>()

        content.lines().forEach {
            when {
                parsingSectionName -> {
                    sectionName = it
                    endSection = true
                    parsingSectionName = false
                }
                it.trim().startsWith("-") && it.trim().endsWith("-") -> when {
                    endSection -> {
                        endSection = false
                        parsingSection = true
                    }
                    parsingSection -> {
                        parsingSection = false
                        parsingSectionName = true
                        sections[sectionName] = subsections
                        subsections = mutableListOf()
                        sectionContent = mutableListOf()
                        sectionName = ""
                    }
                    else -> {
                        parsingSectionName = true
                    }
                }
                parsingSection -> when {
                    it.isBlank() -> {
                        if (sectionContent.any { !it.contains(":") }) {
                            subsections.add(ListSection(sectionContent))
                        } else {
                            val temp = mutableMapOf<String, MutableList<String>>()
                            sectionContent.forEach {
                                var key = it.substringBefore(':').trim()
                                val value = it.substringAfter(':').trim()
                                if (key == value || key.startsWith("|")) {
                                    key = temp.size.toString()
                                }
                                if (temp.containsKey(key)) {
                                    temp[key]!!.add(value)
                                } else {
                                    temp[key] = mutableListOf(value)
                                }
                            }
                            subsections.add(KeyValueSection(temp))
                        }
                        sectionContent = mutableListOf()
                    }
                    else -> sectionContent.add(it)
                }
            }
        }
        return sections
    }

    fun withPostProcessor(postProcessor: PostProcessor): DxdiagBuilder {
        postProcessors.add(postProcessor)
        return this
    }
}