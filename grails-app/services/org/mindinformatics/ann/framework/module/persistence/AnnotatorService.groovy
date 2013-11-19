package org.mindinformatics.ann.framework.module.persistence

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
        def annotation = new Annotation(
            uri: jsonObject.uri,
            json: jsonObject.toString(),
            text: jsonObject.text,
            media: jsonObject?.media,
            source: jsonObject?.target?.src,
            userid:  jsonObject?.user?.id?:jsonObject?.user?.name
        )
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
        def annotation = Annotation.get(jsonObject.id)
        if (annotation) {
            annotation.uri = jsonObject.uri
            annotation.json = jsonObject.toString()

            if (jsonObject.text) annotation.text= jsonObject.text
            if (jsonObject.media) annotation.media= jsonObject?.media
            if (jsonObject.target) annotation.source= jsonObject?.target?.src
            if (jsonObject.user) annotation.userid=  jsonObject?.user?.id?:jsonObject?.user?.name

            annotation.save(failOnError: true)
        }
        return annotation
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
     *
     * @param id
     * @return
     */
    def destroy(id) {
        def annotation = Annotation.get(id)
        if (annotation) {
            annotation.delete()
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
    def search(uri) {
        //return Annotation.findAllByUri(uri).collect { it.toJSONObject() }
        def query = Annotation.where {
            uri == uri
        }
        return query.list().collect { it.toJSONObject() }
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
    def search(uri, media, text, userid, source) {
        def query = Annotation.where {
            if (uri) uri =~ uri + "%"
            if (media) media == media
            if (text) text =~ "%" + text + "%"
            if (userid) userid == userid
            if (source) source == source
        }
        return query.list().collect { it.toJSONObject() }
    }

    /**
     * Generate a token to be used by the annotator client.  Not used at the moment.
     *
     * See https://github.com/okfn/annotator/wiki/Authentication
     */
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



    /**
     * Oracle implementation -- cannot find Oracle Security jar
     * http://docs.oracle.com/cd/E23943_01/apirefs.1111/e26380/oracle/security/restsec/jwt/JwtToken.html
     */
    /*
    def generateTokenUsingOracle() {
        JwtToken jwtToken = new JwtToken();
        jwtToken.setType(JwtToken.JWT);
        jwtToken.setIssuer("my.oracle.com");
        jwtToken.setPrincipal("john.doe");
        String jwtString = jwtToken.serializeUnsigned();
    }
    */

    def generateTokenUsingJsonToken() {
        //HmacSHA256Signer signer = new HmacSHA256Signer("google.com", "key2", SYMMETRIC_KEY);
        //JsonToken token = new JsonToken(signer, clock);
        //token.setParam("bar", 15);
        //token.setParam("foo", "some value");
        //token.setAudience("http://www.google.com");
        //token.setIssuedAt(clock.now());
        //token.setExpiration(clock.now().withDurationAdded(60, 1));
        //return token.serializeAndSign();
    }

    /*
    private String getJWT() throws InvalidKeyException, SignatureException {
        JsonToken token = null;
        token = createToken();
        return token.serializeAndSign();
    }
    */
    /*
    private JsonToken createToken() throws InvalidKeyException{
        //Current time and signing algorithm
        Calendar cal = Calendar.getInstance();
        HmacSHA256Signer signer = new HmacSHA256Signer(ISSUER, null, SIGNING_KEY.getBytes());

        //Configure JSON token
        JsonToken token = new JsonToken(signer);
        token.setAudience("Google");
        token.setParam("typ", "google/payments/inapp/item/v1");
        token.setIssuedAt(new Instant(cal.getTimeInMillis()));
        token.setExpiration(new Instant(cal.getTimeInMillis() + 60000L));

        //Configure request object
        JsonObject request = new JsonObject();
        request.addProperty("name", "Piece of Cake");
        request.addProperty("description", "Virtual chocolate cake to fill your virtual tummy");
        request.addProperty("price", "10.50");
        request.addProperty("currencyCode", "USD");
        request.addProperty("sellerData", "user_id:1224245,offer_code:3098576987,affiliate:aksdfbovu9j");

        JsonObject payload = token.getPayloadAsJsonObject();
        payload.add("request", request);

        return token;
    }
    */


    def getToken() {

        // Given a user instance
        // Compose the JWT claims set
        JWTClaimsSet jwtClaims = new JWTClaimsSet();
        jwtClaims.setIssueTime(new Date());
        jwtClaims.setJWTID(UUID.randomUUID().toString());
        jwtClaims.setCustomClaim("userId", "jmiranda");
        jwtClaims.setCustomClaim("consumerKey", "openannotation");
        jwtClaims.setCustomClaim("ttl", 86400);
        // 2013-08-30T22:23:30+00:00
        jwtClaims.setCustomClaim("issuedAt", new Date().format("yyyy-MM-dd'T'hh:mm:ssZ"));


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
        } catch(com.nimbusds.jose.JOSEException e) {
            System.err.println("Error signing JWT: " + e.getMessage());
            return;
        }

        // Serialise to JWT compact form
        //String jwtString = jwsObject.serialize();
        return jwsObject.serialize()
    }

}
