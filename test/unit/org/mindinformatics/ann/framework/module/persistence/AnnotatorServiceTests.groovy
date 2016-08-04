package org.mindinformatics.ann.framework.module.persistence

import grails.buildtestdata.mixin.Build
import grails.converters.JSON
import grails.test.mixin.*
import org.apache.commons.codec.binary.Base64
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.codehaus.groovy.grails.web.util.TypeConvertingMap
import org.junit.*
import org.mindinformatics.ann.framework.module.security.systems.SystemApi

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AnnotatorService)
@Mock([Annotation,AnnotationUser,AnnotationPermission,Tag,SystemApi])
@Build(SystemApi)
class AnnotatorServiceTests {

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
        assert annotation.permissions?.size() == 3
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
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":["jcm62@columbia.edu"]},
                "user":{"id":"justin.miranda@gmail.com","name":"justin.miranda"},
                "media":"text"
            }
        """

        def annotationJson2 = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":["jcm62@columbia.edu"]},
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
                "permissions":{"update":["justin.miranda@gmail.com"],"admin":["justin.miranda@gmail.com"],"delete":["justin.miranda@gmail.com"],"read":["justin.miranda@gmail.com"]},
                "user":{"id":"jcm62@columbia.edu","name":"jmiranda"},
                "media":"text"
            }
        """

        def annotationJson4 = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["lduarte1991@gmail.com"],"admin":["lduarte1991@gmail.com"],"delete":["lduarte1991@gmail.com"],"read":[]},
                "user":{"id":"lduarte1991@gmail.com","name":"lduarte"},
                "media":"text"
            }
        """


        def jsonObject1 = JSON.parse(annotationJson1)
        def jsonObject2 = JSON.parse(annotationJson2)
        def jsonObject3 = JSON.parse(annotationJson3)
        def jsonObject4 = JSON.parse(annotationJson4)

        def annotation1 = service.create(jsonObject1)
        def annotation2 = service.create(jsonObject2)
        def annotation3 = service.create(jsonObject3)
        def annotation4 = service.create(jsonObject4)


        def annotations = Annotation.list()
        assert annotations.size() == 4

        // Search by tag
        def params1 = new GrailsParameterMap(["tag":"anaphora"], null)
        def results = service.search(params1)
        assert results != null
        assert results.totalCount == 4
        assert results.annotations.size() == 4
        assert annotations.contains(annotation1)
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)

        // Search by username
        def params2 = new GrailsParameterMap(["username":"jmiranda"], null)
        results = service.search(params2)
        assert results != null
        assert results.totalCount == 2
        assert results.annotations.size() == 2
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)


        // Search by userid
        def params3 = new GrailsParameterMap(["userid":"jcm62@columbia.edu"], null)
        results = service.search(params3)
        assert results != null
        assert results.totalCount == 2
        assert results.annotations.size() == 2
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)

        // Search by multiple users
        def params4 = new GrailsParameterMap(["username":["jmiranda","lduarte"]], null)
        println "params4: " + params4
        results = service.search(params4)
        assert results != null
        assert results.totalCount == 3
        assert results.annotations.size() == 3
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)
        assert annotations.contains(annotation4)

        // FIXME String-based queries like [executeQuery] are currently not supported in this implementation of GORM. Use criteria instead.
//        def params5 = new GrailsParameterMap(["username":"jmiranda"], null)
//        results = service.searchSecure(params5, "jcm62@columbia.edu")
//        assert results != null
//        assert results.totalCount == 1
//        assert results.annotations.size() == 1
    }


    @Test
    void createAndUpdate_shouldNotSaveDuplicatePermissions() {
        def jsonString = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":["jcm62@columbia.edu"]},
                "user":{"id":"justin.miranda@gmail.com","name":"justin.miranda"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonString)
        def annotation = service.create(jsonObject)
        assert annotation != null
        assert annotation.id == 1
        assert annotation.tags.size() == 1
        assert annotation.permissions.size() == 4

    }




}
