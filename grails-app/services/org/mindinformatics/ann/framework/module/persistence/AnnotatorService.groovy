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
import org.mindinformatics.ann.framework.module.org.mindinformatics.ann.framework.module.persistence.Permission
import org.mindinformatics.ann.framework.module.security.systems.SystemApi

class AnnotatorService {

    static GROUP_WORLD = 'group:__world__'
    static GROUP_AUTHENTICATED = 'group:__authenticated__'
    static GROUP_CONSUMER = 'group:__consumer__'

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

        def annotation = new Annotation()
        annotation.json = jsonObject.toString()
        annotation.uri = jsonObject.uri
        annotation.text = jsonObject.text
        annotation.quote = jsonObject.quote
        annotation.media = jsonObject?.media
        annotation.source = jsonObject?.target?.src
        annotation.collectionId = jsonObject.collectionId
        annotation.contextId = jsonObject.contextId

        if (jsonObject.user) {
            updateUser(annotation, jsonObject)
        }

        // Set the parent if one has been passed in
        def parent = jsonObject.parent ? Annotation.get(jsonObject.parent) : null
        annotation.parent = parent

        // FIXME As a workaround we need to save the annotation before we can add tags to it
        annotation.save(flush:true)

        // Add tags to the annotation and save again
        log.info "Adding tags " + jsonObject.tags
        if (jsonObject.tags) {
            updateTags(annotation, jsonObject.tags)
        }

