.PHONY: build test run clean install help

help:
	@echo "Available commands:"
	@echo "  make build    - Compile the whole project"
	@echo "  make test     - Run tests"
	@echo "  make install  - Install all modules into local Maven repository"
	@echo "  make run      - Launch the JavaFX application"
	@echo "  make clean    - Clean build artifacts"

build:
	mvn clean compile

test:
	mvn clean test

install:
	mvn clean install

run:
	mvn -f dictionary-editor-fx/pom.xml javafx:run

clean:
	mvn clean
