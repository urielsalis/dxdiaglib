package me.urielsalis.dxdiaglib.model.postprocessor

import me.urielsalis.dxdiaglib.model.Dxdiag


interface PostProcessor {
    fun process(dxdiag: Dxdiag): Dxdiag
    fun getName(): String
}
