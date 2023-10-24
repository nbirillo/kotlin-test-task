import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "kotlin.test.task"
version = "0.0.1-SNAPSHOT"

fun properties(key: String) = project.findProperty(key).toString()

@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    java
    val kotlinVersion = "1.9.0"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion apply false

    id("org.springframework.boot") version "2.7.3" apply false
    id("io.spring.dependency-management") version "1.0.13.RELEASE" apply false
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion apply false

    id("io.gitlab.arturbosch.detekt") version "1.23.1"
}

val detektReportMerge by tasks.registering(io.gitlab.arturbosch.detekt.report.ReportMergeTask::class) {
    output.set(rootProject.buildDir.resolve("reports/detekt/merge.sarif"))
}

allprojects {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.jetbrains.team/maven/p/kotlin-test-framework/kotlin-test-framework")
        }
    }
}

tasks {
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
}

val server = "Server"

fun String.getGameName(suffix: String) = substring(0 until indexOf(suffix))

configure(subprojects.filter { server in it.name }) {
    apply<io.gitlab.arturbosch.detekt.DetektPlugin>()

    apply {
        plugin("java")
        plugin("kotlin")

        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jetbrains.kotlin.plugin.spring")
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config = rootProject.files("detekt.yml")
        buildUponDefaultConfig = true
        debug = true
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        exclude("**/GameFunctionsService.kt", "**/GameService.kt")
        finalizedBy(detektReportMerge)
        reports.sarif.required.set(true)
        detektReportMerge.get().input.from(sarifReportFile)
    }

    tasks.getByPath("detekt").onlyIf { project.hasProperty("runDetekt") }


    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")

        implementation("org.jetbrains.academy.test.system:core:2.0.7")

        val junitJupiterVersion = "5.9.0"
        implementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
        testRuntimeOnly("org.junit.platform:junit-platform-console:1.9.0")
    }

    val jvmVersion = "11"

    tasks {
        withType<KotlinCompile> {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = jvmVersion
            }
        }

        withType<JavaCompile> {
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }
    }
}
