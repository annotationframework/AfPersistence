package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.security.users.User

import java.text.SimpleDateFormat

class Annotation {

    String text
    String quote
    String media
    String userid
    String source
    String uri

    // Associations
    Annotation parent

    // Metadata
    String json
    Boolean deleted
    Boolean archived
    Date dateCreated
    Date lastUpdated
    User owner

    //static hasMany = [ranges : AnnotationRange]

    static constraints = {
        uri(nullable: false)
        media(nullable: true)
        text(nullable: true)
        quote(nullable: true)
        userid(nullable: true)
        source(nullable: true)
        owner(nullable: true)
        json(nullable: false)
        deleted(nullable: true)
        archived(nullable: true)
        parent(nullable:true)
    }

    static mapping = {
        uri sqlType:"text"
        json sqlType:"text"
        text sqlType:"text"
        quote sqlType:"text"
    }

    /**
     * Converts domain object to JSON format.
     *
     * @return
     */
    JSONObject toJSONObject() {
        println "toJSONObject " + json
        if (!json) {
            throw new RuntimeException("Cannot convert to JSON - object is empty")
        }

        def isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SZ")
        def jsonObject = JSON.parse(json)
        jsonObject["id"] = id
        jsonObject["updated"] = isoFormatter.format(lastUpdated)
        jsonObject["created"] = isoFormatter.format(dateCreated)
        return jsonObject

    }


}
