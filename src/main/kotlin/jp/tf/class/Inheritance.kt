package jp.tf.`class`

sealed class Base(var p: Int) {
    open fun dump(): String {
        return "Base(p=$p)"
    }

}

class Derived(p: Int) : Base(p) {
    override fun dump(): String {
        return "Derived(p=$p)"
    }
}

fun main() {
    val d = Derived(1)
    println(d.dump())
}

