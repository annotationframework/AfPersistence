package org.mindinformatics.ann.framework.module.persistence



import grails.test.mixin.*
import org.junit.Test
import org.mindinformatics.ann.framework.module.persistence.Annotation

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Annotation)
class AnnotationTests {

    @Test
    void toJSONObject_shouldFailWhenJsonIsEmpty() {
        def annotation = new Annotation(id: "1", uri: "http://example.com/1")
        shouldFail {
            annotation.toJSONObject()
        }
    }
}
