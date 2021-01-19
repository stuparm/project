FROM adoptopenjdk/openjdk11

ADD ./users/users.txt /users.txt
ADD ./target/*jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]