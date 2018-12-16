/*
* Copyright 2013-2014 Damien Raude-Morvan
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.dictanova.jcasgen.gradle

import com.dictanova.jcasgen.gradle.enhancements.EclipseEnhancement
import com.dictanova.jcasgen.gradle.enhancements.IDEAEnhancement
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet

/**
 * A plugin for adding JCasGen generation support to {@link JavaPlugin java projects}
 *
 * @author Damien Raude-Morvan
 */
class JCasGenPlugin implements Plugin<Project> {
    private static final String GENERATE_GROUP = 'generate'

    private static final String EXTENSION_NAME = 'jcasgen'

    private Project project

    void apply(Project project) {
        this.project = project
        project.apply plugin: 'java'
        JCasGenExtension ext = project.extensions.create(EXTENSION_NAME, JCasGenExtension, project)
        project.configurations.create("jcasgen") {
            visible = false
            description = "The JCasGen libraries to be used for this project."
            extendsFrom = []
        }

        project.sourceSets.all { SourceSet sourceSet ->
            setupJCasGenFor(sourceSet)
        }

        configureEnhancements()

        project.afterEvaluate { Project p ->
            p.dependencies {
                jcasgen "org.apache.uima:uimaj-tools:${p.extensions.getByType(JCasGenExtension).uimaVersion}"
            }
        }
    }

    private setupJCasGenFor(SourceSet sourceSet) {
        insertJCasGenSourceDirectorySetInto(sourceSet)

        // Wire task
        Task jcasgenTask = createJCasGenTaskFor(sourceSet)

        // Generate source code before java compile
        String compileJavaTaskName = sourceSet.getCompileTaskName("java")
        Task compileJavaTask = project.tasks.getByName(compileJavaTaskName)
        compileJavaTask.dependsOn(jcasgenTask)
    }

    private insertJCasGenSourceDirectorySetInto(SourceSet sourceSet) {
        def typesystemDir = "src/${sourceSet.name}/typesystem"

        sourceSet.convention.plugins.jcasgen = new JCasGenSourceDirectory(sourceSet.name, project.fileResolver)
        sourceSet.jcasgen { srcDir typesystemDir }
        sourceSet.resources { srcDir typesystemDir }

        // Add generated source dir to compilation source
        sourceSet.java { srcDir generatedJavaDirFor(sourceSet) }
    }

    private Task createJCasGenTaskFor(SourceSet sourceSet) {
        def taskName = taskName(sourceSet)
        def jcasgenTask = project.tasks.create(taskName, JCasGenTask)
        jcasgenTask.group = GENERATE_GROUP
        jcasgenTask.description = "Generates code from the ${sourceSet.name} JCasGen typesystem."
        jcasgenTask.classpath = project.files(sourceSet.resources.srcDirs) + project.configurations.jcasgen
        jcasgenTask.source = sourceSet.jcasgen
        jcasgenTask.destinationDir = generatedJavaDirFor(sourceSet)
        jcasgenTask
    }

    private File generatedJavaDirFor(SourceSet sourceSet) {
        def generatedSourceDir = 'generated-src'
        project.file("${project.buildDir}/${generatedSourceDir}/jcasgen/${sourceSet.name}")
    }

    private String taskName(SourceSet sourceSet) {
        return sourceSet.getTaskName('generate', 'TypeSystem')
    }

    /**
     * Configure enhancements so that other Gradle plugins can be "enhanced" by custom behavior.
     */
    private void configureEnhancements() {
        new EclipseEnhancement(project).apply()
        new IDEAEnhancement(project).apply()
    }
}
