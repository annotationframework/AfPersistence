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

    String collectionId
    String contextId

    // Associations
    Annotation parent

    // Metadata
    String json
    Boolean deleted
    Boolean archived
    Date dateCreated
    Date lastUpdated
    AnnotationUser owner

    static transients = ["comments"]
    static hasMany = [tags : Tag, permissions: AnnotationPermission]

    static constraints = {
        uri(nullable: false, size: 1..2048)
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
        parent(nullable:true,display:false,editable:false)
        tags(display:false,editable:false)
        collectionId(nullable:true)
        contextId(nullable:true)
    }

    static mapping = {
        uri index: "annotation_uri_idx"
        userid index: "annotation_user_idx"
        username index: "annotation_user_idx"
        media index: "annotation_media_idx"
        source index: "annotation_source_idx"
        collectionId index: "annotation_context_idx"
        contextId index: "annotation_context_idx"

        json sqlType:"text"
        text sqlType:"text"
        quote sqlType:"text"

    }

    def getComments() {
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
            throw new RuntimeException("Cannot convert to JSON Object as JSON string is null")
        }

        def isoFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z")
        def jsonObject = JSON.parse(json)
        jsonObject.id = id
        jsonObject.updated = lastUpdated?isoFormatter.format(lastUpdated):null
        jsonObject.created = dateCreated?isoFormatter.format(dateCreated):null
        jsonObject.totalComments = comments.size()
        jsonObject.archived = archived?:false
        jsonObject.deleted = deleted?:false
        return jsonObject
    }


    @Override
    public String toString() {
        return "${id}:${uri}:${source}:${text}:${quote}:${username}:${userid}"
    }

}
