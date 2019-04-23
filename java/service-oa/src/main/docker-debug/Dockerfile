FROM frolvlad/alpine-oraclejdk8:latest
#设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
VOLUME /tmp
ADD service-oa-1.0.0-release.jar app.jar
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Xdebug","-Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n","-jar","/app.jar"]
EXPOSE 8020
EXPOSE 4000