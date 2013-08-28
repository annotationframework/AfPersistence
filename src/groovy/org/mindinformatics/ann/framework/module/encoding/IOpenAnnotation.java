package org.mindinformatics.ann.framework.module.encoding;

public interface IOpenAnnotation {

	public static final String NAMESPACE = "http://www.w3.org/ns/oa#";
	
	public static final String CLASS_ANNOTATION = "oa:Annotation";
	public static final String CLASS_ANNOTATION_NAME = "Annotation";
	public static final String CLASS_ANNOTATION_URI = NAMESPACE + "Annotation";
	
	public static final String CLASS_SPECIFICRESOURCE_URI = NAMESPACE + "SpecificResource";
	
	public static final String PROPERTY_HASBODY_URI ="http://www.w3.org/ns/oa#hasBody";
	public static final String PROPERTY_HASTARGET_URI ="http://www.w3.org/ns/oa#hasTarget";
	public static final String PROPERTY_HASSOURCE_URI ="http://www.w3.org/ns/oa#hasSource";
	public static final String PROPERTY_HASSELECTOR_URI ="http://www.w3.org/ns/oa#hasSelector";
}
