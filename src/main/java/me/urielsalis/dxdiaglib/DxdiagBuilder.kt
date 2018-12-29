package me.urielsalis.dxdiaglib

import me.urielsalis.dxdiaglib.model.Dxdiag
import me.urielsalis.dxdiaglib.model.postprocessor.PostProcessor
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParser
import me.urielsalis.dxdiaglib.model.postprocessor.RequiredParserNotFound
import me.urielsalis.dxdiaglib.parsers.DxdiagParser
import me.urielsalis.dxdiaglib.state.*
import kotlin.reflect.full.findAnnotation

class DxdiagBuilder {
    var content = ""
    var parsers = mutableListOf<DxdiagParser>()
    var postProcessors = mutableListOf<PostProcessor>()

    fun of(text: String) = apply {
        this.content = text
    }

    fun withParser(parser: DxdiagParser) = apply {
        this.parsers.add(parser)
    }

    fun withPostProcessor(postProcessor: PostProcessor) = apply {
        postProcessors.add(postProcessor)
    }

    fun parse(): Dxdiag {
        var dxdiag = parseDxdiag(content)

        parsers.forEach {
            dxdiag = it.parse(dxdiag)
        }

        postProcessors.forEach {
            if (parsers.map { it::class }.containsAll(it::class.findAnnotation<RequiredParser>()?.value?.toList().orEmpty())) {
                dxdiag = it.process(dxdiag)
            } else {
                throw RequiredParserNotFound()
            }
        }

        return dxdiag
    }

    private fun parseDxdiag(dxdiag: String): Dxdiag {
        var currentState: State = InitialState(VariableContext("root"))
        dxdiag.lines().forEach {
            when (currentState) {
                is InitialState -> {
                    currentState = (currentState as InitialState).startSection(it)
                }
                is SectionStart -> {
                    currentState = (currentState as SectionStart).processSectionName(it)
                }
                is SectionName -> {
                    currentState = (currentState as SectionName).finishSection(it)
                }
                is SectionEnd -> {
                    currentState = (currentState as SectionEnd).verifyNextState(it)
                }
                is VariableRead -> {
                    currentState = (currentState as VariableRead).verifyNextState(it)
                }
            }
        }
        return Dxdiag(currentState.context.sections, mutableMapOf())
    }

}
