class UrlMappings {

	static mappings = {

        "/api/$action/$id?"{
            controller = 'annotator'
        }

        "/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

        "/"(view:"/info")
		"401"(controller: 'error', action: 'unauthorized')
		"403"(controller: 'error', action: 'forbidden')
		"404"(controller: 'error', action: 'notFound')
		"405"(controller: 'error', action: 'methodNotAllowed')
		"500"(controller: 'error', action: 'serverError')
	}
}
