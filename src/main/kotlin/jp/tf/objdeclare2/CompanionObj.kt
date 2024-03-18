class MyClass1 {
    companion object Named {
        val x = 10

        fun foo() {
            println("Companion object")
        }
    }

    fun bar() {
        foo()
    }
}

fun main() {
    println(MyClass1.x) // class variable
    MyClass1.foo() // class method
    MyClass1().bar() // object method
}
