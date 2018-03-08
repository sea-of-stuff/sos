## Useful tools and commands

- Online JSON Linter (and more) - https://jsoncompare.com/#!/simple/
- Online hex dump inspector - https://hexed.it/
- File to SHA values - https://md5file.com/calculator
- Hash Online - https://quickhash.com
- File leak detector - http://file-leak-detector.kohsuke.org/

- cloc - https://github.com/AlDanial/cloc
    - `cloc . --exclude-dir=datasets,contexts,plots,processed,remote,usro,configuration,target,logs,output`


- List of opened resources: `lsof -p pid`
- File leak detector (https://github.com/kohsuke/file-leak-detector)
    - Build the sos-app jar first with `mvn package -DskipTests`
    - `java -javaagent:third-party/file-leak-detector/file-leak-detector-1.10-jar-with-dependencies.jar=http=19999,strong -Djava.awt.headless=true -jar sos-app/target/sos-app.jar -c example_config.json -j`

