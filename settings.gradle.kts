// SPDX-FileCopyrightText: Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

rootProject.name = "sng-crest-device-simulator"

include("application")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("californium", "3.8.0")
            library("californium-core", "org.eclipse.californium", "californium-core").versionRef("californium")
            library("californium-scandium", "org.eclipse.californium", "scandium").versionRef("californium")
            bundle("californium", listOf("californium-core", "californium-scandium"))

            library("postgresql", "org.postgresql", "postgresql").withoutVersion()
            library("flyway", "org.flywaydb", "flyway-core").withoutVersion()
            bundle("data", listOf("postgresql", "flyway"))

            library("logging", "io.github.oshai", "kotlin-logging-jvm").version("6.0.9")

            library("commonsCodec", "commons-codec", "commons-codec").version("1.17.0")

            library(
                "mockitoKotlin",
                "org.mockito.kotlin",
                "mockito-kotlin"
            ).version("5.3.1")
        }
        create("integrationTestLibs") {
            library("h2", "com.h2database", "h2").withoutVersion()
        }
    }
}
