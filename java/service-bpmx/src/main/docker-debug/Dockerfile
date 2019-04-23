FROM frolvlad/alpine-oraclejdk8:latest
VOLUME /tmp
ADD service-bpmx-1.0.0-release.war app.war
#RUN bash -c 'touch /app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n","-jar","/app.war"]
EXPOSE 8030
EXPOSE 4000