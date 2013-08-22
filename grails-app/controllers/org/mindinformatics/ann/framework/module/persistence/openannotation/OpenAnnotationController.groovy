package org.mindinformatics.ann.framework.module.persistence.openannotation

import grails.converters.JSON

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.web.json.JSONObject

class OpenAnnotationController {

	def iTripleStorePersistence
	
	def blob = {
		def grailsApplication = ApplicationHolder.application;
		def url = grailsApplication.config.af.node.base.url + 'blob/' + params.id;
		JSONObject graph = iTripleStorePersistence.retrieveGraphAsJson(url)
		JSON.use('deep')
		render graph as JSON;
	}
}
