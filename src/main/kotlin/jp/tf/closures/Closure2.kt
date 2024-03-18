package jp.tf.closures

fun main() {

    var counter = 3

    sendMoneyWithLog {
        counter
    }
}


fun sendMoneyWithLog(f: () -> Int) {
    println("in getMoneyWithLog")
    val money = f()
    println("send money: $money")
}