        log.info "Adding permissions " + jsonObject.permissions
        if (jsonObject.permissions) {
            updatePermissions(annotation, jsonObject)
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
            if (jsonObject.quote) annotation.quote = jsonObject?.quote
            if (jsonObject.collectionId) annotation.collectionId = jsonObject.collectionId
            if (jsonObject.contextId) annotation.contextId = jsonObject.contextId

            if (jsonObject.user) {
                updateUser(annotation, jsonObject)
            }

            if (jsonObject.parent) {
                annotation.parent = Annotation.get(jsonObject.parent)
            }
            if (jsonObject.tags) {
                updateTags(annotation, jsonObject.tags)
            }

            if (jsonObject.permissions) {
                updatePermissions(annotation, jsonObject)
            }

            annotation.save(failOnError: true)
        }
        return annotation
    }

    /**
     * Example:
     * "user":{"id":"justin.miranda@gmail.com","name":"justin.miranda"}
     *
     * @param annotation
     * @param jsonObject
     * @return
     */
    def updateUser(Annotation annotation, jsonObject) {

        if (jsonObject.user) {

            if (jsonObject.user instanceof String) {
                annotation.userid = jsonObject.user
                annotation.username = jsonObject.user
            }
            else {
                annotation.userid = jsonObject.user.id
                annotation.username = jsonObject.user.name

                AnnotationUser user = AnnotationUser.findByUserId(jsonObject.user.id)
                if (!user) {
                    user = new AnnotationUser()
                }

                user.userId = jsonObject.user.id
                user.username = jsonObject.user.name
                user.email = jsonObject.user.email
                user.save(flush: true)
            }

        }
    }

    /**
     * Update the tags for a given annotation.
     *
     * @param annotation
     * @param tags
     * @return
     */
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
     * Update permissions for the given annotation.
     *
     * Example JSON:
     * "permissions":{"update":["justin.miranda@gmail.com"],"admin":["justin.miranda@gmail.com"],"delete":["justin.miranda@gmail.com"],"read":["justin.miranda@gmail.com"]}
     *
     * @param annotation
     * @param permissions
     * @return
     */
    def updatePermissions(Annotation annotation, jsonObject) {
        println "Update permissions " + jsonObject

        // If there's no read permission, we need to add a default
        if (!jsonObject.permissions["read"]) {
            jsonObject.permissions["read"] = ['group:__world__']
        }


        if (jsonObject.permissions) {

            // If the annotation already has permissions, we need to clear them so we can add the new ones
            if (annotation.permissions) {
                annotation.permissions.clear()
            }

            // Iterate over user-provide permissions, lookup user/permission, and add new ACL entry to annotation
            jsonObject.permissions.each { permissionId, userIds ->
                if (userIds) {
                    userIds.each { userId ->
                        AnnotationUser user = AnnotationUser.findByUserId(userId)
                        if (!user) {
                            user = new AnnotationUser(userId: userId)
                            if (!user.save(flush: true)) {
                                throw new RuntimeException("User with user id ${userId} was not found.")
                            }
                        }

                        Permission permission = Permission.findById(permissionId)
                        if (!permission) {
                            throw new RuntimeException("Permission ${permissionId} is not recognized.")
                        }

                        // Create a new ACL entry
                        AnnotationPermission annotationPermission = new AnnotationPermission()
                        annotationPermission.permission = permission
                        annotationPermission.user = user

                        // Add ACL entry to annotation
                        annotation.addToPermissions(annotationPermission)

                    }
                }
            }

            // Save all changes to the annotation
            annotation.save(flush: true)
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
    def search(params) {
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


        def annotations = results
//        def annotations = []
//        if (uid) {
//            results.each { annotation ->
//                JSONObject json = annotation.toJSONObject()
//
//                boolean permissionsContainUid = (uid in json?.permissions?.read)
//                boolean permissionsEmpty = json?.permissions?.read?.empty
//
//                // Add annotation to results if
//                if (permissionsEmpty || permissionsContainUid) {
//                    annotations << annotation
//                }
//            }
//        }
//        else {
//            throw new IllegalArgumentException("Auth token does not contain a valid user ID")
//        }

        return [totalCount: totalCount, size: annotations.size(), annotations: annotations]
    }


    def searchSecure (Map params, String uid) {

        def queryParams = buildQueryParams(params, uid)

        // Get annotations
        def baseQuery = buildQuery(queryParams, false)
        log.info "Base Query: " + baseQuery
        log.info "Query params: " + queryParams
        def annotations = Annotation.executeQuery(baseQuery, queryParams);

        // Get total count
        def countQuery = buildQuery(queryParams, true)
        def totalCount = Annotation.executeQuery(countQuery, queryParams);


        return [totalCount: totalCount[0], size: annotations.size(), annotations: annotations]
    }

    /**
     *  We need to do this parameter cleansing because otherwise we end up with these annoying exceptions
     * (e.g. QueryParameterException: could not locate named parameter [action]) for every http parameter that is
     * not mapped in the query (action, controller, limit, etc). There's probably a better (more Groovy) way to
     * do this, but this'll work for now.
     *
     * @param params
     * @param uid
     * @return
     */
    Map buildQueryParams(Map params, String uid) {
        Map queryParams = [:]

        if (uid) queryParams.uid = uid
        if (params.limit) queryParams.max = params.limit
        if (params.uri) queryParams.uri = params.uri
        if (params.media) queryParams.media = params.media
        if (params.quote) queryParams.quote = "%" + params.quote + "%";
        if (params.text) queryParams.text = "%" + params.text + "%";
        if (params.userids) queryParams.userids = params.list("userid")?:params.list("userid[]");
        if (params.usernames) queryParams.usernames = params.list("username")?:params.list("username[]");
        if (params.source) queryParams.media = params.media
        if (params.contextId) queryParams.contextId = params.contextId
        if (params.collectionId) queryParams.collectionId = params.collectionId
        if (params.parentid) queryParams.parentid = params.parentid
        if (params.tag) queryParams.tag = "%" + params.tag + "%";
        if (params.dateCreatedOnOrAfter) queryParams.dateCreatedOnOrAfter = params.dateCreatedOnOrAfter
        if (params.dateCreatedOnOrBefore) queryParams.dateCreatedOnOrBefore = params.dateCreatedOnOrBefore

        return queryParams

    }

    String buildQuery(Map queryParams, boolean isCountQuery) {
        def query = ""

        if (!isCountQuery) {
            query += "SELECT distinct(annotation) "
        }
        else {
            query += "SELECT count(distinct annotation.id) "
        }

        query += """
            FROM Annotation AS annotation
            LEFT OUTER JOIN annotation.permissions AS permission
            LEFT OUTER JOIN permission.user AS user
            LEFT OUTER JOIN annotation.tags AS tag
            WHERE (annotation.deleted = false OR annotation.deleted is null)
            AND (annotation.archived = false OR annotation.archived is null)
            AND (permission.permission = 'read' AND (user.userId = :uid OR user.userId = 'group:__world__'))
            """

        if (queryParams.uri) { query += " AND annotation.uri = :uri" }
        if (queryParams.media) { query += " AND annotation.media = :media" }
        if (queryParams.quote) { query += " AND annotation.quote = :quote" }
        if (queryParams.text) { query += " AND annotation.text = :text" }
        if (queryParams.userids) { query += " AND annotation.userid IN :userids" }
        if (queryParams.usernames) { query += " AND annotation.username IN :usernames" }
        if (queryParams.source) { query += " AND annotation.source = :source" }
        if (queryParams.contextId) { query += " AND annotation.contextId = :contextId" }
        if (queryParams.collectionId) { query += " AND annotation.collectionId = :collectionId" }
        if (queryParams.parentid) { query += " AND annotation.parentid = :parentid" }
        if (queryParams.tag) { query += " AND tag.name LIKE :tag" }
        if (queryParams.dateCreatedOnOrAfter) { query += " AND annotation.dateCreated >= :dateCreatedOnOrAfter" }
        if (queryParams.dateCreatedOnOrBefore) { query += " AND annotation.dateCreated <= :dateCreatedOnOrBefore" }

        if (!isCountQuery) {
            query += " ORDER by annotation.dateCreated desc"
        }

        log.info("Query: " + query)
        return query
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

    def migrateAnnotations() {
        long startTime = System.currentTimeMillis()
        int count = 1
        def annotations = [] //Annotation.where { isNotNull("permissions") }.list([max:1000])
        log.debug "Found ${annotations.size()} annotations to migrate"

        // Use collect in order to avoid ConcurrentModificationException
        annotations.collect().each { annotation ->
            if(annotation.json) {
                def jsonObject = JSON.parse(annotation.json)
                if (jsonObject.permissions) {
                    log.info "Updating permissions ${jsonObject.permissions} for annotation ${annotation.id}"
                    //updateTags(annotation, jsonObject.tags)
                    updatePermissions(annotation, jsonObject.permissions)
                    count++
                }
                else {
                    log.info "No permissions for annotation ${annotation.id}"

                }
                if (count % 100 == 0) {
                    log.info "Refreshed ${count} of ${annotations?.size()?:0} annotations: " + (System.currentTimeMillis() - startTime) + " ms"
                    cleanUpGorm()
                }
            }
        }
    }

}
