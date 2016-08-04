package org.mindinformatics.ann.framework.module.persistence

import grails.test.mixin.*
import org.junit.Test
import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission
import org.mindinformatics.ann.framework.module.persistence.Annotation

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Annotation)
@Mock([Tag,Annotation, AnnotationUser, AnnotationPermission])
class AnnotationTests {

    @Test
    void toJSONObject_shouldFailWhenJsonIsEmpty() {
        def annotation = new Annotation(id: "1", uri: "http://example.com/1")
        shouldFail {
            annotation.toJSONObject()
        }
    }


    @Test
    void addTag() {
        def annotation = new Annotation(text:"my comment 6", quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation.userid = "justin.miranda@gmail.com"
        annotation.json = '{"text":"my comment 6","tags":["tag1","tag2"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation.addToTags(name: "tag1")
        annotation.addToTags(name: "tag2")
        annotation.addToTags(name: "tag3")
        annotation.save(failOnError:true)
        assert annotation.tags.size() == 3
        assert Tag.list().size() == 3
    }

    @Test
    void addTag_shouldNotAddSameTagMoreThanOnce() {
        def annotation = new Annotation(text:"my comment 6", quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation.userid = "justin.miranda@gmail.com"
        annotation.json = '{"text":"my comment 6","tags":["tag1","tag2"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation.addToTags(name: "tag1")
        annotation.addToTags(name: "tag2")
        annotation.addToTags(name: "tag3")
        annotation.addToTags(name: "tag3")
        annotation.save(failOnError:true)
        assert annotation.tags.size() == 3
        assert Tag.list().size() == 3
    }


    @Test
    void addTag_shouldAllowMultipleAnnotationsPerTag() {
        def annotation1 = new Annotation(text:"my comment 6", quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation1.userid = "justin.miranda@gmail.com"
        annotation1.json = '{"text":"my comment 6","tags":["tag1","tag2","tag3"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation1.addToTags(name: "tag1")
        annotation1.addToTags(name: "tag2")
        annotation1.addToTags(name: "tag3")
        annotation1.save(failOnError:true)

        def annotation2 = new Annotation(text:"my comment 7", quote:"quote7",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source7")
        annotation2.userid = "justin.miranda@gmail.com"
        annotation2.json = '{"text":"my comment 7","tags":["tag1","tag2"],"quote":"quote7","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source7","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation2.addToTags(name: "tag1")
        annotation2.addToTags(name: "tag2")
        annotation2.save(failOnError:true)

        annotation1.tags.size() == 3
        annotation2.tags.size() == 2

        def tag1 = Tag.findByName("tag1")
        tag1.annotations.size() == 2
        tag1.annotations.containsAll([annotation1,annotation2])

    }


    @Test
    void removeTag_shouldNotAllDeleteOnTagAssociatedWithMultipleAnnotations() {
        def annotation1 = new Annotation(text:"my comment 6", quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation1.userid = "justin.miranda@gmail.com"
        annotation1.json = '{"text":"my comment 6","tags":["tag1","tag2","tag3"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation1.addToTags(name: "tag1")
        annotation1.addToTags(name: "tag2")
        annotation1.addToTags(name: "tag3")
        annotation1.save(failOnError:true)

        def annotation2 = new Annotation(text:"my comment 7", quote:"quote7",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source7")
        annotation2.userid = "justin.miranda@gmail.com"
        annotation2.json = '{"text":"my comment 7","tags":["tag1","tag2"],"quote":"quote7","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source7","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation2.addToTags(name: "tag1")
        annotation2.addToTags(name: "tag2")
        annotation2.save(failOnError:true)

        annotation1.tags.size() == 3
        annotation2.tags.size() == 2

        def tag1 = Tag.findByName("tag1")
        tag1.delete()

        annotation1.tags.size() == 2
        annotation2.tags.size() == 1
    }


    @Test
    void toString_shouldNotThrowOutOfMemoryError() {
        def json1 = '''{"tags":["tag1"],"citation":"Peter Bol and Yu Wen","text":"<p>&nbsp;</p>\n<p class=\"p1\">The <em>Spring and Autumn Annals</em> is the official chronicle of the State of Lu from 722 BCE to 481 BCE and was said to have been edited by Confucius. The original text of this chronicle is very concise,and some early commentators tried to decipher Confucius&rsquo;s judgment from his choice of words. However, The <em>Zuo Commentary</em>, composed around the early 4th century, took a different approach and gave lengthy accounts of the events which the <em>Annals</em> referred to.</p>","totalComments":2,"ranges":[{"endOffset":24,"start":"/textannotation[1]/p[20]/i[1]","end":"/textannotation[1]/p[20]/i[2]","startOffset":0}],"parent":"0","deleted":false,"uri":"https://courses.edx.org/courses/HarvardX/SW12.6x/2T2014/courseware/f9ec9c0c7bb8498d814684358b6e8b0f/ddccd8c979604535992e240d673467cd/6","id":34597,"archived":false,"created":"2014-06-13T02:12:17.0+0000","updated":"2014-06-13T10:06:45.0+0000","quote":"The Zuo Commentary on Spring and Autumn Annals","permissions":{"update":["chinaxharvard@gmail.com"],"admin":["chinaxharvard@gmail.com"],"delete":["chinaxharvard@gmail.com"],"read":[]},"user":{"id":"chinaxharvard@gmail.com","name":"ChinaX_Staff"},"media":"text"}'''

        //def json2 = '{"tags":["longs"],"citation":"Wu, Jingzi, 1701-1754. The Scholars. [Translated by Yang Hsien-yi and Gladys Yang. Author\'s port. and illus. by Cheng Shih-fa] Peking, Foreign Languages Press, 1957. Pages 49-51","text":"","created":"2014-06-29T07:25:07.498Z","updated":"2014-06-29T07:25:07.498Z","quote":"Xia","ranges":[{"endOffset":346,"start":"/textannotation[1]/p[10]","end":"/textannotation[1]/p[10]","startOffset":343}],"permissions":{"update":["micazorla@yahoo.es"],"admin":["micazorla@yahoo.es"],"delete":["micazorla@yahoo.es"],"read":[]},"parent":"0","uri":"https://courses.edx.org/courses/HarvardX/SW12.6x/2T2014/courseware/f9ec9c0c7bb8498d814684358b6e8b0f/0134825cf0f34fd68516ff4018d3ead4/1","media":"text","user":{"id":"micazorla@yahoo.es","name":"Mila1969"}}'

        def annotation1 = new Annotation(text:"my comment 6", quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation1.userid = "justin.miranda@gmail.com"
        annotation1.json = json1
        annotation1.addToTags(name: "tag1")
        annotation1.save(failOnError:true)
        assertNotNull annotation1.toString()

    }

    @Test
    void equalsAndHashCode_shouldReturnTrueIfAllFieldsAreEqual() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotation2 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        assertTrue annotation1.equals(annotation2)
    }

    @Test
    void equalsAndHashCode_shouldReturnTrueIfIdsDoNotMatch() {
        def annotation1 = new Annotation(id: "1", text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotation2 = new Annotation(id: "2", text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        assertTrue annotation1.equals(annotation2)
    }

    @Test
    void equalsAndHashCode_shouldReturnFalseWhenDifferent() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotation2 = new Annotation(text:"different text", quote:"different quote",uri:"http://notthesameuri.com",media:"different media",source:"different source")
        assertFalse annotation1.equals(annotation2)
    }

    @Test
    void addPermission_shouldNotAddDuplicateAnnotationPermissions() {
        def annotation1 = new Annotation(text:"text", quote:"quote",uri:"http://sampleuri.com",media:"media",source:"source")
        def annotationUser1 = new AnnotationUser(userId: "user1")
        def annotationPermission1 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)
        def annotationPermission2 = new AnnotationPermission(user: annotationUser1, annotation: annotation1, permission: Permission.READ)

        annotation1.addToPermissions(annotationPermission1)
        annotation1.addToPermissions(annotationPermission2)

        assertEquals 1, annotation1.permissions.size()
    }



}