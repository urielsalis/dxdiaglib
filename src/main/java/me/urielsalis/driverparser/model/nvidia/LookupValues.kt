package me.urielsalis.driverparser.model.nvidia

import com.fasterxml.jackson.annotation.JsonProperty

data class LookupValues(
        @get:JsonProperty("LookupValue") val lookupValues: MutableList<LookupValue> = mutableListOf()
)
