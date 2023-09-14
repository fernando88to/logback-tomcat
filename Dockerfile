FROM  tomcat:9.0.80-jdk11-temurin

COPY build/libs/logback-tomcat-0.1-plain.war $CATALINA_HOME/webapps/ROOT.war