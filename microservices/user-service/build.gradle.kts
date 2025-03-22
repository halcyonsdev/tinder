plugins {
    id("java")
    id("org.flywaydb.flyway") version "8.4.2"
}

group = "com.halcyon"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    val jjwtVersion = "0.12.6"
    val mapstructVersion = "1.6.3"

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    implementation(project(":common:redis-cache"))
}

flyway {
    url = "jdbc:postgresql://${project.findProperty("postgresHost")}:${project.findProperty("postgresPort")}/${project.findProperty("postgresDatabase")}"
    user = project.findProperty("postgresUser") as String
    password = project.findProperty("postgresPassword") as String
    locations = arrayOf("classpath:db/migration")
}