package me.urielsalis.dxdiaglib.model.postprocessor

import me.urielsalis.dxdiaglib.parsers.DxdiagParser
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiredParser(vararg val value: KClass<out DxdiagParser>)
