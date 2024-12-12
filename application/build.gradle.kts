// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

plugins {
    id("org.springframework.boot")
}

dependencies {

    implementation(libs.bundles.spring)

    implementation(libs.kotlinReflect)
    implementation(libs.bundles.californium)
    implementation(libs.logging)

    implementation(libs.commonsCodec)
    implementation(libs.jacksonCbor)
    implementation(libs.jakartaXml)

    runtimeOnly(libs.micrometerPrometheusModule)
    runtimeOnly(libs.bundles.db)

    testImplementation(libs.springBootStarterTest)
    testImplementation(libs.mockitoKotlin)

    testRuntimeOnly(libs.junitPlatformLauncher)

    // Generate test and integration test reports
    jacocoAggregation(project(":application"))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    imageName.set("ghcr.io/osgp/sng-crest-device-simulator:${version}")
    if (project.hasProperty("publishImage")) {
        publish.set(true)
        docker {
            publishRegistry {
                username.set(System.getenv("GITHUB_ACTOR"))
                password.set(System.getenv("GITHUB_TOKEN"))
            }
        }
    }
}

testing {
    suites {
        val integrationTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(libs.springBootStarterTest)
                implementation(libs.springBootStarterDataJpa)
                implementation(libs.jacksonCbor)
                implementation.bundle(libs.bundles.californium)
                implementation(libs.awaitility)
                runtimeOnly(libs.h2)
            }
        }
    }
}
