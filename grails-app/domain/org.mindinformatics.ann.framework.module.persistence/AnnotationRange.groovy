package org.mindinformatics.ann.framework.module.persistence

class AnnotationRange {

    String start
    String end
    Integer endOffset
    Integer startOffset

    static belongsTo = [annotation:Annotation]

    static constraints = {
        start(nullable: false)
        end(nullable: false)
        startOffset(nullable: false)
        endOffset(nullable: false)
    }
}
