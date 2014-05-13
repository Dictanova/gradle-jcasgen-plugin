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

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.file.FileResolver

/**
 * A handler for a virtual directory mapping, injecting a virtual directory named 'jcasgen'
 * into the project's various
 * {@link org.gradle.api.tasks.SourceSet source sets}.
 *
 * @author Damien Raude-Morvan
 */
class JCasGenSourceDirectory {

    /**
     * All JCasGen source for this source set
     */
    def SourceDirectorySet jcasgen

    JCasGenSourceDirectory(String parentDisplayName, FileResolver fileResolver) {
        jcasgen = new DefaultSourceDirectorySet("${parentDisplayName} JCasGen source", fileResolver)
        jcasgen.filter.include("**/*_TS.xml")
    }

    /**
     * Configures the JCasGen source for this set.
     * The given closure is used to configure the {@code SourceDirectorySet}
     * (see {@link #jcasgen}) which contains the JCasGen source.
     *
     * @param configuration The closure to use to configure the JCasGen source
     * @return this
     */
    JCasGenSourceDirectory jcasgen(Closure configuration) {
        configuration.delegate = jcasgen
        configuration.call()

        this
    }
}
