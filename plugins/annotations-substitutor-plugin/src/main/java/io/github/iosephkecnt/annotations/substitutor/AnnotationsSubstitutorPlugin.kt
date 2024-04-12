package io.github.iosephkecnt.annotations.substitutor

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.AndroidTest
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register

internal class AnnotationsSubstitutorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.findPlugin(AppPlugin::class.java) ?: error("unsupported plugin")

        val androidComponents =
            target.extensions.getByType(AndroidComponentsExtension::class.java)

        androidComponents.onVariants { variant ->
            val transformProvider =
                target.tasks.register<ProcessAndroidTestAnnotations>("processAndroidTestAnnotations${variant.name.capitalized()}")

            val transformProvider2 =
                target.tasks.register<ProcessAndroidTestAnnotations2>("processAndroidTestAnnotations2${variant.name.capitalized()}")

            val androidTestComponent = variant
                .nestedComponents
                .filterIsInstance<AndroidTest>()
                .firstOrNull()
                ?: return@onVariants

//            androidTestComponent
//                .artifacts
//                .forScope(ScopedArtifacts.Scope.PROJECT)
//                .use(transformProvider)
//                .toTransform(
//                    type = ScopedArtifact.CLASSES,
//                    inputJars = ProcessAndroidTestAnnotations::allJars,
//                    inputDirectories = ProcessAndroidTestAnnotations::allDirectories,
//                    into = ProcessAndroidTestAnnotations::output
//                )

            androidTestComponent
                .artifacts
                .forScope(ScopedArtifacts.Scope.PROJECT)
                .use(transformProvider2)
                .toTransform(
                    type = ScopedArtifact.CLASSES,
                    inputJars = ProcessAndroidTestAnnotations2::allJars,
                    inputDirectories = ProcessAndroidTestAnnotations2::allDirectories,
                    into = ProcessAndroidTestAnnotations2::output
                )
        }
    }
}