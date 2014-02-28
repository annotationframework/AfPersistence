dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
    show_sql = true
    format_sql = true
    use_sql_comments = true

}
// environment specific settings
environments {
    development {
        dataSource {

            //dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            //url = "jdbc:h2:mem:devDb;MVCC=TRUE"

            pooled = true
            //dbCreate = "create-drop"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            url = "jdbc:mysql://localhost:3306/catch?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true"
            username = "catch"
            password = "catch"


        }


    }
    test {
        dataSource {
            //dbCreate = "update"
            //url = "jdbc:h2:mem:testDb;MVCC=TRUE"

            pooled = true
            //dbCreate = "create-drop"
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            url = "jdbc:mysql://localhost:3306/catch?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true"
            username = "catch"
            password = "catch"


        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE"
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
