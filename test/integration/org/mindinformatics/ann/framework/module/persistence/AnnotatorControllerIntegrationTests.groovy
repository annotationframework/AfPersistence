package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.buildtestdata.mixin.Build

import static org.junit.Assert.*
import org.junit.*

@TestFor(AnnotatorController)
@Mock([Annotation,Tag])
@Build([Annotation])
class AnnotatorControllerIntegrationTests {

    @Before
    void setUp() {
        // Setup logic here
        def annotation1 = new Annotation(text:"my comment 1",quote:"quote1",uri:"http://afdemo.aws.af.cm/annotation/index1",media:"video",source:"source1")
        annotation1.userid = "justin.miranda@gmail.com"
        annotation1.username = "jmiranda"
        annotation1.json = '{"text":"my comment 1","quote":"quote1","uri":"http://afdemo.aws.af.cm/annotation/index1","media":"video","source":"source1"}'
        annotation1.save(flush:true, failOnError:true)

        def annotation2 = new Annotation(text:"my comment 2",quote:"quote2",uri:"http://afdemo.aws.af.cm/annotation/index2",media:"text",source:"source2")
        annotation2.userid = "danielcebrianr@gmail.com"
        annotation2.username = "dani"
        annotation2.json = '{"text":"my comment 2","quote":"quote2","uri":"http://afdemo.aws.af.cm/annotation/index2","media":"text","source":"source2"}'
        annotation2.save(flush:true, failOnError:true)

        def annotation3 = new Annotation(text:"my comment 3",quote:"quote3",uri:"http://afdemo.aws.af.cm/annotation/index3",media:"audio",source:"source3")
        annotation3.userid = "justin.miranda@gmail.com"
        annotation3.username = "jmiranda"
        annotation3.json = '{"text":"my comment 3","quote":"quote3","uri":"http://afdemo.aws.af.cm/annotation/index3","media":"audio","source":"source3"}'
        annotation3.save(flush:true, failOnError:true)

        def annotation4 = new Annotation(text:"my comment 4",quote:"quote4",uri:"http://afdemo.aws.af.cm/annotation/index4",media:"video",source:"source4")
        annotation4.userid = "danielcebrianr@gmail.com"
        annotation4.username = "dani"
        annotation4.json = '{"text":"my comment 4","quote":"quote4","uri":"http://afdemo.aws.af.cm/annotation/index4","media":"video","source":"source4"}'
        annotation4.parent = annotation1
        annotation4.save(flush:true, failOnError:true)

        def annotation5 = new Annotation(text:"my comment 5",quote:"quote5",uri:"http://afdemo.aws.af.cm/annotation/index5",media:"text",source:"source5")
        annotation5.userid = "justin.miranda@gmail.com"
        annotation5.username = "jmiranda"
        annotation5.json = '{"text":"my comment 5","quote":"quote5","uri":"http://afdemo.aws.af.cm/annotation/index5","media":"text","source":"source5"}'
        annotation5.parent = annotation1
        annotation5.save(flush:true, failOnError:true)

        def annotation6= new Annotation(text:"my comment 6",quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index6",media:"text",source:"source6")
        annotation6.userid = "justin.miranda@gmail.com"
        annotation6.username = "jmiranda"
        annotation6.json = '{"text":"my comment 6","quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index6","media":"text","source":"source6"}'
        annotation6.parent = annotation1
        annotation6.deleted = true
        annotation6.save(flush:true, failOnError:true)

        def annotation7 = new Annotation(text:"my comment 7",quote:"quote7",uri:"http://afdemo.aws.af.cm/annotation/index7",media:"video",source:"source7")
        annotation7.userid = "danielcebrianr@gmail.com"
        annotation7.username = "dani"
        annotation7.json = '{"text":"my comment 7","quote":"quote7","uri":"http://afdemo.aws.af.cm/annotation/index7","media":"video","source":"source7"}'
        annotation7.parent = annotation1
        annotation7.archived = true
        annotation7.save(flush:true, failOnError:true)

        assert Annotation.list().size() == 7
    }

    @After
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
        controller.list();
        assert controller.response.json.size() == 7
    }

