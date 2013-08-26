package org.mindinformatics.ann.framework.module.persistence

import grails.test.mixin.*
import org.apache.commons.codec.binary.Base64
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AnnotatorService)
class AnnotatorServiceTests {

    //def annotatorService

    @Test
    void generateToken_shouldDoSomething() {
        def expectedToken = "eyJhbGciOiJIUzI1NiIsImN0eSI6InRleHRcL3BsYWluIn0.eyJqdGkiOiI3MDk2ZTcwZS04MmM0LTRlMDQtODMwOC05NTVkYTkxNzQzODEiLCJpYXQiOjEzNzc0NTU3NDh9.ybRphsHq27Jb5YchBfODTu9qZJaaBKx7XKOZTJsjKa8"
        println("LOCATION: " + Base64.class.getProtectionDomain().getCodeSource().getLocation());
        def annotatorService = new AnnotatorService()
        def actualToken = annotatorService.getToken()
        println actualToken

        assertEquals expectedToken, actualToken
    }



}
