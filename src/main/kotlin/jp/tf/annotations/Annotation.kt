package jp.tf.jp.tf.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerializableProperties(val properties: Array<String>)

@SerializableProperties(properties = ["id", "name"])
data class User(val id: Int, val name: String, val age: Int)

fun serialize(obj: Any): String {
    val kClass = obj::class

    val annotation = kClass.annotations.find { it is SerializableProperties }
    if (annotation == null) {
        throw IllegalArgumentException("Class is not annotated with @SerializableProperties")
    }

    val properties = (annotation as SerializableProperties).properties
    val propertiesToSerialize = annotation.properties.toSet()

    // Use reflection to fetch and filter the properties based on the annotation
    val propertiesMap = kClass.members
        .filter { it.name in propertiesToSerialize }
        .associate { it.name to it.call(obj) }

    return propertiesMap.toString() // Simplified serialization
}


fun main() {
    val user = User(id = 1, name = "John Doe", age = 30)
    val serialized = serialize(user)
    println(serialized)
}



