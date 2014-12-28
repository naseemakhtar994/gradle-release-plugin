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

import org.gradle.api.NamedDomainObjectContainer

class ReleaseExtension {

    private static final DEFAULT_DEPENDS_ON = Collections.singletonList('build')
    private static final DEFAULT_PUSH = false
    private static final DEFAULT_SUFFIX = '-SNAPSHOT'

    final NamedDomainObjectContainer<? extends Publication> publications
    final PublicationFactory factory

    List<Object> dependsOn = DEFAULT_DEPENDS_ON
    boolean push = DEFAULT_PUSH
    String suffix = DEFAULT_SUFFIX

    ReleaseExtension(publications, factory) {
        this.publications = publications
        this.factory = factory
    }

    public void dependsOn(Object... paths) {
        this.dependsOn = Arrays.asList(paths)
    }

    def publications(Closure closure) {
        publications.configure(closure)
    }

    def getPublicationByName(String name) {
        return publications.getByName(name)
    }

}