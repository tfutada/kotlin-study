package jp.tf.arrays

fun main() {
    var riversArray = arrayOf("Nile", "Amazon", "Yangtze")

    // Using the += assignment operation creates a new riversArray,
    // copies over the original elements and adds "Mississippi"
    riversArray += "Mississippi"

    riversArray[0] = "Congo"
    println(riversArray.joinToString())
}


