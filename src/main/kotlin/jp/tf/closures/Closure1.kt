package jp.tf.closures

fun main() {
    sendMoneyWithLog(8, { v ->
        v + 1
    })
}

fun sendMoneyWithLog(v: Int, f: (m: Int) -> Int) {
    println("in getMoneyWithLog")
    val money = f(v)
    println("send modey: $money")
}

