plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
    application
}

application {
    mainClass.set("icu.takeneko.omms.central.main.Main")
}

tasks {
    shadowJar {
        archiveClassifier.set("full")
        relocate("com.google.gson","icu.takeneko.deps.gson")
    }
}

repositories{
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
}

java.sourceCompatibility = JavaVersion.VERSION_17