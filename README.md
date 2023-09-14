## Issue 13124 grails core



### Steps to run the project

```
./grailsw war
docker build -t logbacktomcat:1.0 .
docker run --rm -it -p 8080:8080   logbacktomcat:1.0 
``` 