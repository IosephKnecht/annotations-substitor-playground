package io.github.iosephkecnt.annotations.substitutor

import io.github.iosephknecht.overridden.annotations.ParameterizedTest
import io.github.iosephknecht.overridden.annotations.RegularTest
import javassist.ClassPool
import javassist.CtClass
import javassist.bytecode.AnnotationsAttribute
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

internal abstract class ProcessAndroidTestAnnotations : DefaultTask() {

    @get:InputFiles
    abstract val allJars: ListProperty<RegularFile>

    @get:InputFiles
    abstract val allDirectories: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @Internal
    val jarPaths = mutableSetOf<String>()

    @TaskAction
    fun execute() {
        val pool = ClassPool(ClassPool.getDefault())

        val jarOutput = JarOutputStream(
            BufferedOutputStream(
                FileOutputStream(
                    output.get().asFile
                )
            )
        )

//        allJars.get().forEach { file ->
//            println("handling " + file.asFile.absolutePath)
//            val jarFile = JarFile(file.asFile)
//            jarFile.entries().iterator().forEach { jarEntry ->
//                println("Adding from jar ${jarEntry.name}")
//                jarOutput.writeEntity(jarEntry.name, jarFile.getInputStream(jarEntry))
//            }
//            jarFile.close()
//        }

        allDirectories.get().forEach { directory ->
            directory.asFile.walk().forEach { file ->
                if (file.isFile && file.name.endsWith(".class")) {
                    val ctClass = file.inputStream().use {
                        pool.makeClass(it)
                    }

                    if (ctClass.hasAnnotation(RegularTest::class.java) || ctClass.hasAnnotation(
                            ParameterizedTest::class.java
                        )
                    ) {
                        val classFile = ctClass.classFile
                        val classFileConstPool = classFile.constPool

                        val annotationsAttribute = AnnotationsAttribute(
                            classFileConstPool,
                            AnnotationsAttribute.visibleTag
                        )

                        annotationsAttribute.removeAnnotation(RegularTest::class.java.canonicalName)
                        annotationsAttribute.removeAnnotation(ParameterizedTest::class.java.canonicalName)

                        classFile.addAttribute(annotationsAttribute)

                        val relativePath =
                            directory.asFile.toURI().relativize(file.toURI()).path

                        jarOutput.writeEntity(
                            relativePath.replace(File.separatorChar, '/'),
                            ctClass.toBytecode()
                        )
                    } else {
                        val relativePath =
                            directory.asFile.toURI().relativize(file.toURI()).path

                        jarOutput.writeEntity(
                            relativePath.replace(File.separatorChar, '/'),
                            file.inputStream()
                        )
                    }
                }
            }
        }

        jarOutput.close()
    }

    // writeEntity methods check if the file has name that already exists in output jar
    private fun JarOutputStream.writeEntity(name: String, inputStream: InputStream) {
        // check for duplication name first
        if (jarPaths.contains(name)) {
            printDuplicatedMessage(name)
        } else {
            putNextEntry(JarEntry(name))
            inputStream.copyTo(this)
            closeEntry()
            jarPaths.add(name)
        }
    }

    private fun JarOutputStream.writeEntity(relativePath: String, byteArray: ByteArray) {
        // check for duplication name first
        if (jarPaths.contains(path)) {
            printDuplicatedMessage(relativePath)
        } else {
            putNextEntry(JarEntry(relativePath))
            write(byteArray)
            closeEntry()
            jarPaths.add(relativePath)
        }
    }

    private fun printDuplicatedMessage(name: String) =
        println("Cannot add ${name}, because output Jar already has file with the same name.")
}