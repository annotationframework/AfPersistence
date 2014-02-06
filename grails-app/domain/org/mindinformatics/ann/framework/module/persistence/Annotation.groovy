package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON
import org.apache.commons.lang.builder.ReflectionToStringBuilder
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.security.users.User

import java.text.SimpleDateFormat

class Annotation {

    String text
    String quote
    String media
    String userid
    String username
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

    static transients = ["comments"]
    static hasMany = [tags : Tag]

    static constraints = {
        uri(nullable: false)
        media(nullable: true)
        text(nullable: true)
        quote(nullable: true)
        userid(nullable: true)
        username(nullable: true)
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

    def getComments() {
        //return Annotation.findAllByParent(this)
        return Annotation.where {
            ((deleted == false || deleted == null) && (archived == false || archived == null))
            parent == this
        }.list([sort:"dateCreated", order: "desc"])
    }

    /**
     * Converts domain object to JSON format.
     *
     * @return
     */
    JSONObject toJSONObject() {
        if (!json) {
            throw new RuntimeException("Cannot convert to JSON - object is empty")
        }

        def isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SZ")
        def jsonObject = JSON.parse(json)
        jsonObject.id = id
        jsonObject.updated = lastUpdated?isoFormatter.format(lastUpdated):null
        jsonObject.created = dateCreated?isoFormatter.format(dateCreated):null
        jsonObject.totalComments = comments.size()
        jsonObject.archived = archived?:false
        jsonObject.deleted = deleted?:false

        // Removed the obfuscation code
        //if (jsonObject.geolocation) {
        //    jsonObject.geolocation.latitude = 0.0
        //    jsonObject.geolocation.longitude = 0.0
        //}

        return jsonObject
    }


    @Override
    public String toString() {
        final ReflectionToStringBuilder reflectionToStringBuilder = new ReflectionToStringBuilder(this);
        return reflectionToStringBuilder.toString();
    }

}
