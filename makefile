all:
	./gradlew jarJar

test:
	./gradlew runClient

api:
	javac -h api/ src/main/java/com/example/examplemod/StaticLoaderProtocol.java

.PHONY: all test api