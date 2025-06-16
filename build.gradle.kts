plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"

    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.devtools.ksp") version "2.1.20-2.0.0"
}

group = "com.xermao"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter:0.9.93")
    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.h2database:h2:2.3.232")
    testRuntimeOnly("com.h2database:h2:2.3.232")
    ksp("org.babyfish.jimmer:jimmer-ksp:0.9.93")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    sourceSets.main {
        kotlin.srcDirs(
            "build/generated/ksp/main/kotlin"
        )
    }
}
