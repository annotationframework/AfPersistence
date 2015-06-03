<%@ page import="org.mindinformatics.ann.framework.module.persistence.Annotation" %>



<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'media', 'error')} ">
	<label for="media">
		<g:message code="annotation.media.label" default="Media" />
		
	</label>
	<g:textField name="media" value="${annotationInstance?.media}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'text', 'error')} ">
	<label for="text">
		<g:message code="annotation.text.label" default="Text" />
		
	</label>
	<g:textField name="text" value="${annotationInstance?.text}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'quote', 'error')} ">
	<label for="quote">
		<g:message code="annotation.quote.label" default="Quote" />
		
	</label>
	<g:textField name="quote" value="${annotationInstance?.quote}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'userid', 'error')} ">
	<label for="userid">
		<g:message code="annotation.userid.label" default="Userid" />
		
	</label>
	<g:textField name="userid" value="${annotationInstance?.userid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'username', 'error')} ">
	<label for="username">
		<g:message code="annotation.username.label" default="Username" />
		
	</label>
	<g:textField name="username" value="${annotationInstance?.username}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'source', 'error')} ">
	<label for="source">
		<g:message code="annotation.source.label" default="Source" />
		
	</label>
	<g:textField name="source" value="${annotationInstance?.source}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'owner', 'error')} ">
	<label for="owner">
		<g:message code="annotation.owner.label" default="Owner" />
		
	</label>
	<g:select id="owner" name="owner.id" from="${org.mindinformatics.ann.framework.module.security.users.User.list()}" optionKey="id" value="${annotationInstance?.owner?.id}" class="many-to-one" noSelection="['null': '']"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'json', 'error')} ">
	<label for="json">
		<g:message code="annotation.json.label" default="Json" />
		
	</label>
	<g:textField name="json" value="${annotationInstance?.json}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'deleted', 'error')} ">
	<label for="deleted">
		<g:message code="annotation.deleted.label" default="Deleted" />
		
	</label>
	<g:checkBox name="deleted" value="${annotationInstance?.deleted}" />
</div>

<div class="fieldcontain ${hasErrors(bean: annotationInstance, field: 'archived', 'error')} ">
	<label for="archived">
		<g:message code="annotation.archived.label" default="Archived" />
		
	</label>
	<g:checkBox name="archived" value="${annotationInstance?.archived}" />
</div>

