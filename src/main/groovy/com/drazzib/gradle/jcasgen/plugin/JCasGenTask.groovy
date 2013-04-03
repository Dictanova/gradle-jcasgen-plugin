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

import org.apache.uima.tools.jcasgen.IError
import org.apache.uima.tools.jcasgen.IProgressMonitor
import org.apache.uima.tools.jcasgen.Jg
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.AbstractCompile

/**
 * Call 'Jg' tool to generate UIMA java typesystem from XML descriptor(s).
 *
 * @author Damien Raude-Morvan
 */
class JCasGenTask extends AbstractCompile {

    protected void compile() {
        getDestinationDir().mkdir()
        logger.debug "JCasGen using files ${getSource().getFiles()}"

        def allDescriptors = getSource().getFiles()
        allDescriptors.each {File file ->
            println file.name
            // run JCasGen to generate the Java sources
            Jg jCasGen = new Jg();
            def String[] args = [
                    "-jcasgeninput",
                    file,
                    "-jcasgenoutput",
                    getDestinationDir()
            ];
            jCasGen.main0(args, null, new JCasGenProgressMonitor(), new JCasGenErrors());
        }
    }

    class JCasGenProgressMonitor implements IProgressMonitor {

        @Override
        public void done() {
        }

        public void beginTask(String name, int totalWorked) {
        }

        public void subTask(String message) {
            print message
        }

        public void worked(int work) {
        }
    }

    class JCasGenErrors implements IError {

        public void newError(int severity, String message, Exception exception) {
            String fullMessage = "JCasGen: " + message;
            if (severity == IError.INFO) {
                print fullMessage
            } else if (severity == IError.WARN) {
                print fullMessage
            } else if (severity == IError.ERROR) {
                print fullMessage
            } else {
                throw new UnsupportedOperationException("Unknown severity level: " + severity);
            }
        }
    }

}