package org.mindinformatics.ann.framework.module.persistence

import grails.converters.JSON

import org.codehaus.groovy.grails.web.json.JSONObject

class TripleStoreController {

	def iTripleStorePersistence
	
	def index = {
		render 'hello'
	}
	
	
	
	def setmessage = {
		render iTripleStorePersistence.store("paolo", "http://google.com", null)
	}
	
	def getmessage = {
		iTripleStorePersistence.retrieve("http://google.com")
		render 'done'
	}
	
	def retrieveGraph = {
		JSONObject graph = iTripleStorePersistence.retrieveGraphAsJson(params.uri)
		render graph as JSON;
	}
}
