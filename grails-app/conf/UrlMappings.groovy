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
		"500"(view:'/error')
	}
}
