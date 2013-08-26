package org.mindinformatics.ann.framework.module.persistence

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.security.users.User

class Annotation {

    String uri
    String json
    //String text
    //String quote

    Date dateCreated
    Date lastUpdated

    User owner

    //static hasMany = [ranges : AnnotationRange]

    static constraints = {
        uri(nullable: false)
        json(nullable: false)
        owner(nullable: true)
        //text(nullable: true)
        //quote(nullable: false)
    }

    static mapping = {
        uri sqlType:"text"
        json sqlType:"text"
        //text sqlType:"text"
        //quote sqlType:"text"
    }


    JSONObject toJSONObject() {
        println "toJSONObject " + json
        if (!json) {
            throw new RuntimeException("Cannot convert to JSON when object is empty")
        }

        def jsonObject = new JSONObject()
        jsonObject["id"] = id
        println "jsonObject: " + jsonObject
        return jsonObject

    }


}
