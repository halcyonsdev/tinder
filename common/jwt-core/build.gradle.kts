plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.lombok") version "1.9.24"
}

group = "com.halcyon.tinder"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    val jjwtVersion = "0.12.6"

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation(project(":common:redis-cache"))
    implementation(project(":common:exception-core"))
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}
