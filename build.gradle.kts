import org.gradle.kotlin.dsl.repositories

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.kotlin.serialization)
        classpath(libs.androidx.navigation.safe.args.gradle.plugin)
        classpath(libs.androidx.navigation.safe.args.generator)
        classpath(libs.google.services)
        classpath(libs.secrets.gradle.plugin)
    }
}