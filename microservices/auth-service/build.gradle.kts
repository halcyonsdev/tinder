import com.google.protobuf.gradle.*

plugins {
    id("java")
    id("com.google.protobuf") version "0.9.5"
}

group = "com.halcyon"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    val jjwtVersion = "0.12.6"
    val mapstructVersion = "1.6.3"

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    implementation("io.grpc:grpc-stub:1.71.0")
    implementation("io.grpc:grpc-protobuf:1.71.0")
    implementation("io.grpc:grpc-netty-shaded:1.71.0")
    implementation("com.google.protobuf:protobuf-java:4.30.2")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    implementation("org.springframework.security:spring-security-crypto")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")

    implementation(project(":common:redis-cache"))
    implementation(project(":common:jwt-core"))
    implementation(project(":common:exception-core"))
}

protobuf {
    protoc {
        artifact="com.google.protobuf:protoc:4.30.2"
    }
    plugins {
        id("grpc") {
            artifact="io.grpc:protoc-gen-grpc-java:1.71.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}
