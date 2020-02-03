from openjdk:8
add /MP_api/MP_api.jar /opt/MP_api/MP_api.jar
EXPOSE 9000
entrypoint ["java", "-jar", "/opt/MP_api/MP_api.jar"]
