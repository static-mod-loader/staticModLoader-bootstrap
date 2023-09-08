all:
	./gradlew jarJar

test:
	./gradlew runClient

api:
	javac -h api/ api/StaticLoaderProtocol.java

.PHONY: all test api