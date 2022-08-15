# movie-platform-app

# Requirements
For building and running the application you need:

JDK 16

Maven version v3.6.3 or higher version

MongoDB Server version v3.6.8 or higher version

# Running the application locally
IntelliJ IDEA

1.Open IntelliJ IDEA and select File > Open....or File > New > Project from Version Control...

2.Choose the movie-platform directory and click OK.

3.Select File > Project Structure... and ensure that the Project SDK are set to 16.

4.Shift+f10 Run MoviePlatformApplication

Ubuntu
1. git clone https://github.com/MherMg/movie-platform.git

2. cd /movie-platform/

3. mvn clean package

4. nohup java -jar ~/movie-platform/movie-platform-api/target/movie-platform-api-0.0.1-SNAPSHOT.jar &

# This is the link for API doc
 Local http://localhost:8080/swagger-ui.html#/ 

 Server http://84.201.131.118:8080/swagger-ui.html#/
