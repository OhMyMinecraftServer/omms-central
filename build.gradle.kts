import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    kotlin("jvm") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    application
}

tasks{
    shadowJar {
        archiveClassifier.set("full")
    }
}

//tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "17"
//}

application {
    mainClass.set("net.zhuruoling.omms.central.main.Main")
}


group = "org.zhuruoling"
version = "0.12.3"
description = "omms-central"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://libraries.minecraft.net")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-releases/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven{
        url = uri("https://jcenter.bintray.com/")
    }
}

dependencies {
    implementation("io.ktor:ktor-server-auth:2.0.2")
    implementation("io.ktor:ktor-server-auth-jvm:2.0.2")
    implementation("uk.org.lidalia:sysout-over-slf4j:1.0.2")
    implementation("org.jline:jline:3.21.0")
    implementation("com.mojang:brigadier:1.0.18")
    implementation("io.ktor:ktor-server-call-logging-jvm:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.0.2")
    implementation("io.ktor:ktor-server-core-jvm:2.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.0.2")
    implementation("io.ktor:ktor-serialization-gson-jvm:2.0.2")
    implementation("io.ktor:ktor-server-netty-jvm:2.0.2")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("ch.qos.logback:logback-classic:1.2.11")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("io.netty:netty-all:4.1.77.Final")
    implementation("com.github.oshi:oshi-core:6.1.6")
    implementation("net.java.dev.jna:jna:5.11.0")
    implementation("net.java.dev.jna:jna-platform:5.11.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.0.2")
    implementation("io.ktor:ktor-server-content-negotiation:2.0.2")
    implementation("me.xdrop:fuzzywuzzy:1.4.0")
    implementation("io.ktor:ktor-client-core-jvm:2.1.2")
    implementation("io.ktor:ktor-client-cio:2.1.2")
    implementation("io.ktor:ktor-client-cio-jvm:2.1.2")
    implementation("io.ktor:ktor-client-websockets:2.1.2")
    implementation("io.ktor:ktor-client-auth-jvm:2.1.2")
    implementation("io.ktor:ktor-client-auth:2.1.2")
    implementation("commons-io:commons-io:2.11.0")
    implementation("cn.hutool:hutool-all:5.8.11")
    implementation("io.ktor:ktor-http:2.2.3")
    implementation("com.github.gotson:sqlite-jdbc:3.32.3.8")
    implementation("io.ktor:ktor-client-serialization:2.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("io.ktor:ktor-server-websockets-jvm:2.0.2")
    implementation("org.jetbrains.pty4j:pty4j:0.12.10")
    implementation("org.apache.groovy:groovy:4.0.10")
    implementation("io.socket:socket.io-client:2.1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.10")
}

fun generateProperties(){
    val propertiesFile = file("./src/main/resources/build.properties")
    if (propertiesFile.exists()) {
        propertiesFile.delete()
    }
    propertiesFile.createNewFile()
    val m = mutableMapOf<String, String>()
    propertiesFile.printWriter().use {writer ->
        properties.forEach {
            val str = it.value.toString()
            if ("@" in str || "(" in str || ")" in str || "extension" in str || "null" == str || "\'" in str || "\\" in str || "/" in str)return@forEach
            if ("PROJECT" in str.toUpperCaseAsciiOnly() || "PROJECT" in it.key.toUpperCaseAsciiOnly() || " " in str)return@forEach
            if ("GRADLE" in it.key.toUpperCaseAsciiOnly() || "GRADLE" in str.toUpperCaseAsciiOnly() || "PROP" in it.key.toUpperCaseAsciiOnly())return@forEach
            if("." in it.key || "TEST" in it.key.toUpperCaseAsciiOnly())return@forEach
            if(it.value.toString().length <= 2)return@forEach
            m += it.key to str
        }
        m += "buildTime" to System.currentTimeMillis().toString()
        m.toSortedMap().forEach{
            writer.println("${it.key} = ${it.value}")
        }
    }
}

generateProperties()