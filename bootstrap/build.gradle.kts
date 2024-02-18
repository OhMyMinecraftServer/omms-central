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
    }
}

dependencies {

}

java.sourceCompatibility = JavaVersion.VERSION_17