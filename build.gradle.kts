val kotlin_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.9"
}

group = "jp.tf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val kotlinSdkVersion = "1.0.41"

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    implementation("aws.sdk.kotlin:s3:$kotlinSdkVersion")
    implementation("io.ktor:ktor-network:$ktor_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("jp.tf.udp.server.UDPServerKt")
}