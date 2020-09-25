package tech.kaxon.projects.bot.gson

import com.google.gson.*
import java.lang.reflect.Type


internal class InterfaceAdapter<T : Any> : JsonSerializer<T>, JsonDeserializer<T> {
    override fun serialize(obj: T, interfaceType: Type?, context: JsonSerializationContext): JsonElement? {
        val wrapper = JsonObject()
        wrapper.addProperty("type", obj::class.java.name)
        wrapper.add("data", context.serialize(obj))
        return wrapper
    }

    @Throws(JsonParseException::class)
    override fun deserialize(elem: JsonElement, interfaceType: Type, context: JsonDeserializationContext): T {
        val wrapper = elem as JsonObject
        val typeName = get(wrapper, "type")
        val data = get(wrapper, "data")
        val actualType: Type = typeForName(typeName)
        return context.deserialize(data, actualType)
    }

    private fun typeForName(typeElem: JsonElement): Type {
        return try {
            Class.forName(typeElem.asString)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e)
        }
    }

    private operator fun get(wrapper: JsonObject, memberName: String): JsonElement {
        return wrapper[memberName] ?: throw JsonParseException("No '$memberName' member found in what was expected to be an interface wrapper")
    }
}