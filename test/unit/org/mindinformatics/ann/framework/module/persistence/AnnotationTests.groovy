package org.mindinformatics.ann.framework.module.persistence

import grails.test.mixin.*
import org.junit.Test
import org.mindinformatics.ann.framework.module.persistence.Annotation

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Annotation)
@Mock([Tag,Annotation])
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

}