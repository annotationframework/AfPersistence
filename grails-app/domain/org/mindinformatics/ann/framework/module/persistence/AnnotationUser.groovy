package org.mindinformatics.ann.framework.module.persistence

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString
@EqualsAndHashCode(includeFields=true)
class AnnotationUser {

    String userId
    String username
    String email

    static constraints = {
        userId nullable: true
        username nullable: true
        email nullable: true
    }
}
