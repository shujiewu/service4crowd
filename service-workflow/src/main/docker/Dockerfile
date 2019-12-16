FROM livingobjects/jre8
VOLUME /tmp
ADD service-workflow-2.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
RUN cd /etc/
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]