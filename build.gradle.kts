plugins {
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
    id("java")
    kotlin("jvm") version "1.9.24" apply false
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

subprojects {
    if (project.path != ":microservices" && !project.path.startsWith(":common")) {
        apply(plugin = "org.springframework.boot")
    }

    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "java")

    group = "com.halcyon"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        val lombokVersion = "1.18.36"

        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.3"))

        implementation("org.springframework.boot:spring-boot-starter-actuator")

        implementation("org.projectlombok:lombok:$lombokVersion")
        annotationProcessor("org.projectlombok:lombok:$lombokVersion")

        implementation("org.springframework.boot:spring-boot-starter-web") {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
        }

        implementation("org.springframework.boot:spring-boot-starter-jetty")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            encoding("UTF-8")
            importOrder()
            removeUnusedImports()
            eclipse("4.21").configFile("${rootDir}/config/codestyle.xml")

            targetExclude("build/generated/**", "build/generated/source/proto/**")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

allprojects {
    repositories {
        mavenCentral()
    }
}