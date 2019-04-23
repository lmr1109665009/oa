FROM frolvlad/alpine-oraclejdk8:latest
#设置时区
#设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
ADD service-buc-1.0.0-release.war app.war
#RUN bash -c 'touch /app.war'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n","-jar","/app.war"]
EXPOSE 8050
EXPOSE 4000