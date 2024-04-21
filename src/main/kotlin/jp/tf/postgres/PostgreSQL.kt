package jp.tf.postgres


import java.sql.Connection
import java.sql.DriverManager

val postgresServer = System.getenv("POSTGRES_SERVER") ?: "jdbc:postgresql://localhost:5432/postgres?currentSchema=testschema1"
val postgresUser = System.getenv("POSTGRES_USER") ?: "postgres"
val postgresPassword = System.getenv("POSTGRES_PASSWORD") ?: "postgres"

class PostgresClient(private val url: String, private val user: String, private val password: String) {
    private var connection: Connection? = null

    fun connect() {
        try {
            connection = DriverManager.getConnection(url, user, password)
            println("Connected to the PostgreSQL server successfully.")
        } catch (e: Exception) {
            println("Connection failure: ${e.message}")
        }
    }

    fun disconnect() {
        try {
            connection?.close()
            println("Disconnected from the PostgreSQL server.")
        } catch (e: Exception) {
            println("Disconnection failure: ${e.message}")
        }
    }

    fun executeQuery(query: String): List<Map<String, Any>> {
        val results = mutableListOf<Map<String, Any>>()
        try {
            val statement = connection?.createStatement()
            val resultSet = statement?.executeQuery(query)
            val metaData = resultSet?.metaData
            val columnCount = metaData?.columnCount ?: 0

            while (resultSet?.next() == true) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..columnCount) {
                    metaData?.getColumnName(i)?.let { columnName ->
                        row[columnName] = resultSet.getObject(i)
                    }
                }
                results.add(row)
            }
        } catch (e: Exception) {
            println("Query execution failure: ${e.message}")
        }
        return results
    }

    fun executeUpdate(sql: String): Int {
        return try {
            val statement = connection?.createStatement()
            val result = statement?.executeUpdate(sql) ?: 0
            println("Update executed successfully; $result rows affected.")
            result
        } catch (e: Exception) {
            println("Update execution failure: ${e.message}")
            0
        }
    }
}

fun main() {
    val url = "jdbc:postgresql://${postgresServer}/postgres?currentSchema=testschema1"
    val client = PostgresClient(url, postgresUser, postgresPassword)
    client.connect()
    // Example query
    val users = client.executeQuery("SELECT * FROM user1")
    println(users)
    // Example update
    client.executeUpdate("UPDATE user1 SET name = 'John Doe' WHERE id = 1")
    client.disconnect()
}
