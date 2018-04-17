import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.31"

    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlin_version))
    }
}

group = "com.gamasoft"
version = "1.0-SNAPSHOT"

plugins {
    java
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8", kotlin_version))

    compile("org.jetbrains.kotlinx","kotlinx-coroutines-core", "0.22.2")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


//buildscript {
//    ext.kotlin_version = '1.2.31'
//    ext.arrow_version = '0.6.1'
//
//    repositories {
//        mavenCentral()
//    }
//    dependencies {
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//    }
//}
//
//group 'com.gamasoft.arrow-test'
//version '1.0-SNAPSHOT'
//
//apply plugin: 'java'
//apply plugin: 'kotlin'
//apply plugin: 'kotlin-kapt'
//
//sourceCompatibility = 1.8
//
//repositories {
//    mavenCentral()
//    jcenter()
//}
//
//dependencies {
//    compile "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
//    compile 'org.jetbrains.kotlinx:kotlinx-coroutines-core:0.22.2'
//
//    compile "io.arrow-kt:arrow-core:$arrow_version"
//    compile "io.arrow-kt:arrow-typeclasses:$arrow_version"
//    compile "io.arrow-kt:arrow-instances:$arrow_version"
//    compile "io.arrow-kt:arrow-data:$arrow_version"
//    compile "io.arrow-kt:arrow-syntax:$arrow_version"
//    kapt    "io.arrow-kt:arrow-annotations-processor:$arrow_version"
//
//    compile "io.arrow-kt:arrow-free:$arrow_version" //optional
//    compile "io.arrow-kt:arrow-mtl:$arrow_version" //optional
//    compile "io.arrow-kt:arrow-effects:$arrow_version" //optional
//    compile "io.arrow-kt:arrow-effects-rx2:$arrow_version" //optional
//    compile "io.arrow-kt:arrow-effects-kotlinx-coroutines:$arrow_version" //optional
//    compile "io.arrow-kt:arrow-optics:$arrow_version" //optional
//
//
//    testCompile 'com.willowtreeapps.assertk:assertk:0.9'
//    testCompile group: 'junit', name: 'junit', version: '4.12'
//}
//
//compileKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}
//compileTestKotlin {
//    kotlinOptions.jvmTarget = "1.8"
//}