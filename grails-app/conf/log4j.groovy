import org.apache.logging.log4j.Level

log4j = {
    // Define the log level for the root logger
    root {
        // Set the logging level (e.g., ERROR, WARN, INFO, DEBUG)
        level = Level.DEBUG
        appenders = ['console'] // Output to the console
    }

    // Console appender configuration
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%c{2} %m%n')
    }

    // Configure logging for specific packages
    error 'org.codehaus.groovy.grails.web.servlet',  // Controllers
          'org.codehaus.groovy.grails.web.pages',    // GSP
          'org.codehaus.groovy.grails.web.mapping',   // URL mapping
          'org.codehaus.groovy.grails.web.mapping.filter', // filters
          'org.codehaus.groovy.grails.web.mapping.interceptor', // interceptors
          'org.codehaus.groovy.grails.plugin',        // plugins
          'org.springframework',                        // Spring framework
          'org.hibernate'                             // Hibernate

    // Enable debug level logging for your application
    debug 'paddlee' // Change this to your package name to see debug logs
}


