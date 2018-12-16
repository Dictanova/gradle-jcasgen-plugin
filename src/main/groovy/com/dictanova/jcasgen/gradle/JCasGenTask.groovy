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

import org.apache.uima.tools.jcasgen.IError
import org.apache.uima.tools.jcasgen.IProgressMonitor
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

/**
 * Call 'Jg' tool to generate UIMA java typesystem from XML descriptor(s).
 *
 * @author Damien Raude-Morvan
 */
class JCasGenTask extends SourceTask {

    @Classpath
    FileCollection classpath

    @OutputDirectory
    File destinationDir

    @TaskAction
    void generate() {
        destinationDir.mkdir()
        logger.debug "JCasGen using files ${source.files}"
        logger.debug "Classpath ${classpath.asPath}"

        def classLoader = new URLClassLoader(classpath.collect { it.toURI().toURL() } as URL[])
        def jCasGen = classLoader.loadClass('org.apache.uima.tools.jcasgen.Jg').newInstance()
        def allDescriptors = source.files
        allDescriptors.each { File file ->
            logger.debug "${file}"
            def jgArgs = []
            jgArgs << "-jcasgeninput"
            jgArgs << file
            jgArgs << "-jcasgenoutput"
            jgArgs << destinationDir
            jgArgs << "-jcasgenclasspath"
            jgArgs << classpath.asPath
            logger.debug "Launch Jg with args ${jgArgs}"

            // Disable custom progress and error handling
            //jCasGen.main0(jgArgs as String[], null, new JCasGenProgressMonitor(), new JCasGenErrors())
            // run JCasGen to generate the Java sources
            jCasGen.main1(jgArgs as String[])
        }
    }

    class JCasGenProgressMonitor implements IProgressMonitor {

        void done() {
        }

        void beginTask(String name, int totalWorked) {
        }

        void subTask(String message) {
            JCasGenTask.this.logger.info message
        }

        void worked(int work) {
        }
    }

    class JCasGenErrors implements IError {

        void newError(int severity, String message, Exception exception) {
            String fullMessage = "JCasGen: " + message + exception
            if (severity == INFO) {
                JCasGenTask.this.logger.info fullMessage
            } else if (severity == WARN) {
                JCasGenTask.this.logger.warn fullMessage
            } else if (severity == ERROR) {
                JCasGenTask.this.logger.error fullMessage
            } else {
                throw new UnsupportedOperationException("Unknown severity level: " + severity)
            }
        }
    }

}
