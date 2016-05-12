package org.mindinformatics.ann.framework.module.persistence

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
