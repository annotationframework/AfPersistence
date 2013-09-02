package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject


/**
 * Implements the Annotator Storage API methods
 *
 * https://github.com/okfn/annotator/wiki/Storage
 * https://github.com/okfn/annotator-store/blob/master/annotator/store.py
 */
class AnnotatorController {

    def annotatorService

    static allowedMethods = [create:'POST',
            destroy:'DELETE',
            update: ['POST','PUT'],
            search: ['GET','POST'],
            read: ['GET','POST']
        ]

    /**
     *
     * @return
     */
    def index() {
        println "Index " + params
        def apiResponse = [
            "name": "Annotator Store API",
            "version": getGrailsApplication().metadata["app.version"]]
        render (apiResponse as JSON)
    }

    /**
     *
     * https://github.com/okfn/annotator/wiki/Authentication
     * @return
     */
    def token() {
        //println "Get token: " + params
        //def jsonObject = request.JSON
        //println "JSON " + jsonObject.toString()
        //response.status = 200
        render(status: 200, text: annotatorService.getToken())
        //render(status: 200, text: "your-token-has-been-created")
        //render(status: 503, text: 'Failed to create annotation' + annotation.errors)
    }

    def read() {
        println "Read " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()

        def annotation = Annotation.get(params.id)
        if (annotation) {
            render annotation.toJSONObject() as JSON
        }
        else {
            def message = "Unable to locate annotation with ID ${params.id}"
            //render([status: 503, text: message] as JSON)
            //response.status = 404
            render(status: 404, text: "Annotation not found!")
        }

    }

    def create() {
        println "Create " + params
        println "JSON = " + request.JSON
        def annotation = annotatorService.create(request.JSON)

        // Need to find a way to handle errors
        if (!annotation.hasErrors()) {
            println "Saved annotation " + annotation.toJSONObject()
            render annotation.toJSONObject() as JSON
            //redirect(action: "read", id: annotation.id)
        }
        else {
            println "Annotation has errors " + annotation.errors
            response.status = 400
            render "No JSON payload sent. Annotation not created."
            //render([status: 503, text: 'Unable to create annotation', errors: annotation.errors]as JSON)
        }


        /*
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
        }*/
    }


    def update() {
        println "Update " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()

        def annotation = annotatorService.update(jsonObject)
        if (!annotation) {
            render(status: 404, text: "Annotation not found! No update performed")
            return
        }

        println "Updated annotation: " + annotation
        render annotation.toJSONObject() as JSON
    }

    def destroy() {
        println "Destroy " + params
        def jsonObject = request.JSON
        println "JSON " + jsonObject.toString()

        // Delete the annotation
        def deleted = annotatorService.destroy(params.id)
        if (!deleted) {
            render(status: 404, text: "Annotation not found! No delete performed")
        }


        render(status: 204, text: "")
    }

    def list() {
        println "List " + params
        def results = Annotation.list().collect { it.toJSONObject() }
        render (results as JSON)
    }

    def annotations() {
        redirect(action: "list")
    }


    def search() {
        println "Search annotations " + params
        def jsonObject = request.JSON
        def results = annotatorService.search(params.uri)
        render ([total: results.size(), rows: results] as JSON)
    }
}
