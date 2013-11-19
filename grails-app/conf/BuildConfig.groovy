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
    repositories {
        grailsCentral()
        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenCentral()
        //mavenLocal()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
		mavenRepo "http://repo.aduna-software.org/maven2/releases/"
		mavenRepo "http://repo1.maven.org/maven2/"
		mavenRepo "http://mavenrepo.fzi.de/semweb4j.org/repo/"
		//mavenRepo "https://repository.jboss.org/nexus/content/repositories/thirdparty-releases"
		
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
		//compile "org.semweb4j:rdf2go.api:4.6.2"

        //compile 'commons-codec:commons-codec:1.5'
		compile ("org.apache.marmotta:sesame-tools-rio-jsonld:3.0.0-incubating")
		compile ("org.semweb4j:rdf2go.impl.sesame:4.8.2")
		compile ("org.semweb4j:rdf2go.api:4.8.2")
        compile ("com.nimbusds:nimbus-jose-jwt:2.18")
        compile ("com.googlecode.jsontoken:jsontoken:1.0")
		
		compile ("org.apache.jena:jena-core:2.11.0") {
			excludes 'slf4j-api', 'xercesImpl'
		}
		compile ("org.apache.jena:jena-arq:2.9.3") 
		
        runtime "org.semweb4j:rdf2go.impl.sesame:4.8.2"
		runtime "org.semweb4j:rdf2go.api:4.8.2"
		compile "org.semweb4j:rdf2go.impl.base:4.6.2"
		//compile "org.openrdf:openrdf-sesame-onejar-osgi:2.1.2"
		//compile "org.openrdf.sesame:sesame-query:2.7.2"

    }

    plugins {
        build(":tomcat:$grailsVersion",
              ":release:1.0.0") {
            export = false
        }

	    compile ':spring-security-core:2.0-RC2'
	    compile ":spring-security-openid:2.0-RC2"

        //test ":build-test-data:2.0.5"
    }
}
