/**
 * Copyright 2014 Rahel Lüthy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.netzwerg.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.LoggerFactory

import java.util.regex.Matcher

class ReleaseTask extends DefaultTask {

    private static final LOGGER = LoggerFactory.getLogger(ReleaseTask.class)

    static final RELEASE_TASK_DESC = 'Creates a tagged non-SNAPSHOT release'

    ReleaseTask() {
        description = RELEASE_TASK_DESC
    }

    @TaskAction
    def release() {
        commitVersionFile("Release v$project.version")
        String tag = createReleaseTag()
        String nextVersion = getNextVersion(project.version as String)
        project.ext.versionFile.text = nextVersion
        commitVersionFile("Prepare next release v$nextVersion")
        ReleaseExtension releaseExtension = project.getExtensions().getByType(ReleaseExtension.class)
        if (releaseExtension.push) {
            pushChanges(tag)
        }
    }

    def commitVersionFile(String msg) {
        LOGGER.debug("Committing version file: $msg")
        git 'commit', '-m', msg, project.ext.versionFile.name
    }

    def createReleaseTag() {
        String tagName = "v$project.version"
        LOGGER.debug("Creating release tag: $tagName")
        git 'tag', '-a', tagName, "-m Release $tagName"
        return tagName
    }

    def static getNextVersion(String currentVersion) {
        String pattern = /(\d+)([^\d]*$)/
        Matcher matcher = currentVersion =~ pattern
        String nextVersion = matcher.replaceAll("${(matcher[0][1] as int) + 1}${matcher[0][2]}") + "-SNAPSHOT"
        return nextVersion
    }

    def pushChanges(String tag) {
        LOGGER.debug('Pushing changes to repository')
        git 'push', 'origin', tag
        git 'push', 'origin', 'master'
    }

    def git(Object[] arguments) {
        LOGGER.debug("git $arguments")
        def output = new ByteArrayOutputStream()
        project.exec {
            executable 'git'
            args arguments
            standardOutput output
            ignoreExitValue = true
        }
        String gitOutput = output.toString().trim()
        if (!gitOutput.isEmpty()) {
            LOGGER.debug(gitOutput)
        }
    }

}
