/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.5.1/userguide/building_java_projects.html
 */

plugins {
    // Apply the java-library plugin for API and implementation separation.
    id("java-library")
    id("maven-publish")
    id("signing")
    id("checkstyle")
    id("jacoco")
    id("com.github.spotbugs") version "6.0.18"
    id("org.checkerframework") version "0.6.41"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

group = "com.rokoder.concurrency"
version = "1.0.0-snapshot"
val artifactName = "context-preserved-slf4j"

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

// More info https://docs.gradle.org/current/dsl/org.gradle.api.tasks.javadoc.Javadoc.html
tasks.withType<Javadoc>().configureEach {
    doFirst {
        println(destinationDir.toString() + "/index.html")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {

            groupId = group.toString()
            artifactId = artifactName
            version = version

            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set(artifactName)
                description.set("It provides thread local MDC context for Slf4j.")
                url.set("https://github.com/paramvir-b/context-preserved-slf4j")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://github.com/paramvir-b/context-preserved/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("paramvir-b")
                        name.set("Paramvir Bali")
                        email.set("paramvir@rokoder.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/paramvir-b/context-preserved-slf4j.git")
                    developerConnection.set("scm:git:https://github.com/paramvir-b/context-preserved-slf4j.git")
                    url.set("https://github.com/paramvir-b/context-preserved-slf4j")
                }
            }

            repositories {
                maven {
                    val releasesRepoUrl = uri(layout.buildDirectory.dir("repos/releases"))
                    val snapshotsRepoUrl = uri(layout.buildDirectory.dir("repos/snapshots"))
                    url = uri(if (version.toString().endsWith("snapshot")) snapshotsRepoUrl else releasesRepoUrl)
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}


dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.slf4j:slf4j-api:1.7+")

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.rokoder.concurrency:context-preserved:1.0.+")
    implementation("com.github.spotbugs:spotbugs-annotations:4.7.3")

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.hamcrest:hamcrest:2.2")

    val checkerVersion = "3.44.0"
    compileOnly("org.checkerframework:checker-qual:$checkerVersion")
    testCompileOnly("org.checkerframework:checker-qual:$checkerVersion")
    checkerFramework("org.checkerframework:checker:$checkerVersion")
}

checkstyle {
    toolVersion = "10.4"
    isIgnoreFailures = false // Added this so that the tasks fail if CheckStyle errors are present.
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(false)
        html.required.set(true)
    }
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            isEnabled = true
            limit {
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
    }

    doFirst {
        println(reports.html.outputLocation.get().toString() + "/index.html")
    }
}

tasks.test {
    finalizedBy(
        tasks.jacocoTestReport,
        tasks.jacocoTestCoverageVerification
    ) // report is always generated after tests run
}

tasks.spotbugsTest {
    enabled = false
}

tasks.spotbugsMain {
    reports.create("html") {
        required.set(true)
    }
}

tasks.spotbugsTest {
    reports.create("html") {
        required.set(true)
    }
}

checkerFramework {
    checkers = listOf(
        "org.checkerframework.checker.nullness.NullnessChecker",
        "org.checkerframework.common.value.ValueChecker"
    )
    excludeTests = true
    skipCheckerFramework = false
}

tasks.named<Test>("test") {

    // Run tests in parallel
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2).takeIf { it > 0 } ?: 1

    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}