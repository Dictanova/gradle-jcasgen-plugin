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
import org.gradle.api.tasks.compile.AbstractCompile

/**
 * Call 'Jg' tool to generate UIMA java typesystem from XML descriptor(s).
 *
 * @author Damien Raude-Morvan
 */
class JCasGenTask extends AbstractCompile {

    protected void compile() {
        destinationDir.mkdir()
        logger.debug "JCasGen using files ${source.files}"

        def allDescriptors = source.files
        allDescriptors.each { File file ->
            logger.debug "${file}"
            logger.debug "${classpath.asPath}"
            // run JCasGen to generate the Java sources
            Jg jCasGen = new Jg();
            def String[] args = [
                    "-jcasgeninput",
                    file,
                    "-jcasgenoutput",
                    destinationDir,
                    "=jcasgenclasspath",
                    classpath.asPath
            ];
            jCasGen.main0(args, null, new JCasGenProgressMonitor(), new JCasGenErrors());
        }
    }

    class JCasGenProgressMonitor implements IProgressMonitor {

        public void done() {
        }

        public void beginTask(String name, int totalWorked) {
        }

        public void subTask(String message) {
            println message
        }

        public void worked(int work) {
        }
    }

    class JCasGenErrors implements IError {

        public void newError(int severity, String message, Exception exception) {
            String fullMessage = "JCasGen: " + message + exception;
            if (severity == IError.INFO) {
                println fullMessage
            } else if (severity == IError.WARN) {
                println fullMessage
            } else if (severity == IError.ERROR) {
                println fullMessage
            } else {
                throw new UnsupportedOperationException("Unknown severity level: " + severity);
            }
        }
    }

}
