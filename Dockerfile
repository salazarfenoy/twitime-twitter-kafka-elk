FROM adoptopenjdk/openjdk11:x86_64-ubuntu-jdk-11.0.3_7

MAINTAINER Jesus

RUN mkdir -p /user/share/[artifactId]/static/songs

RUN mkdir -p /user/share/[artifactId]/bin

ADD /target/[artifactId]*SNAPSHOT.jar /user/share/cisapify/bin/[artifactId].jar

WORKDIR /user/share/[artifactId]

ENTRYPOINT ["/opt/java/openjdk/bin/java", "-jar", "bin/[artifactId].jar"]