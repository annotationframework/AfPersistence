package org.mindinformatics.ann.framework.module.persistence

class Annotation {

    String text
    String quote
    String uri
    String json

    static hasMany = [ranges : AnnotationRange]

    static constraints = {
        text(nullable: false)
        quote(nullable: false)
        uri(nullable: false)
        json(nullable: false)
    }

    static mapping = {
        text sqlType:"text"
        quote sqlType:"text"
        uri sqlType:"text"
        json sqlType:"text"
    }
}
