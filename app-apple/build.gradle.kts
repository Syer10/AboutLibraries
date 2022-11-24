import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

repositories {
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    val uikitConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
                // TODO: the current compose binary surprises LLVM, so disable checks for now.
                freeCompilerArgs = freeCompilerArgs + "-Xdisable-phases=VerifyBitcode"
            }
        }
    }
    iosX64("uikitX64", uikitConfiguration)
    iosArm64("uikitArm64", uikitConfiguration)
    iosSimulatorArm64("uikitSimulatorArm64", uikitConfiguration)

    val macosConfiguration: KotlinNativeTarget.() -> Unit = {
        binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal"
                )
            }
        }
    }
    macosX64(configure = macosConfiguration)
    macosArm64(configure = macosConfiguration)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(compose.ui)
                // implementation(compose("org.jetbrains.compose.components:components-resources"))

                implementation(project(":aboutlibraries-core"))
                implementation(project(":aboutlibraries-compose"))

                // Coroutines
                implementation(libs.kotlin.coroutines.core)
            }
        }
        val commonTest by getting
        val uikitMain by creating {
            dependsOn(commonMain)
        }
        val uikitTest by creating {
            dependsOn(commonTest)
        }

        listOf(
            "uikitX64",
            "uikitArm64",
            "uikitSimulatorArm64",
        ).forEach {
            getByName(it + "Main").dependsOn(uikitMain)
            getByName(it + "Test").dependsOn(uikitTest)
        }

        val macosMain by creating {
            dependsOn(commonMain)
        }
        val macosTest by creating {
            dependsOn(commonTest)
        }

        listOf(
            "macosX64",
            "macosArm64",
        ).forEach {
            getByName(it + "Main").dependsOn(macosMain)
            getByName(it + "Test").dependsOn(macosTest)
        }
    }
}

compose {
    kotlinCompilerPlugin.set(libs.versions.composeCompiler.get())
}

compose.experimental {
    uikit.application {
        bundleIdPrefix = "com.mikepenz.aboutlibraries.app.ios"
        projectName = "AboutLibraries"
        // ./gradlew :app:ios:iosDeployIPhone13Debug
        deployConfigurations {
            simulator("IPhone13") {
                device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPHONE_13
            }
        }
    }
}


compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"), kotlin.targets.getByName("macosArm64"))
    distributions {
        targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg)
        packageName = "AboutLibraries"
        packageVersion = "1.0.0"
    }
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs = freeCompilerArgs + "-Xdisable-phases=VerifyBitcode"
        }
    }
}

aboutLibraries {
    registerAndroidTasks = false
    prettyPrint = true
}
