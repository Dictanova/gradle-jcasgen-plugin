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
        project.plugins.apply(JavaPlugin)

        project.configurations.add('jcasgen') {
            visible = false
            transitive = false
            description = "The JCasGen libraries to be used for this project."
        }

        project.configurations.compile {
            extendsFrom project.configurations.jcasgen
        }

        project.convention.plugins.java.sourceSets.all { SourceSet sourceSet ->
            setupJCasGenFor(sourceSet, project)
        }
    }

    private setupJCasGenFor(SourceSet sourceSet, Project project) {
        insertJCasGenSourceDirectorySetInto(sourceSet, project)

        Task jcasgenTask = createJCasGenTaskFor(sourceSet, project)
        project.tasks[sourceSet.compileJavaTaskName].dependsOn(jcasgenTask)
    }

    private insertJCasGenSourceDirectorySetInto(SourceSet sourceSet, Project project) {
        def typesystemDir = "src/${sourceSet.name}/jcasgen"
        sourceSet.convention.plugins.jcasgen = new JCasGenSourceDirectory(sourceSet.name, project.fileResolver)
        sourceSet.java { srcDir generatedJavaDirFor(project, sourceSet) }
        sourceSet.jcasgen { srcDir typesystemDir }
        sourceSet.resources { srcDir typesystemDir }
    }

    private Task createJCasGenTaskFor(SourceSet sourceSet, Project project) {
        def jcasgenTask = project.tasks.add(taskName(sourceSet), JCasGenTask)

        jcasgenTask.group = GENERATE_GROUP
        jcasgenTask.description = "Generates code from the ${sourceSet.name} JCasGen typesystem."
        jcasgenTask.outputDirectory = generatedJavaDirFor(project, sourceSet)
        jcasgenTask.conventionMapping.defaultSource = { sourceSet.jcasgen }
        jcasgenTask.conventionMapping.jcasgenClasspath = {
            def jcasgenClassPath = project.configurations.jcasgen.copy()
            jcasgenClassPath.transitive = true
            jcasgenClassPath
        }

        jcasgenTask
    }

    private File generatedJavaDirFor(Project project, SourceSet sourceSet) {
        project.file("${project.buildDir}/generated-src/jcasgen/${sourceSet.name}")
    }

    private String taskName(SourceSet sourceSet) {
        return sourceSet.getTaskName('generate', 'SchemaSource')
    }
}