package jp.tf.sam

fun interface IntPredicate {
    fun accept(i: Int): Boolean
}

fun main() {
    // Creating an instance of a class
    val isEven = object : IntPredicate {
        override fun accept(i: Int): Boolean {
            return i % 2 == 0
        }
    }
    println(isEven.accept(4))
    println(isEven.accept(5))

    // Using a lambda
    val isEven2 = IntPredicate { it % 2 == 0 }
    println(isEven2.accept(4))
    println(isEven2.accept(5))
}

