## Additional Instructions to run

### The Module

The API was intialized with Spring Initialzr as a module as opposed to a project, so when opening the code in an IDE or trying to compile it with Maven, make sure to open "../Smithsonian_API_Challenge/smithsonian-api-challenge/" as root as opposed to "../Smithsonian_API_Challenge/".
From there you can either run it from the terminal using:

```
./mvnw spring-boot:run
```

To compile your own Jar file from the module run:

```
./mvnw clean package
```

After building the Jar should be located in /smithsonian-api-challenge/target/

### Compiled Jar File

The compiled Jar file exceeded the GitHub size limit, but it can be found [here](https://drive.google.com/open?id=16FqH4pBgFXzfSdqeBiLkdSL52VvXNmED)

To run the API, open the directory where the Jar file is located and input the following command:

```
java -jar ShowsApi.jar
```
