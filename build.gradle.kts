buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.android.tools.build:gradle:7.4.0")
        classpath ("com.vanniktech:gradle-maven-publish-plugin:0.14.2")
        classpath ("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.1" apply false
}