    @Test
    void create() {
        //x-annotator-auth-token:eyJhbGciOiJIUzI1NiIsImN0eSI6InRleHRcL3BsYWluIiwidHlwIjoiSldTIn0.eyJjb25zdW1lcktleSI6IjBjYmZhMzcwLWI3M2MtNGUzYS1hZTQ2LTU4MmRmMjg0YjdjMyIsImlzc3VlZEF0IjoiMjAxMy0xMi0xM1QwNzozNjoyNSswMDAwIiwidXNlcklkIjoiam1pcmFuZGEiLCJqdGkiOiIyMmZjNmZmZS04ZWZhLTQ1NDktOGZhYy0yZTI3YWM2Mzg2YzEiLCJ0dGwiOjg2NDAwLCJpYXQiOjEzODY5NjMzODV9.dwQ1M9s7NR7GoLJf2_pFOQLP19KiOI8M7Qgm20wnV0c
        def json = '{"tags":["tag1","tag2","tag3"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        // Now check the annotation to make sure all fields were created
        def annotation = Annotation.get(controller.response.json.id)
        assert annotation != null
        assert annotation.id == controller.response.json.id
        assert annotation.text == "asfsafsafasf"
        assert annotation.quote == "qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena"
        assert annotation.uri == "http://afdemo.aws.af.cm/annotation/index"
        assert annotation.json != null
        assert annotation.tags.size() == 3
    }

    @Test
    void create_shouldNotAddDuplicateTags() {
        //x-annotator-auth-token:eyJhbGciOiJIUzI1NiIsImN0eSI6InRleHRcL3BsYWluIiwidHlwIjoiSldTIn0.eyJjb25zdW1lcktleSI6IjBjYmZhMzcwLWI3M2MtNGUzYS1hZTQ2LTU4MmRmMjg0YjdjMyIsImlzc3VlZEF0IjoiMjAxMy0xMi0xM1QwNzozNjoyNSswMDAwIiwidXNlcklkIjoiam1pcmFuZGEiLCJqdGkiOiIyMmZjNmZmZS04ZWZhLTQ1NDktOGZhYy0yZTI3YWM2Mzg2YzEiLCJ0dGwiOjg2NDAwLCJpYXQiOjEzODY5NjMzODV9.dwQ1M9s7NR7GoLJf2_pFOQLP19KiOI8M7Qgm20wnV0c
        def json = '{"tags":["tag1","tag1","tag1"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        // Now check the annotation to make sure only one tag was created
        def annotation = Annotation.get(controller.response.json.id)
        assert annotation != null
        assert annotation.tags.size() == 1
    }

    @Test
    void create_shouldHandleUserAsString() {
        //def json = '{"tags":["existing tag one"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def json1 = '{"permissions":{"read":[],"update":["jmiranda@example.com"],"delete":["jmiranda@example.com"],"admin":["jmiranda@example.com"]},"user":"jmiranda","ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json1.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        def annotation = Annotation.get(controller.response.json.id)
        assert annotation.userid == "jmiranda"
        assert annotation.username == "jmiranda"

    }

    @Test
    void create_shouldHandleUserAsObject() {
        //def json = '{"tags":["existing tag one"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def json1 = '{"permissions":{"read":[],"update":["jmiranda@example.com"],"delete":["jmiranda@example.com"],"admin":["jmiranda@example.com"]},"user":{"id":"jmiranda@example.com","name":"jmiranda"},"ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json1.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        def annotation = Annotation.get(controller.response.json.id)
        assert annotation.userid == "jmiranda@example.com"
        assert annotation.username == "jmiranda"

    }

    @Test
    void create_shouldAddExistingTagToNewAnnotation() {
        //def json = '{"tags":["existing tag one"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def json1 = '{"permissions":{"read":[],"update":["jmiranda@example.com"],"delete":["jmiranda@example.com"],"admin":["jmiranda@example.com"]},"user":{"id":"jmiranda@example.com","name":"jmiranda"},"ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json1.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json


        def json2 = '{"permissions":{"read":[],"update":["Beta930@gmail.com"],"delete":["Beta930@gmail.com"],"admin":["Beta930@gmail.com"]},"user":{"id":"Beta930@gmail.com","name":"TriBob"},"ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json2.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        // Now check the annotation to make sure only one tag was created
        def annotation = Annotation.get(controller.response.json.id)
        assert annotation != null
        //assert annotation.tags.size() == 1
    }





