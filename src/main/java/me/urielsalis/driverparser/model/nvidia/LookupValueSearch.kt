package me.urielsalis.driverparser.model.nvidia

import com.fasterxml.jackson.annotation.JsonProperty

data class LookupValueSearch(
        @get:JsonProperty("LookupValues") val lookupValues: MutableList<LookupValue> = mutableListOf()
)