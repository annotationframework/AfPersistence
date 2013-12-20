package org.mindinformatics.ann.framework.module.persistence

import grails.test.mixin.*
import org.apache.commons.codec.binary.Base64
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AnnotatorService)
class AnnotatorServiceTests {

    @Test
    void generateToken_shouldGenerateAndVerifyToken() {
        def calendar = Calendar.getInstance()
        calendar.set(2013, 1, 1)
        def issuedAt = calendar.getTime()
        def actualToken = service.getToken("jmiranda", "openannotation", 86400, issuedAt)
        println actualToken

        assertTrue service.verifyToken(actualToken)
    }


}
