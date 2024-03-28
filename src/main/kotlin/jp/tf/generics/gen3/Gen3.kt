package jp.tf.jp.tf.generics.gen3

fun <T> T.protected(block: T.() -> Unit) {
    block()
}

fun main() {
    val myList = mutableListOf(1, 2, 3)

    // Using the 'protected' extension function to modify the list within a block
    myList.protected {
        add(4)
        removeAt(0)
    }

    println(myList) // Output: [2, 3, 4]
}