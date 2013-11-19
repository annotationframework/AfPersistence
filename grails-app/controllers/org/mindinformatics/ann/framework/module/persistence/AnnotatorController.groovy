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
            "version": getGrailsApplication().metadata["app.version"]
        ]

        render (apiResponse as JSON)
    }

    /**
     * Generates a token to be used to communicate with the annotator store.
     *
     * See https://github.com/okfn/annotator/wiki/Authentication
     *
     * @return
     */
    def token() {
        render(status: 200, text: annotatorService.getToken())
    }

    /**
     * GETs annotations. Server should return an array of annotations serialised as JSON or JSON-LD.
     *
     * @return
     */
    def read() {
        def jsonObject = request.JSON
        def annotation = Annotation.get(params.id)
        if (annotation) {
            render annotation.toJSONObject() as JSON
        }
        else {
            //def message = "Unable to locate annotation with ID ${params.id}"
            //render([status: 503, text: message] as JSON)
            //response.status = 404
            render(status: 404, text: "Annotation not found!")
        }

    }

    /**
     * POSTs an annotation (serialised as JSON or JSON-LD) to the server. The annotation is updated with any data
     * (such as a newly created id and probably creation/saving date) returned from the server.
     *
     * In annotator.js this is called when the annotator publishes the "annotationCreated" event.
     *
     * @return
     */
    def create() {
        def annotation = annotatorService.create(request.JSON)

        // Need to find a way to handle errors
        if (!annotation.hasErrors()) {
            render annotation.toJSONObject() as JSON
            //redirect(action: "read", id: annotation.id)
        }
        else {
            response.status = 400
            render "No JSON payload sent. Annotation not created."
            //render([status: 503, text: 'Unable to create annotation', errors: annotation.errors]as JSON)
        }
    }


    /**
     * PUTs an annotation (serialised as JSON) on the server under its id. The annotation is updated with any data
     * (such as a newly created id) returned from the server.
     *
     * Called when the annotator publishes the "annotationUpdated" event.
     *
     * @return
     */
    def update() {
        def jsonObject = request.JSON
        def annotation = annotatorService.update(jsonObject)
        if (!annotation) {
            render(status: 404, text: "Annotation not found! No update performed")
            return
        }
        render annotation.toJSONObject() as JSON
    }

    /**
     * Issues a DELETE request to server for the annotation.
     *
     * @return
     */
    def destroy() {
        def jsonObject = request.JSON
        // Delete the annotation
        def deleted = annotatorService.destroy(params.id)
        if (!deleted) {
            render(status: 404, text: "Annotation not found! No delete performed")
            return;
        }
        render(status: 204, text: "")
    }

    /**
     * Issues a PUT and the annotation is marked as deleted but a copy of it is kept in the storage.
     *
     * @return
     */
    def delete() {


    }


    /**
     *
     * @return
     */
    def list() {
        def results = Annotation.list().collect { it.toJSONObject() }
        render (results as JSON)
    }

    /**
     *
     * @return
     */
    def annotations() {
        redirect(action: "list")
    }

    /**
     * Marks data as archived
     *
     * @return
     */
    def archive() {

    }


    /**
     * GETs all annotations relevant to the query. Should return a JSON object with a rows property
     * containing an array of annotations.
     *
     * @param uri
     * @param media
     * @param text
     * @param user the username or user id
     * @param source the
     *
     * @return
     */
    def search() {
        def jsonObject = request.JSON
        def results = annotatorService.search(params.uri, params.media, params.text, params.user, params.source)
        render ([total: results.size(), rows: results] as JSON)
    }
}
