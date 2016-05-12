package org.mindinformatics.ann.framework.module.persistence


import grails.test.mixin.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AnnotationPermission)
class AnnotationPermissionTests {

    void testSomething() {
        AnnotationPermission permission = new AnnotationPermission()
        assertTrue !permission.validate()
    }
}
