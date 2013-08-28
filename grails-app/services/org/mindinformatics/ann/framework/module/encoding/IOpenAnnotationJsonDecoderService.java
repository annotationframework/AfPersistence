/*
 * Copyright 2013 Massachusetts General Hospital
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.mindinformatics.ann.framework.module.encoding;

import org.codehaus.groovy.grails.web.json.JSONObject;

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
public interface IOpenAnnotationJsonDecoderService {

	/**
	 * Returns a JSON representation of the annotation contained
	 * by the specified graph.
	 * @param graph	The URI of the graph to decode
	 * @return	The JSON content
	 */
	public JSONObject decode(org.openrdf.model.URI graph);
}
