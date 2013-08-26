package org.mindinformatics.ann.framework.module.converter

import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.persistence.Annotation
import org.springframework.core.convert.converter.Converter

/**
 * We don't have classes for the different annotation types yet, so we'll just have to use the
 * Annotation class as the vehicle.  There's a JSON attribute within Annotation that will
 * be used to hold the actual annotation format.
 *
 * Essentially a no-op to test the ConversionService.
 */
final class AnnotationConverter implements Converter<Annotation, Annotation> {

    /**
     * @param source
     * @return  the source object
     */
    public Annotation convert(Annotation source) {
        def target = new Annotation()
        target.id = source.id
        target.dateCreated = source.dateCreated
        target.lastUpdated = source.lastUpdated
        target.owner = source.owner
        target.uri = source.uri
        target.json = source.json
        return target
    }
}
