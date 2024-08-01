
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
}

group = "example.com"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.server)
    implementation(libs.serverCore)
    implementation(libs.websockets)
    implementation(libs.serverCallLogging)
    implementation(libs.contentNegotiation)
    implementation(libs.serialization)
    implementation(libs.serializationKotlinxJson)
    implementation(libs.serverNetty)
    implementation(libs.logbackClassic)
    implementation(libs.serverConfigYaml)
    testImplementation(libs.serverTests)
    testImplementation(libs.kotlinTestJunit)

    implementation(libs.gson)

    implementation(libs.koinCore)
    implementation(libs.koinKtor)
    implementation(libs.koinLoggerSlf4j)
}