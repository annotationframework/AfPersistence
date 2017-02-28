package org.mindinformatics.ann.framework.module.persistence


import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AnnotationUser)
class AnnotationUserTests {

    @Test
    void validate_shouldReturnTrueWhenAllFieldsAreEmpty() {
        AnnotationUser user = new AnnotationUser()
        assertTrue user.validate()
    }
}
