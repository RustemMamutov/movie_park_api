from openjdk:11
add /MP_api.jar /opt/MP_api/MP_api.jar
entrypoint ["java", "-jar", "/opt/MP_api/MP_api.jar"]
