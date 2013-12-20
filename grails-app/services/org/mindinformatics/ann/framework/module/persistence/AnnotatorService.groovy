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
import org.codehaus.groovy.grails.web.json.JSONObject

class AnnotatorService {

    private static final String SHARED_KEY = "{shared key}";

    /**
     * Create an annotation using properties from the given the json object.
     *
     * @param jsonObject
     * @return
     */
    def create(jsonObject) {
        def parent = jsonObject.parent ? Annotation.get(jsonObject.parent) : null
        def annotation = new Annotation(
            uri: jsonObject.uri,
            json: jsonObject.toString(),
            text: jsonObject.text,
            quote: jsonObject.quote,
            media: jsonObject?.media,
            source: jsonObject?.target?.src,
            userid:  jsonObject?.user?.id,
            username: jsonObject?.user?.name,
            parent: parent
        )
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

        println "Update: ${jsonObject.id}"
        def annotation = Annotation.get(jsonObject.id)
        println "annotation: ${annotation}"
        if (annotation) {
            annotation.uri = jsonObject.uri
            annotation.json = jsonObject.toString()

            if (jsonObject.text) annotation.text = jsonObject.text
            if (jsonObject.media) annotation.media = jsonObject?.media
            if (jsonObject.target) annotation.source = jsonObject?.target?.src
            if (jsonObject.user) annotation.userid = jsonObject?.user?.id?:jsonObject?.user?.name
            if (jsonObject.quote) annotation.quote = jsonObject?.quote
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
        // Associate all tags with the given
        if (tags) {
            annotation?.tags?.clear()
            tags.each { tagName ->
                def tag = Tag.findByName(tagName)
                if (!tag) {
                    tag = new Tag(name: tagName)
                }
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
    def search(params) {
        println "Search with params: " + params
        def query = Annotation.where {
            ((deleted == false || deleted == null) && (archived == false || archived == null))
            if (params.uri) uri =~ params.uri + "%"
            if (params.media) media == params.media
            if (params.quote) quote =~ "%" + params.quote + "%"
            if (params.text) text =~ "%" + params.text + "%"
            if (params.userid) userid == params.userid
            if (params.username) username == params.username
            if (params.source) source == params.source
            if (params.dateCreatedOnOrAfter) {
                dateCreated >= params.dateCreatedOnOrAfter
            }
            if (params.dateCreatedOnOrBefore) {
                dateCreated <= params.dateCreatedOnOrBefore
            }
            if (params.parentid) {
                def parentAnnotation = Annotation.get(params.parentid)
                parent == parentAnnotation
            }
            if (params.tag) {
                tags {
                    name =~ "%" + params.tag + "%"
                }
            }
        }

        // FIXME Don't like that I need to do execute two queries here
        def results = query.list([offset: params.offset, max: params.limit, sort:"dateCreated", order: "desc"])
        //results = results.reverse()
        def totalCount = query.list().size()

        return [annotations: results, totalCount: totalCount]
    }

    /**
     * Generate a java web token.
     *
     * @param userId
     * @param consumerKey
     * @param ttl
     * @return
     */
    def getToken(userId, consumerKey, ttl) {
        return getToken(userId, consumerKey, ttl, new Date())
    }


    /**
     * Generate a java web token.
     *
     * @return
     */
    def getToken(userId, consumerKey, ttl, issuedAt) {
        // Given a user instance
        // Compose the JWT claims set
        JWTClaimsSet jwtClaims = new JWTClaimsSet();
        jwtClaims.setIssueTime(issuedAt);
        jwtClaims.setJWTID(UUID.randomUUID().toString());
        jwtClaims.setCustomClaim("userId", userId);
        jwtClaims.setCustomClaim("consumerKey", consumerKey);
        jwtClaims.setCustomClaim("ttl", ttl);
        jwtClaims.setCustomClaim("issuedAt", issuedAt.format("yyyy-MM-dd'T'hh:mm:ssZ")); // e.g. 2013-08-30T22:23:30+00:00
        // jwtClaims.setCustomClaim("email", user.email);

        // Create JWS header with HS256 algorithm
        //JWSHeader
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        header.setContentType("text/plain");
        header.setType(JOSEObjectType.JWS)

        // Create JWS object
        JWSObject jwsObject = new JWSObject(header, new Payload(jwtClaims.toJSONObject()));

        // Create HMAC signer

        JWSSigner signer = new MACSigner(SHARED_KEY.getBytes());

        try {
            jwsObject.sign(signer);
        } catch(JOSEException e) {
            System.err.println("Error signing JWT: " + e.getMessage());
            return;
        }

        // Serialise to JWT compact form
        //String jwtString = jwsObject.serialize();
        return jwsObject.serialize()
    }

    /**
     *
     * @param token
     * @return
     */
    def verifyToken(token) {
        def jwsObject = JWSObject.parse(token);
        JWSVerifier verifier = new MACVerifier(SHARED_KEY.getBytes());
        println "Payload: ${jwsObject.payload}"
        return jwsObject.verify(verifier)
    }

    /**
     * Generate a token to be used by the annotator client.  Not used at the moment.
     *
     * See https://github.com/okfn/annotator/wiki/Authentication
    def generateToken() {
        // Create JWS payload
        Payload payload = new Payload("Hello world!");

        // Create JWS header with HS256 algorithm
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        header.setContentType("text/plain");

        // Create JWS object
        JWSObject jwsObject = new JWSObject(header, payload);

        // Create HMAC signer
        String sharedKey = "our-shared-key";
        JWSSigner signer = new MACSigner(sharedKey.getBytes());
        jwsObject.sign(signer);

        // Serialise JWS object to compact format
        String s = jwsObject.serialize();
        println("Serialised JWS object: " + s);

        // Parse back and check signature
        jwsObject = JWSObject.parse(s);

        JWSVerifier verifier = new MACVerifier(sharedKey.getBytes());
        boolean verifiedSignature = jwsObject.verify(verifier);
        if (verifiedSignature)
            println("Verified JWS signature!");
        else
            println("Bad JWS signature!");

        println("Recovered payload message: " + jwsObject.getPayload());
        return jwsObject.getPayload()
    }
     */


}
