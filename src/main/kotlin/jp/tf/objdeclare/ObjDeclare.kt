package jp.tf.objdeclare

object ObjDeclare {
    var count = 0

    fun increment() {
        count++
    }
}

fun main() {
    val c = ObjDeclare.increment()
    println(ObjDeclare.count) // 1
}

