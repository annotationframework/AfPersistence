/**
 * Configuration for plugin testing - will not be included in the plugin zip
 * nor in the project war file
 */
log4j = {
	
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
}

// Spring Security Configuration
// The following have been listed as this plugin depends on the AfSecurity plugin that needs to be configured.
// -------------------------------------------------------------------------------------------------------------------------------------------
grails.plugin.springsecurity.userLookup.userDomainClassName 			= 'org.mindinformatics.ann.framework.module.security.users.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName 			= 'org.mindinformatics.ann.framework.module.security.users.UserRole'
grails.plugin.springsecurity.authority.className 						= 'org.mindinformatics.ann.framework.module.security.users.Role'

grails.plugin.springsecurity.rememberMe.persistent 						= true
grails.plugin.springsecurity.rememberMe.persistentToken.domainClassName = 'org.mindinformatics.ann.framework.module.security.PersistentLogin'
grails.plugin.springsecurity.openid.domainClass 						= 'org.mindinformatics.ann.framework.module.security.OpenID'

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	'/info'				: ['permitAll'],
	'/main/**'				: ['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_USER']
]
// -------------------------------------------------------------------------------------------------------------------------------------------

grails.views.default.codec	= "none" // none, html, base64
grails.views.gsp.encoding	= "UTF-8"
