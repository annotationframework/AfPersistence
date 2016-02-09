package org.mindinformatics.ann.framework.module.persistence

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSObject
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.Payload
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import grails.converters.JSON
//import org.apache.commons.codec.binary.Base64
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.security.systems.SystemApi

class AnnotatorService {

    def sessionFactory
    def propertyInstanceMap = org.codehaus.groovy.grails.plugins.DomainClassGrailsPlugin.PROPERTY_INSTANCE_MAP

    private static final String SHARED_KEY = "{shared key}";

    def random() {
        //return Annotation.executeQuery("from Annotation order by rand()", [max: 1])
        //def annotations = Annotation.list() as List
        def annotations = Annotation.executeQuery( "select a.id from Annotation a" );
        def random = new Random()
        def index = random.nextInt(annotations.size()-1)
        def annotation = Annotation.get(annotations[index])
        return annotation
    }

    /**
     * Create an annotation using properties from the given the json object.
     *
     * @param jsonObject
     * @return
     */
    def create(jsonObject) {
        log.info "Create annotation: ${jsonObject}"

        log.info "User: " + jsonObject?.user?.class

        def annotation = new Annotation()
        annotation.uri = jsonObject.uri
        annotation.text = jsonObject.text
        annotation.quote = jsonObject.quote
        annotation.media = jsonObject?.media
        annotation.source = jsonObject?.target?.src
        annotation.collectionId = jsonObject.collectionId
        annotation.contextId = jsonObject.contextId
        // Added
        if (jsonObject?.user instanceof String) {
            annotation.userid = jsonObject?.user
            annotation.username = jsonObject?.user
        }
        else {
            annotation.userid = jsonObject?.user?.id
            annotation.username = jsonObject?.user?.name
        }
        annotation.json = jsonObject.toString()

        // Set the parent if one has been passed in
        def parent = jsonObject.parent ? Annotation.get(jsonObject.parent) : null
        annotation.parent = parent

        // FIXME As a workaround we need to save the annotation before we can add tags to it
        annotation.save(flush:true)

        // Add tags to the annotation and save again
        println "Adding tags " + jsonObject.tags
        if (jsonObject.tags) {
            updateTags(annotation, jsonObject.tags)
        }
        annotation.save(failOnError: true)
        return annotation
    }

    /**
     * Update the annotation using properties from the given json obkect
     *
     * @param jsonObject
     * @return
     */
    def update(jsonObject) {
        log.info "Update annotation: ${jsonObject}"

        def annotation = Annotation.get(jsonObject.id)
        log.info  "annotation: ${annotation}"
        if (annotation) {
            annotation.uri = jsonObject.uri
            annotation.json = jsonObject.toString()

            if (jsonObject.text) annotation.text = jsonObject.text
            if (jsonObject.media) annotation.media = jsonObject?.media
            if (jsonObject.target) annotation.source = jsonObject?.target?.src
            if (jsonObject.user) annotation.userid = jsonObject?.user?.id?:jsonObject?.user?.name
            if (jsonObject.quote) annotation.quote = jsonObject?.quote
            if (jsonObject.collectionId) annotation.collectionId = jsonObject.collectionId
            if (jsonObject.contextId) annotation.contextId = jsonObject.contextId

            if (jsonObject.parent) {
                annotation.parent = Annotation.get(jsonObject.parent)
            }
            if (jsonObject.tags) {
                updateTags(annotation, jsonObject.tags)
            }

            annotation.save(failOnError: true)
        }
        return annotation
    }

    def updateTags(annotation, tags) {
        //log.info("Updating annotation ${annotation} with tags ${tags}")
        if (tags) {
            // Fixes lazy initialization issue when refreshing annotations
            if (!annotation.isAttached()) {
                annotation.attach()
            }
            annotation?.tags?.clear()
            tags.each { tagName ->
                // Only create a single tag with the same name.
                def tag = Tag.findByName(tagName)
                if (!tag) {
                    tag = new Tag(name: tagName)
                    tag.save(flush:true)
                }
                // Add tag to the annotation if it doesn't already exist
                if (!annotation?.tags?.contains(tag)) {
                    annotation.addToTags(tag)
                }
            }
        }

    }


    /**
     * Mark annotation as deleted.
     *
     * @param id
     */
    def delete(id) {
        def annotation = Annotation.get(id)
        if (annotation) {
            annotation.deleted = true
            annotation.save()
            return true
        }
        return false

    }


    /**
     * Mark annotation as deleted.
     *
     * @param id
     */
    def archive(id) {
        def annotation = Annotation.get(id)
        if (annotation) {
            annotation.archived = true
            annotation.save()
            return true
        }
        return false

    }


    /**
     * Destroy annotation.
     *
     * @param id
     * @return
     */
    def destroy(id) {
        def annotation = Annotation.get(id)
        if (annotation) {
            annotation.delete(flush:true)
            return true
        }
        return false
    }

    /**
     * Search annotations by uri
     *
     * @param uri
     * @return
     */
    def search(uri, offset, limit) {
        //return Annotation.findAllByUri(uri).collect { it.toJSONObject() }
        def query = Annotation.where {
            uri == uri
        }
        return query.list([offset:offset, max:limit]).collect { it.toJSONObject() }
    }

