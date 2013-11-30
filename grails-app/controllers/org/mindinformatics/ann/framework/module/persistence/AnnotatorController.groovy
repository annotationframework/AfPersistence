package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.security.access.annotation.Secured


/**
 * Implements the Annotator Storage API methods based on Annotator's requirements.
 *
 * https://github.com/okfn/annotator/wiki/Storage
 * https://github.com/okfn/annotator-store/blob/master/annotator/store.py
 *
 * NOTE:  Access control is set to anonymous because we have our own token
 * validation mechanism used to validate incoming requests.  For more
 * information, see AuthTokenFilters.groovy.
 */
@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class AnnotatorController {

    def annotatorService

    static allowedMethods = [
            create:'POST',
            destroy:'DELETE',
            update: ['POST','PUT'],
            archive: ['POST', 'PUT'],
            delete: ['POST', 'PUT', 'DELETE'],
            search: ['GET','POST'],
            read: ['GET','POST']
        ]

    /**
     * Should be used in generating documentation for apiary, swagger, or
     * other API documentation frameworks.
     *
     * @return metadata about the API
     */
    def index() {
        def data = [
            "name": "Annotator Store API",
            "version": getGrailsApplication().metadata["app.version"]
        ]

        render (data as JSON)
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
     * Returns a list of all annotations in the database.
     *
     * TODO We should probably remove this since it gives a bit too much away.
     *
     * @return
     */
    def list() {
        def results = Annotation.list().collect { it.toJSONObject() }
        render (results as JSON)
    }

    /**
     * GETs all annotations relevant to the query. Should return a JSON object with a rows property
     * containing an array of annotations.
     *
     * @param uri
     * @param media
     * @param text
     * @param user
     * @param source
     * @param parent
     *
     * @return
     */
    def search() {
        def jsonObject = request.JSON
        def limit = params.limit?:10
        def offset = params.offset?:0
        def results = annotatorService.search(params.uri, params.media, params.text, params.user, params.source, params.parent, offset as int, limit as int)
        def totalCount = results?.totalCount
        def rows = results?.annotations?.collect { it.toJSONObject() }

        render ([total: totalCount, limit: limit, offset: offset, rows: rows] as JSON)
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
        def jsonObject = request.JSON
        def annotation = annotatorService.delete(jsonObject.id)
        if (!annotation) {
            render(status: 404, text: "Annotation not found! No update performed")
            return
        }
        render annotation.toJSONObject() as JSON
    }

    /**
     * Issues a PUT and the annotation is marked as archived but a copy of it is kept in the storage.
     *
     * @return
     */
    def archive() {
        def jsonObject = request.JSON
        def annotation = annotatorService.archive(jsonObject.id)
        if (!annotation) {
            render(status: 404, text: "Annotation not found! No update performed")
            return
        }
        render annotation.toJSONObject() as JSON


    }


}
