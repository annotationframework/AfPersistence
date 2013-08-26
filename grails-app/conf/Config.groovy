// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

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

// FIXME I do not think this needs to be added to each plugin Config.groovy, but I had to add it in order to keep from
// getting the following error when running tests
//  Error Error executing script TestApp: java.lang.NullPointerException: Cannot invoke method newInstance() on null object (NOTE: Stack trace has been filtered. Use --verbose to see entire trace.)
// java.lang.NullPointerException: Cannot invoke method newInstance() on null object
grails.plugins.springsecurity.userLookup.userDomainClassName = 'org.mindinformatics.ann.framework.module.security.users.User'
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.mindinformatics.ann.framework.module.security.users.UserRole'
grails.plugins.springsecurity.authority.className = 'org.mindinformatics.ann.framework.module.security.users.Role'
grails.plugins.springsecurity.rememberMe.persistent = true
grails.plugins.springsecurity.rememberMe.persistentToken.domainClassName = 'org.mindinformatics.ann.framework.module.security.PersistentLogin'
grails.plugins.springsecurity.openid.domainClass = 'org.mindinformatics.ann.framework.module.security.OpenID'