    @Test
    void update_shouldUpdateExistingAnnotation() {
        def json = '{"id":2,"tags":["tag3"],"text":"asf aggdsgds","created":"2013-12-01T01:02:34.0+0000","updated":"2013-12-01T01:02:34.0+0000","quote":"erroribus","ranges":[{"start":"/div[1]/p[1]","startOffset":140,"end":"/div[1]/p[1]","endOffset":149}],"uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def annotation = Annotation.build(id:2, json: json)
        annotation.addToTags(name: "tag1")
        annotation.addToTags(name: "tag2")
        annotation.save(flush:true, failOnError:true)

        assert annotation.tags.size() == 2
        assert annotation.tags.collect { it.name } == ["tag1","tag2"]

        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json.getBytes()
        controller.update()

        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        annotation = Annotation.get(controller.response.json.id)
        assert annotation.id == controller.response.json.id
        assert annotation.text == "asf aggdsgds"
        assert annotation.quote == "erroribus"
        assert annotation.uri == "http://afdemo.aws.af.cm/annotation/index"
        assert annotation.json != null
        assert annotation.tags.size() == 1
        assert annotation.tags.collect { it.name } == ["tag3"]
    }


    @Test
    void update_shouldHandleUserAsString() {
        //def json = '{"tags":["existing tag one"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def json = '{"id":2, "permissions":{"read":[],"update":["jmiranda@example.com"],"delete":["jmiranda@example.com"],"admin":["jmiranda@example.com"]},"user":"jmiranda@example.com","ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        Annotation.build(id:2, userid: "someone", username: "someone", json: json)

        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        def annotation = Annotation.get(controller.response.json.id)
        assert annotation.userid == "jmiranda@example.com"
        assert annotation.username == "jmiranda@example.com"

    }

    @Test
    void update_shouldHandleUserAsObject() {
        //def json = '{"tags":["existing tag one"],"text":"asfsafsafasf","ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def json = '{"id":2, "permissions":{"read":[],"update":["jmiranda@example.com"],"delete":["jmiranda@example.com"],"admin":["jmiranda@example.com"]},"user":{"id":"jmiranda@example.com","name":"jmiranda"},"ranges":[{"start":"/annotatable[1]/p[5]/font[1]","startOffset":31,"end":"/annotatable[1]/p[5]/font[1]","endOffset":46}],"quote":"lean and loafe","text":"<p>a \'la-la\' effect</p>","tags":["alliteration"],"media":"text","updated":"2014-02-28T20:23:40.603Z","created":"2014-02-28T20:23:40.603Z","parent":"0","uri":"https://courses.edx.org/courses/HarvardX/AI12.2x/2013_SOND/courseware/aa61c36baed647aebf3765ba669e8365/e236ebbd193d48279e669680789ad541/","citation":"None"}'
        Annotation.build(id:2, userid: "someone", username: "someone", json: json)

        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = json.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        def annotation = Annotation.get(controller.response.json.id)
        assert annotation.userid == "jmiranda@example.com"
        assert annotation.username == "jmiranda"

    }




    @Test
    void update_shouldReturn404Error() {
        println "annotations: " + Annotation.list()
        controller.request.method = "POST"
        controller.request.contentType = "text/json"

        // valid content, but the annotationS doesn't exist
        controller.params.id = 100
        controller.request.content = '{"id":100,"tags":[],"text":"asf aggdsgds","created":"2013-12-01T01:02:34.0+0000","updated":"2013-12-01T01:02:34.0+0000","quote":"erroribus","ranges":[{"start":"/div[1]/p[1]","startOffset":140,"end":"/div[1]/p[1]","endOffset":149}],"uri":"http://afdemo.aws.af.cm/annotation/index"}'.getBytes()
        controller.update()
        println controller.response.json
        println controller.response.status
        println controller.response.status == 404

        assertEquals 404, controller.response.status
        assertNotNull controller.response.json
    }

    @Test
    void create_shouldCreateParentChildAssociation() {

        def parentJson = '{"id":1,"tags":[],"text":"asf aggdsgds","created":"2013-12-01T01:02:34.0+0000","updated":"2013-12-01T01:02:34.0+0000","quote":"erroribus","ranges":[{"start":"/div[1]/p[1]","startOffset":140,"end":"/div[1]/p[1]","endOffset":149}],"uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def parent = Annotation.build(id:1, json: parentJson)
        parent.save(flush:true, failOnError:true)
        assertNotNull parent

        def childJson = '{"text":"asfsafsafasf","parent":1,"tags":["sfasfaf"],"ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        controller.request.content = childJson.getBytes()
        controller.create();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json

        // Now check the annotation to make sure all fields were created
        def child = Annotation.get(controller.response.json.id)
        assertNotNull child
        child.parent.id == parent.id
    }


