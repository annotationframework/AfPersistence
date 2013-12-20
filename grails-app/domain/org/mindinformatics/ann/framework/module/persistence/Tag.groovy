package org.mindinformatics.ann.framework.module.persistence

class Tag {

    String name

    Date dateCreated
    Date lastUpdated

    static belongsTo = [ annotation: Annotation ]

    static constraints = {
        name(blank:false, unique: true)
    }
}
