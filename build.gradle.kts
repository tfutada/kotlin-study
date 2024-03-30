val kotlin_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
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

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}