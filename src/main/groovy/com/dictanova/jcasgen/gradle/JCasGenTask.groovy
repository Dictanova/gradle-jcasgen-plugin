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
import org.apache.uima.tools.jcasgen.Jg
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction

/**
 * Call 'Jg' tool to generate UIMA java typesystem from XML descriptor(s).
 *
 * @author Damien Raude-Morvan
 */
class JCasGenTask extends SourceTask {

    FileCollection classpath;

    @OutputDirectory
    File destinationDir

    @TaskAction
    void generate() {
        destinationDir.mkdir()
        logger.debug "JCasGen using files ${source.files}"

        Jg jCasGen = new Jg();
        def allDescriptors = source.files
        allDescriptors.each { File file ->
            logger.debug "${file}"
            logger.debug "${classpath.asPath}"
            def jgArgs = []
            jgArgs << "-jcasgeninput"
            jgArgs << file
            jgArgs << "-jcasgenoutput"
            jgArgs << destinationDir
            jgArgs << "-jcasgenclasspath"
            jgArgs << classpath.asPath
            logger.debug "Launch Jg with args ${jgArgs}"

            // run JCasGen to generate the Java sources
            jCasGen.main0(jgArgs as String[], null, new JCasGenProgressMonitor(), new JCasGenErrors());
        }
    }

    class JCasGenProgressMonitor implements IProgressMonitor {

        public void done() {
        }

        public void beginTask(String name, int totalWorked) {
        }

        public void subTask(String message) {
            JCasGenTask.this.logger.info message
        }

        public void worked(int work) {
        }
    }

    class JCasGenErrors implements IError {

        public void newError(int severity, String message, Exception exception) {
            String fullMessage = "JCasGen: " + message + exception;
            if (severity == IError.INFO) {
                JCasGenTask.this.logger.info fullMessage
            } else if (severity == IError.WARN) {
                JCasGenTask.this.logger.warn fullMessage
            } else if (severity == IError.ERROR) {
                JCasGenTask.this.logger.error fullMessage
            } else {
                throw new UnsupportedOperationException("Unknown severity level: " + severity);
            }
        }
    }

}
