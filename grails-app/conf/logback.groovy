import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.util.FileSize

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

def HOME_DIR="/tmp/"

// See https://examples.javacodegeeks.com/enterprise-java/logback/logback-additivity-example/ for details on configuration
// https://logback.qos.ch/manual/architecture.html

appender('STDOUT', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    charset = Charset.forName('UTF-8')

    if (Environment.isDevelopmentMode() || Environment.current == Environment.TEST)
    {
      pattern =  '%clr(%d{dd.MM.yyyy HH:mm:ss.SSS}){faint} %clr(%5p) %logger %m%n%wex' // Message
    }
    else
    {
      pattern =
        '%clr(%d{dd.MM.yyyy HH:mm:ss.SSS}){faint} ' + // Date
          '%clr(%5p) ' + // Log level
          //'%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
          //'%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
          //'%logger ' + // Logger
          '%m%n%wex' // Message
    }
  }
}

appender("ROLLING", RollingFileAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%clr(%d{dd.MM.yyyy HH:mm:ss.SSS}) %level %logger - %msg%xThrowable%n"
  }
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "${HOME_DIR}${grails.util.Environment.current.name}-projectname-%d{yyyy-MM-dd}.log"
    maxHistory = 30
    totalSizeCap = FileSize.valueOf("2GB")
  }
}

appender("FULL_STACKTRACE", RollingFileAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%clr(%d{dd.MM.yyyy HH:mm:ss.SSS}) %level %logger - %msg%n"
  }
  rollingPolicy(TimeBasedRollingPolicy) {
    fileNamePattern = "${HOME_DIR}${grails.util.Environment.current.name}-stacktrace-%d{yyyy-MM-dd}.log"
    maxHistory = 30
    totalSizeCap = FileSize.valueOf("2GB")
  }
}

logger("grails.plugin.springsecurity.web.access.intercept", ERROR)
logger("org.springframework.mock.web.MockServletContext", ERROR)
logger("org.apache.tomcat.jdbc.pool.PooledConnection", ERROR)

//org.hibernate.id.UUIDHexGenerator HHH000409: Using org.hibernate.id.UUIDHexGenerator which does not generate IETF RFC 4122 compliant UUID values; consider using org.hibernate.id.UUIDGenerator instead
logger("org.hibernate.id.UUIDHexGenerator", ERROR)
logger("org.hibernate.orm.deprecation", ERROR)

//org.springframework.beans.GenericTypeAwarePropertyDescriptor Invalid JavaBean property 'exceptionMappings' being accessed! Ambiguous write methods found next to actually used [public void grails.plugin.springsecurity.web.authentication.AjaxAwareAuthenticationFailureHandler.setExceptionMappings(java.util.List)]: [public void org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler.setExceptionMappings(java.util.Map)]
logger("org.springframework.beans.GenericTypeAwarePropertyDescriptor", ERROR)
// WARN grails.plugin.sentry.SentryGrailsPlugin Sentry disabled
logger("grails.plugin.sentry.SentryGrailsPlugin", ERROR)

if (Environment.current == Environment.TEST)
{
  logger("StackTrace", ERROR, ['STDOUT'], false)
  logger('grails.app.services', ERROR, ['STDOUT'], false)
  logger('grails.app.controllers', ERROR, ['STDOUT'], false)

  root(ERROR, ['STDOUT'])
}
else if (Environment.isDevelopmentMode()) {

  logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
  logger('grails.app.services', INFO, ['ROLLING'], false)
  logger('grails.app.controllers', INFO, ['ROLLING'], false)

  root(WARN, ['STDOUT'])
}
else
{
  logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
  root(WARN, ['STDOUT'])
}


