package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON

class AnnotationController {

    def scaffold = Annotation
    def annotatorService

    def refreshAnnotations() {
        def updatedCount = annotatorService.refreshAnnotations()
        flash.message = "Refreshed ${updatedCount} annotations"
        redirect(action:"list")
    }
}
