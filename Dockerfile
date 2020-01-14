from openjdk:8
name mamutovrm1/rustem
add /MP_api/MP_api.jar /opt/MP_api/MP_api.jar
EXPOSE 9000
entrypoint ["java", "-jar", "/opt/MP_api/MP_api.jar"]
