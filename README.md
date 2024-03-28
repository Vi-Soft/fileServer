![Build](https://github.com/Vi-Soft/fileServer/actions/workflows/java-maven.yml/badge.svg)
# fileServer

---
### Customizing Individually Adjustable Settings

You can customize __Master Password__ value by adding corresponding file along with application's *.jar file:

- for Master Password, it is `master-password.txt` file;

Note that these files are created automatically and contained default values which taken from application properties.

### Customizing logging setup

You can adjust log4j configuration to use another properties (i.e. Graylog host/port/environment name).
- Put your version of log4.properties file to the .jar file directory (you can use log4j.properties from the project as a reference)
- Add a command line parameter `-Dlog4j.configuration=file:log4j.properties` when you run .jar file