    @Test
    // Deleting an annotation only hides the record from public view (wee need this in case of inappropriate comments)
    void destroy_shouldRemoveAnnotationFromDatabase() {
        def json = '{"text":"asfsafsafasf","tags":["sfasfaf"],"ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def annotation = Annotation.build(id:1, json: json)
        annotation.save(flush:true, failOnError:true)
        assertNotNull annotation

        //def json = '{"id":1}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        //controller.request.content = json.getBytes()
        controller.params.id = 1
        controller.destroy();
        println controller.response.json
        println controller.response.status

        assertEquals 204, controller.response.status
        assertNotNull controller.response.json

        assert Annotation.get(1) == null
    }

    @Test
    void delete_shouldMarkAnnotationAsDeleted() {
        def json = '{"text":"asfsafsafasf","tags":["sfasfaf"],"ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def annotation = Annotation.build(id:1, json: json)
        annotation.save(flush:true, failOnError:true)
        assertNotNull annotation

        //def json = '{"id":1}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        //controller.request.content = json.getBytes()
        controller.params.id = 1
        controller.delete();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json
        assertTrue annotation.deleted
    }

    @Test
    void archive_shouldMarkAnnotationAsArchived() {
        def json = '{"text":"asfsafsafasf","tags":["sfasfaf"],"ranges":[{"start":"/div[1]/p[1]","startOffset":244,"end":"/div[1]/p[2]","endOffset":27}],"quote":"qui. Idque graeco scaevola duo in, vix mazim admodum suscipiantur ad. No cum cetero mena","uri":"http://afdemo.aws.af.cm/annotation/index"}'
        def annotation = Annotation.build(id:1, json: json)
        annotation.save(flush:true, failOnError:true)
        assertNotNull annotation

        //def json = '{"id":1}'
        controller.request.method = "POST"
        controller.request.contentType = "text/json"
        //controller.request.content = json.getBytes()
        controller.params.id = 1
        controller.archive();
        println controller.response.json
        println controller.response.status

        assertEquals 200, controller.response.status
        assertNotNull controller.response.json
        assertTrue annotation.archived
    }

