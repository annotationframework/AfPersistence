package org.mindinformatics.ann.framework.module.converters;

import grails.converters.JSON

import org.junit.*
import org.mindinformatics.ann.framework.module.converters.AnnotatorToOpenAnnotationConverter;

class AnnotatorToOpenAnnotationConverterTest {

	@Test
	void normalizeTest() {
		def json = '{"tags":[],"text":"safsafsa","quote":"hall","ranges":[{"endOffset":16,"start":"/div[1]/h4[1]","end":"/div[1]/h4[1]","startOffset":12}],"permissions":{"update":[],"admin":[],"delete":[],"read":[]},"uri":"http://afdemo.aws.af.cm/annotator/index","user":"jmiranda"}';
		
		def jsonObject = JSON.parse(json);
		
		AnnotatorToOpenAnnotationConverter a = new AnnotatorToOpenAnnotationConverter();
		a.normalize(jsonObject);
	}
}