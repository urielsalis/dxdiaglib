package me.urielsalis.dxdiaglib.state

import me.urielsalis.dxdiaglib.model.Section
import me.urielsalis.dxdiaglib.model.SubSection

sealed class State(open val context: VariableContext)

data class VariableSectionEnd(override val context: VariableContext) : State(context) {
    fun startSection(it: String) = if (it.trim().startsWith("-")) {
        SectionStart(context)
    } else if (it.isBlank()) {
        VariableSectionEnd(context)
    } else {
        val newContext = VariableContext(
                context.sectionName,
                context.subSectionId,
                context.sections.apply { this[context.sectionName]!!.subSections.add(SubSection(mutableMapOf())) },
                0
        )
        val (key, value, parsedContext) = parseKeyValue(newContext, it)
        VariableRead(parsedContext, key, value)
    }
}

data class SectionStart(override val context: VariableContext) : State(context) {
    fun processSectionName(it: String) = SectionName(context, it.trim())
}

data class SectionName(override val context: VariableContext, val name: String) : State(context) {
    fun finishSection(it: String) = if (it.trim().startsWith("-")) {
        val newContext = VariableContext(
                name,
                0,
                context.sections.apply { put(name, Section(name, mutableListOf(SubSection(mutableMapOf())))) },
                0
        )
        SectionEnd(newContext)
    } else {
        SectionName(context, name + "\n" + it.trim())
    }
}

data class SectionEnd(override val context: VariableContext) : State(context) {
    fun verifyNextState(it: String) = if (it.isBlank()) {
        //Empty section, lets get to the next one
        InitialState(context)
    } else {
        val (key, value, parsedContext) = parseKeyValue(context, it)
        VariableRead(parsedContext, key, value)
    }
}

data class VariableRead(override val context: VariableContext, val key: String, val value: String) : State(context) {
    fun verifyNextState(it: String) = when {
        it.isBlank() -> {
            //We ended the subsection, possibly the entire section
            val newContext = VariableContext(
                    context.sectionName,
                    context.subSectionId + 1,
                    saveVariable(context, key, value),
                    0
            )
            VariableSectionEnd(newContext)
        }
        else -> {
            //Normal case, just continue as normal
            val (newkey, newvalue, newContext) = parseKeyValue(context, it)
            val parsedContext = VariableContext(
                    newContext.sectionName,
                    newContext.subSectionId,
                    saveVariable(newContext, key, value),
                    newContext.globalCounter
            )
            VariableRead(parsedContext, newkey, newvalue)
        }
    }
}
typealias InitialState = VariableSectionEnd


data class VariableContext(
        val sectionName: String,
        val subSectionId: Int = 0,
        val sections: MutableMap<String, Section> = mutableMapOf(),
        val globalCounter: Int = 0
)

fun parseKeyValue(context: VariableContext, it: String): Triple<String, String, VariableContext> = when {
    it.contains(":") -> {
        //Normal case, Key: Value
        val split = it.split(":").map { it.trim() }
        Triple(split[0], split[1], context)
    }
    it.trim()[0].isLetterOrDigit() -> {
        val newContext = VariableContext(
                context.sectionName,
                context.subSectionId,
                context.sections,
                context.globalCounter + 1
        )
        Triple(newContext.globalCounter.toString(), it.trim(), newContext)
    }
    else -> {
        val newContext = VariableContext(
                context.sectionName,
                context.subSectionId,
                context.sections,
                context.globalCounter + 1
        )
        if (!it.any { it.isLetter() }) {
            Triple(
                    newContext.globalCounter.toString(),
                    "",
                    newContext
            )
        } else {
            Triple(
                    newContext.globalCounter.toString(),
                    it.substring(it.indexOfFirst { it.isLetter() } - 1).trim(),
                    newContext
            )
        }
    }
}

fun saveVariable(context: VariableContext, key: String, value: String) = context
        .sections
        .apply {
            this[context.sectionName]!!.also { section ->
                section.subSections[context.subSectionId]
                        .variables
                        .also {
                            it[key] = value
                        }
            }

        }
