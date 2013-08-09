package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON

class AnnotatorController {

    static allowedMethods = [create:'POST', destroy:'DELETE', update: 'PUT', search: 'GET', read: 'GET']

    def index() {}


    def token() {
        println "Get token: " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()

        render(status: 200, text: "your-token-has-been-created")
        //render(status: 503, text: 'Failed to create annotation' + annotation.errors)
    }

    def read() {
        println "read annotation " + params
        def jsonObject = request.JSON

    }

    def create() {
        println "create annotation " + params
        def jsonObject = request.JSON
        println "JSON = " + jsonObject

        def annotation = new Annotation(text: jsonObject.text, uri: jsonObject.uri, quote:  jsonObject.quote, json: jsonObject.toString())
        request.JSON.ranges.each {
            def range = new AnnotationRange(start: it.start, end: it.end, startOffset: it.startOffset, endOffset:  it.endOffset)
            annotation.addToRanges(range)
        }
        if (!annotation.hasErrors() && annotation.save()) {
            render annotation as JSON
        }
        else {
            render([status: 503, text: 'Unable to create annotation', errors: annotation.errors]as JSON)
        }
    }



    def update() {
        println "update annotation " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()


        def annotation = Annotation.get(params.id)
        annotation.text = jsonObject.text
        annotation.quote = jsonObject.quote
        annotation.json = jsonObject
        annotation.save()

        render annotation as JSON

    }

    def destroy() {
        println "destroy annotation " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()

        // Delete the annotation
        def annotation = Annotation.get(params.id)
        annotation.delete()

        // Return the list of remaining annotations for this page
        def annotations = Annotation.findAllByUri(jsonObject.uri)
        println "annotations: " + annotations.size()
        render ([total: annotations.size, rows: annotations] as JSON)

    }

    def search() {
        println "search annotations " + params
        def jsonObject = request.JSON
        println "JSON = " + jsonObject.toString()

        def annotations = Annotation.findAllByUri(params.uri)
        println "annotations: " + annotations.size()
        render ([total: annotations.size, rows: annotations] as JSON)
    }
}
