/*
* Copyright 2013 Damien Raude-Morvan
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
package com.drazzib.gradle.jcasgen.plugin

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

    void apply(Project project) {
        project.apply plugin: 'java'
        project.convention.plugins.jcasgen = new JCasGenConvention(project);
        project.sourceSets.all { SourceSet sourceSet ->
            setupJCasGenFor(sourceSet, project)
        }
    }

    private setupJCasGenFor(SourceSet sourceSet, Project project) {
        def configName = (sourceSet.getName().equals(SourceSet.MAIN_SOURCE_SET_NAME) ? "jcasgen" : sourceSet.getName() + "Jcasgen")
        project.configurations.add(configName) {
            visible = false
            transitive = false
            description = "The JCasGen libraries to be used for this project."
            extendsFrom = []
        }

        insertJCasGenSourceDirectorySetInto(sourceSet, project)

        // Wire task
        Task jcasgenTask = createJCasGenTaskFor(sourceSet, project)

        // Generate source code before java compile
        String compileJavaTaskName = sourceSet.getCompileTaskName("java");
        Task compileJavaTask = project.tasks.getByName(compileJavaTaskName);
        compileJavaTask.dependsOn(jcasgenTask)
    }

    private insertJCasGenSourceDirectorySetInto(SourceSet sourceSet, Project project) {
        def typesystemDir = "src/${sourceSet.name}/typesystem"

        sourceSet.convention.plugins.jcasgen = new JCasGenSourceDirectory(sourceSet.name, project.fileResolver)
        sourceSet.jcasgen { srcDir typesystemDir }
        sourceSet.resources { srcDir typesystemDir }

        // Add generated source dir to compilation source
        sourceSet.java { srcDir generatedJavaDirFor(project, sourceSet) }
    }

    private Task createJCasGenTaskFor(SourceSet sourceSet, Project project) {
        def taskName = taskName(sourceSet)
        def jcasgenTask = project.tasks.add(taskName, JCasGenTask)
        jcasgenTask.group = GENERATE_GROUP
        jcasgenTask.description = "Generates code from the ${sourceSet.name} JCasGen typesystem."
        jcasgenTask.conventionMapping.map("classpath") {
            return sourceSet.compileClasspath + project.files(sourceSet.resources.srcDirs)
        }
        /*jcasgenTask.conventionMapping.map("typeSystemDir") {
            return project.convention.plugins.jcasgen.typeSystemDir
        }*/
        jcasgenTask.conventionMapping.map('source') {
            return sourceSet.jcasgen
        }
        jcasgenTask.conventionMapping.map('destinationDir') {
            return generatedJavaDirFor(project, sourceSet)
        }

        jcasgenTask
    }

    private File generatedJavaDirFor(Project project, SourceSet sourceSet) {
        def generatedSourceDir = 'generated-src'
        project.file("${project.buildDir}/${generatedSourceDir}/jcasgen/${sourceSet.name}")
    }

    private String taskName(SourceSet sourceSet) {
        return sourceSet.getTaskName('generate', 'TypeSystem')
    }
}
