package org.mindinformatics.ann.framework.module.persistence

import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission

class AnnotationPermission {

    AnnotationUser user
    Permission permission

    static belongsTo = [annotation : Annotation]

    static constraints = {
        permission nullable: false
        user nullable: true
    }
}
