### Logging

The logs are automatically written under the `logs/` folder.

The SOS application uses the log4j logger and changing the logs configuration is as straightforward
as providing a new `log4j.properties` file. To explicitly instruct the SOS application to use a non-default properties file, you must
add this parameter when running the java app:

`-Dlog4j.configuration=file:/path/to/log4j.properties`

**Note** that the property value must be a valid URL.


#### Example of a log4j.properties for both file and stdout

```
log4j.rootLogger=file,console
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.file=org.apache.log4j.RollingFileAppender

# http://stackoverflow.com/a/4953207/2467938
# -Dlogfile.name={logfile}
log4j.appender.file.File=${logfile.name}
log4j.appender.file.ImmediateFlush=true
log4j.appender.file.Threshold=debug
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.MaxFileSize=10MB

log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{dd-MM-yyyy HH:mm:ss} [ %-5p ] -  %c %x ( %-4r [%t] ) ==> %m%n

log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{dd-MM-yyyy HH:mm:ss} [ %-5p ] -  %c %x ( %-4r [%t] ) ==> %m%n
```