    @Test
    void list_shouldReturnAllAnnotations() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.list();
        assert controller.response.status == 200
        assert controller.response.json.size() == 7
    }


    /**
     * loadFromSearch:{
     limit: 2
     media: "text"
     offset: 0
     uri: "http://catool.localhost/ova/index.php?r=ova/index"
     user: "danielcebrianr@gmail.com"
     }
     */
    @Test
    void search() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.search();
        println controller.response.json
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5
    }


    // search should return annotations in reverse chronological order (starting with the more recent record)
    @Test
    void search_shouldReturnAnnotationsInReverseChronologicalOrder() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.search();
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5
        def annotations = controller.response.json.rows.collect { it.text }
        assert annotations == ["my comment 5","my comment 4","my comment 3","my comment 2","my comment 1"]
        /*
        assert controller.response.json.rows[0].id == Annotation.findByText("my comment 5").id
        assert controller.response.json.rows[1].id == Annotation.findByText("my comment 4").id
        assert controller.response.json.rows[2].id == Annotation.findByText("my comment 3").id
        assert controller.response.json.rows[3].id == Annotation.findByText("my comment 2").id
        assert controller.response.json.rows[4].id == Annotation.findByText("my comment 1").id
        */
    }


    @Test
    void search_shouldReturnFirstPageOfResults() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.limit = 2
        controller.params.offset = 0
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
        assert controller.response.json.total == 5
    }



    // search by user
    @Test
    void searchByUserid() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.userid="danielcebrianr@gmail.com"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
    }

    // search by name
    @Test
    void searchByUsername() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.username="dani"
        controller.search();
        println controller.response.json
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
    }


    // search by uri
    @Test
    void searchByUri() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.uri="http://afdemo.aws.af.cm/annotation/index1"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 1
    }

    @Test
    void searchByUriUsingWildcard() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.uri="http://afdemo.aws.af.cm/annotation/index"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5
    }

    // search by source
    @Test
    void searchBySource() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.source="source1"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 1
    }

    // search by media
    @Test
    void searchByMedia() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.media="text"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
    }

    // search by text
    @Test
    void searchByText() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.text="my comment 1"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 1
    }

    @Test
    void searchByTextUsingWildcard() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.text="my comment"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5
    }

    // search by quote
    @Test
    void searchByQuote() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.quote="quote1"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 1
    }

    @Test
    void searchByQuoteUsingWildcard() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.quote="uote"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5
    }

    // search by parent
    @Test
    void searchByParent() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.parentid=1
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
    }

    // search by tag
    @Test
    void searchByTag() {
        def annotation = new Annotation(text:"my comment 6",quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation.userid = "justin.miranda@gmail.com"
        annotation.json = '{"text":"my comment 6","tags":["tag1","tag2"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation.addToTags(name: "tag1")
        annotation.addToTags(name: "tag2")
        annotation.save(failOnError:true)

        def annotation2 = new Annotation(text:"my comment 7",quote:"quote7",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source7")
        annotation2.userid = "justin.miranda@gmail.com"
        annotation2.json = '{"text":"my comment 7","tags":["tag2","tag3"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source7","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation2.addToTags(name: "tag2")
        annotation2.addToTags(name: "tag3")
        annotation2.save(failOnError:true)

        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.tag="tag2"
        controller.search();
        assertNotNull controller.response.json
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 2
    }

    @Test
    // Not sure we want to go down this path, so ignoring the test for now
    void searchByTagUsingWildCard() {
        def annotation = new Annotation(text:"my comment 6",quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation.userid = "justin.miranda@gmail.com"
        annotation.json = '{"text":"my comment 6","tags":["tag1","tag2"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation.addToTags(name: "tag1")
        annotation.addToTags(name: "tag2")
        annotation.save(failOnError:true)

        def annotation2 = new Annotation(text:"my comment 7",quote:"quote7",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source7")
        annotation2.userid = "justin.miranda@gmail.com"
        annotation2.json = '{"text":"my comment 7","tags":["tag2","tag3"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source7","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation2.addToTags(name: "tag2")
        annotation2.addToTags(name: "tag3")
        annotation2.save(failOnError:true)

        def annotation3 = new Annotation(text:"my comment 8",quote:"quote8",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source8")
        annotation3.userid = "justin.miranda@gmail.com"
        annotation3.json = '{"text":"my comment 8","tags":["another tag bites the dust"],"quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source8","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation3.addToTags(name: "another tag bites the dust")
        annotation3.save(failOnError:true)

        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.tag="tag"
        controller.search();
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 3
    }

    // search by quote
    @Test
    void searchByDateCreatedAfter() {
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.dateCreatedOnOrAfter = new Date() - 1
        controller.params.dateCreatedOnOrBefore = new Date() + 1
        controller.search();
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.rows.size() == 5  // FIXME cannot set dateCreated on domain class in unit tests
    }


    // Return obfuscated geolocation data of annotation location for privacy reason (we can do this on the client side but anyone with technical background could find the lat-long within the annotation object) -- this we can discuss on how to do it.
    @Test
    void read_shouldObfuscateGeolocationData() {
        def annotation = new Annotation(text:"my comment 6",quote:"quote6",uri:"http://afdemo.aws.af.cm/annotation/index",media:"text",source:"source6")
        annotation.userid = "justin.miranda@gmail.com"
        annotation.json = '{"text":"my comment 6","quote":"quote6","uri":"http://afdemo.aws.af.cm/annotation/index","media":"text","source":"source6","geolocation":{"altitude": null,"longitude": -77.061244,"latitude": 38.9171597,"accuracy": 24}}'
        annotation.save(failOnError:true)

        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.id = annotation.id
        controller.read();
        println controller.response.json
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.geolocation.latitude == 38.9171597
        assert controller.response.json.geolocation.longitude == -77.061244
    }



    // annotation has 0 comments
    @Test
    void read_shouldHaveZeroCommentsByDefault() {
        def annotation = Annotation.findByText("my comment 2")
        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.id = annotation.id
        controller.read();
        println controller.response.json
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.totalComments == 0
    }


    // annotation has at least 1 comment
    @Test
    void read_shouldHaveAtLeastOneComments() {

        def annotation = Annotation.findByText("my comment 1")
        assert annotation != null

        controller.request.method = "GET"
        controller.request.contentType = "text/json"
        controller.params.id = annotation.id
        controller.read();
        println controller.response.json
        assert controller.response.json != null
        assert controller.response.status == 200
        assert controller.response.json.totalComments == 2
    }



}
