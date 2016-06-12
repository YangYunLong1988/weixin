FROM daocloud.io/library/java
MAINTAINER Captain Dao support@daocloud.io
RUN mkdir -p /app
WORKDIR /app

ADD diana-console/target/diana-console-1.0.0-SNAPSHOT.jar app.jar
COPY docker-entrypoint.sh /usr/local/bin/
EXPOSE 8890
ENTRYPOINT [docker-entrypoint.sh]
CMD []