    /**
     * Search the annotator store for annotations that match all of these
     *
     * @param uri
     * @param media
     * @param text
     * @param username
     *
     * @return a list of annotations that match the given parameters
     */
    def search(params, uid) {
        println  "Search with params: " + params
        def query = Annotation.where {
            // Annotation has not been deleted or archived
            ((deleted == false || deleted == null) && (archived == false || archived == null))

            // Annotation matches basic attributes
            if (params.uri) uri == params.uri
            if (params.media) media == params.media
            if (params.quote) quote =~ "%" + params.quote + "%"
            if (params.text) text =~ "%" + params.text + "%"

            // Annotation associated with a given user in parameter list
            if (params.userid) {
                userid in params.list("userid")
            }
            else if (params.list("userid[]")) {
                userid in params.list("userid[]")
            }
            if (params.username) {
                username in params.list("username")
            }
            else if (params.list("username[]")) {
                username in params.list("username[]")
            }
            if (params.source) source == params.source
            if (params.contextId) contextId == params.contextId
            if (params.collectionId) collectionId == params.collectionId

            if (params.dateCreatedOnOrAfter) {
                dateCreated >= params.dateCreatedOnOrAfter
            }
            if (params.dateCreatedOnOrBefore) {
                dateCreated <= params.dateCreatedOnOrBefore
            }
            if (params.parentid) {
                def parentAnnotation = Annotation.load(params.parentid)
                parent == parentAnnotation
            }
            if (params.tag) {
                tags {
                    name =~ "%" + params.tag + "%"
                }
            }
        }

        def totalCount = query.count();
        def results = query.list([offset: params.offset, max: params.limit, sort:"dateCreated", order: "desc"])

        def annotations = []
        if (uid) {
            results.each { annotation ->
                JSONObject json = annotation.toJSONObject()

                boolean permissionsContainUid = (uid in json?.permissions?.read)
                boolean permissionsEmpty = json?.permissions?.read?.empty

                // Add annotation to results if
                if (permissionsEmpty || permissionsContainUid) {
                    annotations << annotation
                }
            }
        }
        else {
            throw new IllegalArgumentException("Token does not contain a valid user ID")
        }

        return [totalCount: totalCount, size: annotations.size(), annotations: results]
    }

    /**
     * Generate a java web token.
     *
     * See https://github.com/okfn/annotator/wiki/Authentication
     *
     * @param userId
     * @param consumerKey
     * @param ttl
     * @return
     */
    def getToken(String apiKey, String username, Integer ttl = 86400) {
        return getToken(apiKey, username, ttl, new Date())
    }


    /**
     * Generate a java web token.
     *
     * @return
     */
    def getToken(String apiKey, String username, Integer ttl, Date issuedAt) {
        log.info ("Get token username=" + username + ", apiKey=" + apiKey + ", ttl=" + ttl + ", issuedAt=" + issuedAt)
        // Given a user instance
        // Compose the JWT claims set
        JWTClaimsSet jwtClaims = new JWTClaimsSet();
        jwtClaims.setIssueTime(issuedAt);
        jwtClaims.setJWTID(UUID.randomUUID().toString());
        jwtClaims.setCustomClaim("userId", username);
        jwtClaims.setCustomClaim("consumerKey", apiKey);
        jwtClaims.setCustomClaim("ttl", ttl);
        jwtClaims.setCustomClaim("issuedAt", issuedAt.format("yyyy-MM-dd'T'HH:mm:ss z")); // e.g. 2013-08-30T22:23:30+00:00
        // jwtClaims.setCustomClaim("email", user.email);

        // Create JWS header with HS256 algorithm
        //JWSHeader
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        header.setContentType("text/plain");
        header.setType(JOSEObjectType.JWS)

        // Used to debug issue with commons-codec library
        //System.out.println(Base64.class.getProtectionDomain().getCodeSource().getLocation());

        // Create JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));

        SystemApi systemApi = SystemApi.findByApikey(apiKey);
        if (!systemApi) {
            log.info("System API key ${apiKey} does not exist")
            throw new IllegalArgumentException("Unable to locate a registered consumer with API key '" + apiKey + "'." );
        }

        // Check if system API is disabled
        if (!systemApi.enabled) {
            throw new IllegalArgumentException("System API with key " + apiKey + " is currently disabled. Please contact your system administrator.");
        }

        // Use API key as secret key for backwards compatibility
        try {
            // Create HMAC signer
            String secretKey = systemApi?.secretKey?:systemApi?.apikey
            JWSSigner signer = new MACSigner(secretKey?.getBytes());
            jwsObject.sign(signer);
        } catch(JOSEException e) {
            System.err.println("Error signing JWT: " + e.getMessage());
            throw new RuntimeException("Error signing JWT with API key and secret: " + e.message)
        }

        // Serialise to JWT compact form
        //String jwtString = jwsObject.serialize();
        return jwsObject.serialize()
    }


    def cleanUpGorm() {
        def session = sessionFactory.currentSession
        session.flush()
        session.clear()
        propertyInstanceMap.get().clear()
    }

    def refreshAnnotations(params) {
        def startTime = System.currentTimeMillis()
        log.info  '>> Refresh annotation tags for all annotations'
        def count = 0
        def annotations = Annotation.list(params)
        // Call collect in order to avoid ConcurrentModificationException
        annotations.collect().each { annotation ->
            if(annotation.json) {
                def jsonObject = JSON.parse(annotation.json)
                if (jsonObject.tags) {
                    //log.info "Updating tags ${jsonObject.tags} for annotation ${annotation.id}"
                    updateTags(annotation, jsonObject.tags)
                    count++
                }
                if (count % 100 == 0) {
                    log.info "Refreshed ${count} of ${annotations?.size()?:0} annotations: " + (System.currentTimeMillis() - startTime) + " ms"
                    cleanUpGorm()
                }
            }
        }
        log.info "Refreshed ${count} of ${annotations.size()} annotations: " + (System.currentTimeMillis() - startTime) + " ms"
        return count;
    }


}
