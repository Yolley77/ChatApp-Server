val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.0"
}

group = "com.chatapp"
version = "0.0.1"
application {
    mainClass.set("com.chatapp.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

// ./gradlew build
// scp -r .\build\libs\com.chatapp.ktor-sample-0.0.1.jar pi@192.168.2.135:~/Documents/ChatAppServer/
// java -jar com.chatapp.ktor-sample-0.0.1.jar
// BUILD EXECUTABLE JAR
tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes("Main-Class" to "com.chatapp.ApplicationKt")
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory()) it else zipTree(it) })
}