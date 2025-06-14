pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.20"
    }
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/apache-snapshots") }
        mavenCentral()
    }
}
rootProject.name = "jimmer-save"
