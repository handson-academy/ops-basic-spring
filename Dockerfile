FROM openjdk:11
COPY target/basic*.jar /usr/src/basic.jar
COPY src/main/resources/application.properties /opt/conf/application.properties
CMD ["java", "-jar", "/usr/src/basic.jar", "--spring.config.location=file:/opt/conf/application.properties"]
