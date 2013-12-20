package org.mindinformatics.ann.framework.module.persistence

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(AnnotatorController)
@Mock([Annotation])
class AnnotatorControllerTests {

    void setUp() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    @Test
    void index() {
        controller.index()
        assert controller.response.json.name == "Annotator Store API"
    }
    @Test
    void list() {
        //controller.params.test = "something"
        controller.list();
        println controller.response.json
        println controller.response.status
        //assertFalse controller.response.json.success;
    }


}
