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

/**
 * Generates UIMA java typesystem from XML descriptor.
 *
 * @author Damien Raude-Morvan
 */
class JCasGenTask extends ConventionTask {

    /**
     * The classpath containing the UIMA 'Jg' CLI command.
     * <p>
     * This is implemented dynamically from the task's convention mapping setup in <code>JCasGenPlugin</code>
     *
     * @see JCasGenPlugin
     */
    @InputFiles
    FileCollection jcasgenFiles

    /**
     * The directory to generate the parser source files into
     */
    @OutputDirectory
    File outputDirectory

    @TaskAction
    def generate() {
        // run JCasGen to generate the Java sources
        Jg jCasGen = new Jg();
        args = [
                "-jcasgeninput",
                this.jcasgenFiles.toString(),
                "-jcasgenoutput",
                this.outputDirectory.getAbsolutePath()
        ];
        jCasGen.main0(args, null, new JCasGenProgressMonitor(), new JCasGenErrors());
    }

    class JCasGenProgressMonitor implements IProgressMonitor {

        @Override
        public void done() {
        }

        @Override
        public void beginTask(String name, int totalWorked) {
        }

        @Override
        public void subTask(String message) {
            getLog().info("JCasGen: " + message);
        }

        @Override
        public void worked(int work) {
        }
    }

    class JCasGenErrors implements IError {

        public void newError(int severity, String message, Exception exception) {
            String fullMessage = "JCasGen: " + message;
            if (severity == IError.INFO) {
                getLog().info(fullMessage, exception);
            } else if (severity == IError.WARN) {
                getLog().warn(fullMessage, exception);
            } else if (severity == IError.ERROR) {
                throw new JCasGenException(exception.getMessage(), exception);
            } else {
                throw new UnsupportedOperationException("Unknown severity level: " + severity);
            }
        }
    }

    class JCasGenException extends RuntimeException {
        public JCasGenException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}