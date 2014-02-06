package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON

class AnnotationController {

    def scaffold = Annotation
    def annotatorService

    def refreshAnnotations() {
        log.info  '>> Refresh annotation tags for all annotations'
        def count = 0
        def annotations = Annotation.list()
        annotations.collect().each { annotation ->
            if(annotation.json) {
                def jsonObject = JSON.parse(annotation.json)
                if (jsonObject.tags) {
                    log.info "Updating tags ${jsonObject.tags} for annotation ${annotation.id}"
                    annotatorService.updateTags(annotation, jsonObject.tags)
                    count++
                }
            }
        }
        flash.message = "Refreshed ${count} annotations"
        redirect(action:"list")
    }
}
