plugins {
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.0.2"
    id("java")
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

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            encoding("UTF-8")
            importOrder()
            removeUnusedImports()
            eclipse("4.21").configFile("${rootDir}/config/codestyle.xml")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}