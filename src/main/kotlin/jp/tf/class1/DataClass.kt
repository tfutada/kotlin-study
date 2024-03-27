package jp.tf.class1

data class UserData(val name: String, val age: Int)

fun main() {
    val user1 = UserData("John Doe", 30)
    val user2 = UserData("John Doe", 30)

    println(user1)
    println("user1 == user2: ${user1 == user2}")

    // destructuring
    val (name, age) = user1
    println("name: $name, age: $age")
}

