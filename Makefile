setup:
	gradle wrapper --gradle-version 7.0

clean:
	./gradlew clean

build:
	./gradlew clean build

install:
	./gradlew installDist

start-dist:
	./build/install/app/bin/app

check-updates:
	./gradlew dependencyUpdates

lint:
	./gradlew checkstyleMain checkstyleTest

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

.PHONY: build