import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.7.22"
    application
}

group = "me.slucana08"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-runtime", "1.0-M1-1.4.0-rc")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")
    implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.9.20")
    implementation("io.ksmt:ksmt-core:0.5.6")
    implementation("io.ksmt:ksmt-z3:0.5.6")
    implementation("org.jgrapht:jgrapht-core:1.5.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("MainKt")
}
