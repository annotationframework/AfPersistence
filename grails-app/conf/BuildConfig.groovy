grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.plugin.location.'af-security' = '../../annotationframework/AfSecurity'

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
        excludes 'commons-codec'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility
    repositories {
        grailsCentral()
        mavenCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://repo.grails.org/grails/plugins"
		mavenRepo "http://repo1.maven.org/maven2/"
        mavenRepo "http://mavenrepo.fzi.de/semweb4j.org/repo/"
        //mavenRepo "http://repo.aduna-software.org/maven2/releases/"
        //mavenRepo "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases"
		
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
        runtime 'mysql:mysql-connector-java:5.1.32'

        // runtime 'mysql:mysql-connector-java:5.1.5'
		//compile "org.semweb4j:rdf2go.api:4.6.2"

        //compile 'commons-codec:commons-codec:1.5'
		compile ("org.apache.marmotta:sesame-tools-rio-jsonld:3.0.0-incubating")

        compile ("org.semweb4j:rdf2go.api:4.8.3") {
            excludes 'slf4j-api'
        }
        compile ("org.semweb4j:rdf2go.impl.sesame:4.8.3") {
            excludes 'slf4j-api'
            //excludes "sesame-runtime-osgi"
        }

        compile ("com.nimbusds:nimbus-jose-jwt:2.20")
        //compile ("com.googlecode.jsontoken:jsontoken:1.0")

		compile ("org.apache.jena:jena-core:2.11.0") {
			excludes 'slf4j-api', 'xercesImpl'
		}
		compile ("org.apache.jena:jena-arq:2.9.3") 
		
        //runtime "org.semweb4j:rdf2go.impl.sesame:4.8.2"
		//runtime "org.semweb4j:rdf2go.api:4.8.2"
		//compile "org.openrdf:openrdf-sesame-onejar-osgi:2.1.2"
		//compile "org.openrdf.sesame:sesame-query:2.7.2"

    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:2.2.1",
              ":rest-client-builder:1.0.3") {
            export = false
        }

	    compile ':spring-security-core:2.0-RC2'
	    compile ":spring-security-openid:2.0-RC2"
        compile ":build-test-data:2.0.9"

        // #21 Keep this here so commons-codec:1.3 does not get included
        compile (":functional-test:2.0.RC1") {
            excludes "commons-codec"
        }

    }
}
