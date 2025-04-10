plugins {
    id("java")
}

group = "com.halcyon"
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
