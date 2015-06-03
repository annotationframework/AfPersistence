
<%@ page import="org.mindinformatics.ann.framework.module.persistence.Annotation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'annotation.label', default: 'Annotation')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-annotation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-annotation" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="uri" title="${message(code: 'annotation.id.label', default: 'ID')}" />
					
						<g:sortableColumn property="media" title="${message(code: 'annotation.media.label', default: 'Media')}" />
					
						<g:sortableColumn property="text" title="${message(code: 'annotation.text.label', default: 'Text')}" />
					
						<g:sortableColumn property="quote" title="${message(code: 'annotation.quote.label', default: 'Quote')}" />
					
						<g:sortableColumn property="userid" title="${message(code: 'annotation.userid.label', default: 'Userid')}" />
					
						<g:sortableColumn property="username" title="${message(code: 'annotation.username.label', default: 'Username')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${annotationInstanceList}" status="i" var="annotationInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${annotationInstance.id}">${fieldValue(bean: annotationInstance, field: "id")}</g:link></td>
					
						<td>${fieldValue(bean: annotationInstance, field: "media")}</td>
					
						<td>${fieldValue(bean: annotationInstance, field: "text")}</td>
					
						<td>${fieldValue(bean: annotationInstance, field: "quote")}</td>
					
						<td>${fieldValue(bean: annotationInstance, field: "userid")}</td>
					
						<td>${fieldValue(bean: annotationInstance, field: "username")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${annotationInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
