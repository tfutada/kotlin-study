package jp.tf.jp.tf.delegate

interface Task {
    fun perform()
}

class Developer : Task {
    override fun perform() {
        println("Writing code")
    }
}

class Tester : Task {
    override fun perform() {
        println("Testing code")
    }
}

class ProjectManager(task: Task) : Task by task

fun main() {
    val developer = Developer()
    val tester = Tester()
    val managerForDevelopment = ProjectManager(developer)
    val managerForTesting = ProjectManager(tester)

    managerForDevelopment.perform() // Outputs: Writing code
    managerForTesting.perform()     // Outputs: Testing code
}
