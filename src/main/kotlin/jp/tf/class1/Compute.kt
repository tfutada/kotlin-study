package jp.tf.class1

class UserProfile {
    var name: String = "Unknown"
    var age: Int = 0

    var stringRepresentation: String
        get() = "$name, $age"
        set(value) {
            setDataFromString(value)
        }

    private fun setDataFromString(value: String) {
        // Example format: "John Doe, 30"
        val parts = value.split(", ")
        if (parts.size == 2) {
            name = parts[0]
            age = parts[1].toIntOrNull() ?: 0 // Safely parse the integer, defaulting to 0 if parsing fails
        }
    }
}

fun main() {
    val userProfile = UserProfile()

    // Initial values
    println(userProfile.stringRepresentation) // Outputs: "Unknown, 0"

    // Updating stringRepresentation
    userProfile.stringRepresentation = "John Doe, 30"
    println(userProfile.stringRepresentation) // Outputs: "John Doe, 30"

    // Updating stringRepresentation again
    userProfile.stringRepresentation = "Jane Doe, 25"
    println(userProfile.stringRepresentation) // Outputs: "Jane Doe, 25"
}