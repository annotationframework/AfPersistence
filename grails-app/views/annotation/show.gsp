
<%@ page import="org.mindinformatics.ann.framework.module.persistence.Annotation" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'annotation.label', default: 'Annotation')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-annotation" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-annotation" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list annotation">
			
				<g:if test="${annotationInstance?.uri}">
				<li class="fieldcontain">
					<span id="uri-label" class="property-label"><g:message code="annotation.uri.label" default="Uri" /></span>
					
						<span class="property-value" aria-labelledby="uri-label"><g:fieldValue bean="${annotationInstance}" field="uri"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.media}">
				<li class="fieldcontain">
					<span id="media-label" class="property-label"><g:message code="annotation.media.label" default="Media" /></span>
					
						<span class="property-value" aria-labelledby="media-label"><g:fieldValue bean="${annotationInstance}" field="media"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.text}">
				<li class="fieldcontain">
					<span id="text-label" class="property-label"><g:message code="annotation.text.label" default="Text" /></span>
					
						<span class="property-value" aria-labelledby="text-label"><g:fieldValue bean="${annotationInstance}" field="text"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.quote}">
				<li class="fieldcontain">
					<span id="quote-label" class="property-label"><g:message code="annotation.quote.label" default="Quote" /></span>
					
						<span class="property-value" aria-labelledby="quote-label"><g:fieldValue bean="${annotationInstance}" field="quote"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.userid}">
				<li class="fieldcontain">
					<span id="userid-label" class="property-label"><g:message code="annotation.userid.label" default="Userid" /></span>
					
						<span class="property-value" aria-labelledby="userid-label"><g:fieldValue bean="${annotationInstance}" field="userid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.username}">
				<li class="fieldcontain">
					<span id="username-label" class="property-label"><g:message code="annotation.username.label" default="Username" /></span>
					
						<span class="property-value" aria-labelledby="username-label"><g:fieldValue bean="${annotationInstance}" field="username"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.source}">
				<li class="fieldcontain">
					<span id="source-label" class="property-label"><g:message code="annotation.source.label" default="Source" /></span>
					
						<span class="property-value" aria-labelledby="source-label"><g:fieldValue bean="${annotationInstance}" field="source"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.owner}">
				<li class="fieldcontain">
					<span id="owner-label" class="property-label"><g:message code="annotation.owner.label" default="Owner" /></span>
					
						<span class="property-value" aria-labelledby="owner-label"><g:link controller="user" action="show" id="${annotationInstance?.owner?.id}">${annotationInstance?.owner?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.json}">
				<li class="fieldcontain">
					<span id="json-label" class="property-label"><g:message code="annotation.json.label" default="Json" /></span>
					
						<span class="property-value" aria-labelledby="json-label"><g:fieldValue bean="${annotationInstance}" field="json"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.deleted}">
				<li class="fieldcontain">
					<span id="deleted-label" class="property-label"><g:message code="annotation.deleted.label" default="Deleted" /></span>
					
						<span class="property-value" aria-labelledby="deleted-label"><g:formatBoolean boolean="${annotationInstance?.deleted}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.archived}">
				<li class="fieldcontain">
					<span id="archived-label" class="property-label"><g:message code="annotation.archived.label" default="Archived" /></span>
					
						<span class="property-value" aria-labelledby="archived-label"><g:formatBoolean boolean="${annotationInstance?.archived}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.parent}">
				<li class="fieldcontain">
					<span id="parent-label" class="property-label"><g:message code="annotation.parent.label" default="Parent" /></span>
					
						<span class="property-value" aria-labelledby="parent-label"><g:link controller="annotation" action="show" id="${annotationInstance?.parent?.id}">${annotationInstance?.parent?.encodeAsHTML()}</g:link></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.tags}">
				<li class="fieldcontain">
					<span id="tags-label" class="property-label"><g:message code="annotation.tags.label" default="Tags" /></span>
					
						<g:each in="${annotationInstance.tags}" var="t">
						<span class="property-value" aria-labelledby="tags-label"><g:link controller="tag" action="show" id="${t.id}">${t?.encodeAsHTML()}</g:link></span>
						</g:each>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.dateCreated}">
				<li class="fieldcontain">
					<span id="dateCreated-label" class="property-label"><g:message code="annotation.dateCreated.label" default="Date Created" /></span>
					
						<span class="property-value" aria-labelledby="dateCreated-label"><g:formatDate date="${annotationInstance?.dateCreated}" /></span>
					
				</li>
				</g:if>
			
				<g:if test="${annotationInstance?.lastUpdated}">
				<li class="fieldcontain">
					<span id="lastUpdated-label" class="property-label"><g:message code="annotation.lastUpdated.label" default="Last Updated" /></span>
					
						<span class="property-value" aria-labelledby="lastUpdated-label"><g:formatDate date="${annotationInstance?.lastUpdated}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${annotationInstance?.id}" />
					<g:link class="edit" action="edit" id="${annotationInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
