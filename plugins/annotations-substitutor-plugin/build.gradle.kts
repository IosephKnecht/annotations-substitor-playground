plugins {
    `java-gradle-plugin`
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    compileOnly(libs.com.android.tools.build.api)
    implementation(gradleKotlinDsl())
    implementation(libs.org.javassist)
    implementation(libs.junit)
    implementation(project(":overridden-annotations"))
    implementation(project(":parameterized-list"))
}

gradlePlugin {
    plugins {
        create("annotations-substitutor-plugin") {
            id = "io.github.iosephkecnt.annotations.substitutor"
            implementationClass = "io.github.iosephkecnt.annotations.substitutor.AnnotationsSubstitutorPlugin"
        }
    }
}