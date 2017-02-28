package org.mindinformatics.ann.framework.module.persistence


import grails.test.mixin.*
import org.junit.Ignore
import org.junit.Test
import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(AnnotationPermission)
@Mock([Tag,Annotation,AnnotationUser])
class AnnotationPermissionTests {

    @Test
    void validate_shouldReturnFalseWhenNoFieldsArePopulated() {
        AnnotationPermission permission = new AnnotationPermission()
        assertFalse permission.validate()
    }

    @Test
    void validate_shouldReturnTrueWhenAllFieldsArePopulated() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotationUser1 = new AnnotationUser(userId: "user1")
        def annotationPermission1 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        assertTrue annotationPermission1.validate()
    }


    @Ignore
    void equalsAndHashCode_shouldReturnTrueIfAllFieldsAreEqual() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotationUser1 = new AnnotationUser(userId: "user1")
        def annotationPermission1 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        def annotationPermission2 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        assertTrue annotationPermission1.equals(annotationPermission2)
    }


    @Ignore
    void equalsAndHashCode_shouldReturnTrueIfAnyFieldsAreDifferent() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotation2 = new Annotation(text:"different text", quote:"different quote",uri:"http://differenturi.com",media:"different media",source:"different source")
        def annotationUser1 = new AnnotationUser(userId: "user1")
        def annotationPermission1 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        def annotationPermission2 = new AnnotationPermission(user: annotationUser1, annotation: annotation2, permission: Permission.READ)
        assertTrue annotationPermission1.equals(annotationPermission2)
    }

    @Test
    void equalsAndHashCode_shouldReturnTrueIfPermissionIsDifferent() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotation2 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotationUser1 = new AnnotationUser(userId: "user1")
        def annotationPermission1 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        def annotationPermission2 = new AnnotationPermission(user: annotationUser1, annotation: annotation2, permission: Permission.ADMIN)
        assertFalse annotationPermission1.equals(annotationPermission2)
    }





}
