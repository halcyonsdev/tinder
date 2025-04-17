plugins {
    id("java")
}

group = "com.halcyon.tinder"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-config-server:4.2.1")
    implementation("org.springframework.cloud:spring-cloud-starter-bus-kafka:4.2.1")
    runtimeOnly("org.springframework.cloud:spring-cloud-config-monitor:4.2.1")
}
