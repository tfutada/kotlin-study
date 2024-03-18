package jp.tf.closures

import kotlinx.coroutines.*

fun main() = runBlocking {
    var counter = 3

    val sendMoneyJob = launch {
        sendMoneyWithLog2 {
            counter
        }
    }

    launch {
        counter = 9 // update it
        println("in launch: counter = $counter")
    }

    sendMoneyJob.join()
}

suspend fun sendMoneyWithLog2(f: () -> Int) {
    println("in sendMoneyWithLog")
    delay(3000) // Simulates some processing delay
    val money = f()
    println("send money: $money")
}
