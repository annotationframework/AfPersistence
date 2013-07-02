
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
package org.mindinformatics.ann.framework.module.persistence


import org.apache.commons.fileupload.FileUploadException
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.mindinformatics.ann.framework.module.security.users.User
import org.mindinformatics.ann.framework.modules.validation.OAValidationHandler;
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView

/**
 * @author Paolo Ciccarese <paolo.ciccarese@gmail.com>
 */
class UploadController {
	
	def springSecurityService
	
	/*
	 * Loading by primary key is usually more efficient because it takes
	 * advantage of Hibernate's first-level and second-level caches
	 */
	protected def injectUserProfile() {
		def principal = springSecurityService.principal
		if(principal.equals("anonymousUser")) {
			redirect(controller: "login", action: "index");
		} else {
			String userId = principal.id
			def user = User.findById(userId);
			if(user==null) {
				log.error "Error:User not found for id: " + userId
				render (view:'error', model:[message: "User not found for id: "+userId]);
			}
			user
		}
	}
	
	def annotationFile = {
		def loggedUser = injectUserProfile();
		if (request.post) {
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new IllegalArgumentException("Request is not multipart, " + 
					"please 'multipart/form-data' enctype for your form.");
			}

			ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
			PrintWriter writer = response.getWriter();
			response.setContentType("application/json");
			
			JSONObject jsonResponse = new JSONObject();
			
			try {
				long start = System.currentTimeMillis();
				MultipartFile item = request.getFile('annotation');
				println item.getContentType();
				println item.size;
				def okcontents = ['application/json']
				if (!FilenameUtils.getExtension(item.getOriginalFilename()).equals('json') &&
						!okcontents.contains(item.getContentType())) {
					log.error('file format not supported (only json accepted)');
					render status: 406, text: 'Format not accepted';
					return;
				}
									
				jsonResponse.put("name", item.getName());
				jsonResponse.put("size", item.getSize());
				
				String extension = FilenameUtils.getExtension(item.getOriginalFilename());
				log.info("Creating file " + request.getServletContext().getRealPath("/") +
					"uploads/users/"+loggedUser.id + "/" +
					"annotation-" +  start + "." + extension);
				
				File directory = new File(request.getServletContext().getRealPath("/") +
					"uploads/users/"+loggedUser.id + "/");
				if(directory.exists()){
					System.out.println("Directory Exists");
				}else{
					boolean wasDirecotyMade = directory.mkdirs();
					if(wasDirecotyMade)System.out.println("Directory Created");
					else System.out.println("Sorry could not create directory");
				}
				
				File file = new File(request.getServletContext().getRealPath("/") +
					"uploads/users/"+loggedUser.id + "/", "annotation-" +  start + "." + extension);
				log.info("Creating file " + file.getName());		
				item.transferTo(file);
				
				OAValidationHandler validator = new OAValidationHandler();
				ModelAndView mav = validator.validate(item.getInputStream(), "application/json")
				println 'done';
				
				
				JSONObject jsonSummary = new JSONObject();
				Object resultExternal = mav.getModel().get("result");
				println resultExternal;
				jsonSummary.put("total", resultExternal.get("total"));
				jsonSummary.put("warn", resultExternal.get("warn"));
				jsonSummary.put("error", resultExternal.get("error"));
				jsonSummary.put("skip", resultExternal.get("skip"));
				jsonSummary.put("pass", resultExternal.get("pass"));
				jsonResponse.put("summary", jsonSummary);
				
				JSONArray jsonResults = new JSONArray();
				Object resultInternal = resultExternal.get("result");
				for(Object result: resultInternal) {
					JSONObject jsonResult = new JSONObject();

					jsonResult.put("section", result.getAt("section"));
					jsonResult.put("warn", result.getAt("warn"));
					jsonResult.put("error", result.getAt("error"));
					jsonResult.put("skip", result.getAt("skip"));
					jsonResult.put("pass", result.getAt("pass"));
					jsonResult.put("total", result.getAt("total"));
					
					JSONArray jsonConstraints = new JSONArray();
					ArrayList constraints = result.getAt("constraints");
					for(Object constraint: constraints) {
						JSONObject jsonConstraint = new JSONObject();
						jsonConstraint.put("ref", constraint.getAt("ref"));
						jsonConstraint.put("url", constraint.getAt("url"));
						jsonConstraint.put("severity", constraint.getAt("severity"));
						jsonConstraint.put("status", constraint.getAt("status"));
						jsonConstraint.put("result", constraint.getAt("result"));
						jsonConstraint.put("description", constraint.get("description"));
						jsonConstraints.add(jsonConstraint);
						jsonResult.put("constraints", jsonConstraints);
					}
					jsonResults.add(jsonResult);
				}

		
				jsonResponse.put("results", jsonResults);
				
				
			} catch (FileUploadException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				writer.write(jsonResponse.toString());
				writer.close();
			}
		}
	}
}
