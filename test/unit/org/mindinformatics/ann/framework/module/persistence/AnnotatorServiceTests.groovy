package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import grails.test.mixin.*
import org.apache.commons.codec.binary.Base64
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AnnotatorService)
@Mock([Annotation,Tag])
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


    @Test
    void createAnnotation_shouldCreateAnnotation() {
        def json = """
            {
                "tags":["anaphora","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """
        def jsonObject = JSON.parse(json)
        println "jsonObject: " + jsonObject
        assert jsonObject != null
        //assert jsonObject.text == "<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>"
        assert jsonObject.uri == "https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/"
        def annotation = service.create(jsonObject)

        assert annotation != null
        assert annotation.id != null
        assert annotation.tags.size() == 2
    }

    @Test
    void updateAnnotation_shouldUpdateAnnotation() {
        def jsonOnCreate = """
            {
                "tags":["anaphora","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonOnUpdate = """
            {
                "id":1,
                "tags":["anaphora","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonOnCreate)
        def annotation = service.create(jsonObject)
        assert annotation != null
        assert annotation.id == 1

        jsonObject = JSON.parse(jsonOnUpdate)
        annotation = service.update(jsonObject)
        //def annotation = service.update(jsonObject)

        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 2
    }


    @Test
    void updateAnnotation_shouldRemoveTagOnUpdate() {
        def jsonOnCreate = """
            {
                "tags":["anaphora","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonOnUpdate = """
            {
                "id":1,
                "tags":["anaphora"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonOnCreate)
        def annotation = service.create(jsonObject)
        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 2

        jsonObject = JSON.parse(jsonOnUpdate)
        annotation = service.update(jsonObject)
        //def annotation = service.update(jsonObject)

        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 1
    }

    @Test
    void deleteAnnotation_shouldMarkAnnotationAsDeleted() {
        def jsonOnCreate = """
            {
                "tags":["anaphora","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonOnCreate)
        def annotation = service.create(jsonObject)
        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 2
        assert service.delete(1)
        annotation = Annotation.get(1)
        assert annotation != null
        assert annotation.deleted
    }

    @Test
    void destroyAnnotation_shouldDeleteAnnotation() {
        def jsonOnCreate = """
            {
                "tags":["anaphora","flagged-jcm62@columbia.edu"],
                "citation":"None",
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "totalComments":0,
                "ranges":[{"endOffset":78,"start":"/annotatable[1]/p[5]/font[1]","end":"/annotatable[1]/p[5]/font[1]","startOffset":0}],
                "parent":"0",
                "deleted":false,
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "archived":false,
                "created":"2014-02-04T07:17:31.0+0000",
                "updated":"2014-02-04T08:45:25.772+0000",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonOnCreate)
        def annotation = service.create(jsonObject)
        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 2
        assert service.destroy(1)
        assert Annotation.get(1) == null
        assert Tag.findByName("anaphora")
        assert Tag.findByName("flagged-jcm62@columbia.edu")
    }

    @Test
    void searchAnnotations_shouldFindAllAnnotationsByTag() {
        def annotationJson1 = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def annotationJson2 = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def annotationJson3 = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def jsonObject1 = JSON.parse(annotationJson1)
        def jsonObject2 = JSON.parse(annotationJson2)
        def jsonObject3 = JSON.parse(annotationJson3)

        def annotation1 = service.create(jsonObject1)
        def annotation2 = service.create(jsonObject2)
        def annotation3 = service.create(jsonObject3)


        def annotations = Annotation.list()
        assert annotations.size() == 3
        println annotations*.id
        annotations.each {
            println it.id + " " + it.tags*.name
        }

        def params = ["tag":"anaphora"]
        def results = service.search(params)
        assert results != null
        assert results.totalCount == 3
        assert results.annotations.size() == 3
        assert annotations.contains(annotation1)
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)


    }




}
