package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import grails.test.mixin.Mock
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

import org.junit.*

//@TestFor(AnnotatorService)
//@Mock([Annotation,Tag])
class AnnotatorServiceIntegrationTests extends GroovyTestCase {

    def annotatorService

    @Before
    void setUp() {
        // Setup logic here
//        def annotation = new Annotation()
//        annotation.json = '{"tags":["parallelism"],"citation":"None","text":"<p>example of parallelism with line 1</p>","created":"2014-02-11T20:55:09.518Z","updated":"2014-02-11T20:55:09.518Z","quote":"I have somewhere surely lived a life of joy with you,","ranges":[{"endOffset":210,"start":"/annotatable[1]/p[4]/font[1]","end":"/annotatable[1]/p[4]/font[1]","startOffset":157}],"permissions":{"update":["ande018@live.com"],"admin":["ande018@live.com"],"delete":["ande018@live.com"],"read":[]},"parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/","media":"text","user":{"id":"ande018@live.com","name":"ande018"}}'
//        annotation.userid = "ande018@live.com"
//        annotation.username = "ande018"
//        annotation.quote = "I have somewhere surely lived a life of joy with you,"
//        annotation.media = "text"
//        annotation.text = "<p>example of parallelism with line 1</p>"
//        annotation.uri = "https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/"
//        annotation.save(flush:true, failOnError:true)

    }

    @After
    void tearDown() {
        // Tear down logic here
    }

    @Test
    void searchSecure_shouldFilterByPermissions() {
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

        int totalCountBeforeInsert = Annotation.count()

        def annotation1 = annotatorService.create(jsonObject1)
        def annotation2 = annotatorService.create(jsonObject2)
        def annotation3 = annotatorService.create(jsonObject3)
        def annotation4 = annotatorService.create(jsonObject4)

        // Basic check
        def annotations = Annotation.list()
        assert annotations.size() == totalCountBeforeInsert + 4

        // Search by tag
        def params1 = new GrailsParameterMap(["tag":"anaphora"], null)
        def results = annotatorService.search(params1)
        assert results != null
        assert results.totalCount == 4
        assert results.annotations.size() == 4
        assert annotations.contains(annotation1)
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)

        // Search by username
        def params2 = new GrailsParameterMap(["username":"jmiranda"], null)
        results = annotatorService.search(params2)
        assert results != null
        assert results.totalCount == 2
        assert results.annotations.size() == 2
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)


        // Search by userid
        def params3 = new GrailsParameterMap(["userid":"jcm62@columbia.edu"], null)
        results = annotatorService.search(params3)
        assert results != null
        assert results.totalCount == 2
        assert results.annotations.size() == 2
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)

        // Search by multiple users
        def params4 = new GrailsParameterMap(["username":["jmiranda","lduarte"]], null)
        println "params4: " + params4
        results = annotatorService.search(params4)
        assert results != null
        assert results.totalCount == 3
        assert results.annotations.size() == 3
        assert annotations.contains(annotation2)
        assert annotations.contains(annotation3)
        assert annotations.contains(annotation4)

        // Secure search
        // FIXME String-based queries like [executeQuery] are currently not supported in this implementation of GORM. Use criteria instead.
        def params5 = new GrailsParameterMap([:], null)
        results = annotatorService.searchSecure(params5, "jcm62@columbia.edu")
        assert results != null
        assert results.totalCount == 3
        assert results.annotations.size() == 3

    }


    @Test
    void create_shouldAddDefaultReadPermission() {
        def jsonString = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jcm62"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonString)
        def annotation = annotatorService.create(jsonObject)

        println "read: " + annotation.permissions.find { it.permission == Permission.READ }

        assertNotNull annotation.permissions.find { it.permission == Permission.READ && it.user.userId == "group:__world__" }
    }

    @Test
    void createAndUpdate_shouldChangeDefaultReadPermissionToPrivate() {
        def jsonString = """
            {
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":[]},
                "user":{"id":"jcm62@columbia.edu","name":"jcm62"},
                "media":"text"
            }
        """

        def jsonObject = JSON.parse(jsonString)
        def annotation = annotatorService.create(jsonObject)

        jsonString = """
            {
                "id":"${annotation.id}",
                "tags":["anaphora"],
                "text":"<p>Tha anaphoora here grabs our attention early in the poem calls on the listener to pay attention<\\/p>\\n<p><strong><em>{NB this annotation appears at the befinning of Blood AXE as well assection 42 of Song of myself. I cant delete it from one without it being deleted from both}<\\/em><\\/strong><\\/p>\\n<p>&nbsp;<\\/p>",
                "uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/fe939c73594e454da10d734884e54db2/016f704dd546408998ab2c2545878d89/",
                "quote":"The place where the great city stands is not the place of stretch’d wharves, d",
                "permissions":{"update":["jcm62@columbia.edu"],"admin":["jcm62@columbia.edu"],"delete":["jcm62@columbia.edu"],"read":["jcm62@columbia.edu"]},
                "user":{"id":"jcm62@columbia.edu","name":"jcm62"},
                "media":"text"
            }
        """

        println "Updating annotation: " + jsonString

        jsonObject = JSON.parse(jsonString)
        annotation = annotatorService.update(jsonObject)

        assertNull annotation.permissions.find { it.permission == Permission.READ && it.user.userId == "group:__world__" }
        assertNotNull annotation.permissions.find { it.permission == Permission.READ && it.user.userId == "jcm62@columbia.edu" }



    }



}
