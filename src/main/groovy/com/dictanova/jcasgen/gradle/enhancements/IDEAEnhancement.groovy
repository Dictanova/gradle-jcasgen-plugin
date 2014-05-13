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
package com.dictanova.jcasgen.gradle.enhancements

import org.gradle.api.Project

/**
 * @author Damien Raude-Morvan
 */
class IDEAEnhancement extends GradlePluginEnhancement {

    IDEAEnhancement(Project project) {
        super(project)
    }

    void apply() {
        project.gradle.taskGraph.whenReady { taskGraph ->
            if (!project.plugins.hasPlugin('idea'))
                return;

            project.idea.module {
                iml {
                    beforeMerged { module ->
                        module.excludeFolders.clear()
                    }
                    whenMerged { module ->
                        //TODO: better solution is to move the 'generated-src' out of the 'target' folder
                        // - then the exclude folders would not be necessary any more
                        // http://issues.gradle.org/browse/GRADLE-1174
                        // http://stackoverflow.com/questions/16702126/how-to-us-specify-intellij-exclude-directories-using-gradle
                        // https://github.com/szczepiq/hibernate-core/commit/bfd95b476c1fdb63d06c29c833e58bcd66515a8c
                        module.excludeFolders = [module.pathFactory.path(project.file(".gradle"))]
                        ["classes", "docs", "dependency-cache", "libs", "reports", "resources", "test-results", "tmp"].each {
                            module.excludeFolders += module.pathFactory.path(project.file("$project.buildDir/$it"))
                        }
                        sourceDirs += project.file("$project.buildDir/generated-src/jcasgen/main")
                    }
                }
            }
        }
    }
}
