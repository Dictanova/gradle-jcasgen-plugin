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
import org.gradle.plugins.ide.eclipse.model.SourceFolder

/**
 * @author Damien Raude-Morvan
 */
class EclipseEnhancement extends GradlePluginEnhancement {

    def jcasgenConvention = project.convention.plugins.jcasgen

    EclipseEnhancement(Project project) {
        super(project)
    }

    void apply() {
        project.gradle.taskGraph.whenReady { taskGraph ->
            if (!project.plugins.hasPlugin('eclipse'))
                return

            project.eclipse.classpath {
                file {
                    whenMerged { classpath ->
                        classpath.entries.add(new SourceFolder('generated-src/jcasgen/main', null))
                    }
                }
            }
        }
    }

}
