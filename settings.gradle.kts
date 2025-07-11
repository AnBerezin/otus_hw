rootProject.name = "otus_hw"
include("hw01-gradle")
include("hw02-collections")
include("hw03-testFramework")
include("hw04-gc:homework")
include("hw05-aop")
include("hw06-atm")
include("hw07-patterns")
include("hw08-serialization")
include("hw09-orm")
include("hw10-jpql")
include("hw11-cache")

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.gradleup.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
        id("name.remal.sonarlint") version sonarlint
        id("com.diffplug.spotless") version spotless
    }
}