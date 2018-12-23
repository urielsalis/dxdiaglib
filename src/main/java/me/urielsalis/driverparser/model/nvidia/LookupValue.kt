package me.urielsalis.driverparser.model.nvidia

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class LookupValue(
        @get:JsonProperty("Name") val name: String = "",
        @get:JsonProperty("Value") val value: Int = 0
)
