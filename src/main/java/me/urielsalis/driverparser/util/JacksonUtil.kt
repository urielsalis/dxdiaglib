package me.urielsalis.driverparser.util

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.KeyDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule

class KotlinPairKeyDeserializer : KeyDeserializer() {
    override fun deserializeKey(key: String, context: DeserializationContext): Any {
        return if (key.startsWith('(') && key.endsWith(')')) {
            val parts = key.substring(1, key.length - 1).split(", ")
            if (parts.size != 2) {
                throw IllegalStateException("Pair() expects a serialized format of '(first,second)', cannot understand '$key'")
            }
            Pair(parts[0], parts[1])
        } else {
            throw IllegalStateException("Pair() expects a serialized format of '(first,second)', cannot understand '$key'")
        }
    }
}

class KotlinPairKeySerializerModule : SimpleModule() {
    init {
        addKeyDeserializer(Pair::class.java, KotlinPairKeyDeserializer())
    }
}
