package me.urielsalis.dxdiaglib.parsers

import me.urielsalis.dxdiaglib.model.Dxdiag

interface DxdiagParser {
    fun parse(dxdiag: Dxdiag): Dxdiag
}
