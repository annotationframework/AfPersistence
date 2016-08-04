package org.mindinformatics.ann.framework.module.persistence

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission


@ToString(includeNames = true, includeFields = true, includePackage = false)
@EqualsAndHashCode(includeFields=true)
class AnnotationPermission {

    Annotation annotation
    AnnotationUser user
    Permission permission

    static belongsTo = [annotation : Annotation]

    static constraints = {
        annotation nullable: false
        permission nullable: false
        user nullable: true
    }




}
