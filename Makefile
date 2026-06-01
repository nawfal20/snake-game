# Makefile pour Snake Game Pro

JAVAC = javac
JAVA = java
JAR = jar
BIN_DIR = bin
LIB_DIR = lib
JAR_NAME = SnakeGame.jar

.PHONY: all compile run jar clean

all: compile

compile:
	@if not exist $(BIN_DIR) mkdir $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -cp "$(LIB_DIR)/*" src/Main.java src/controller/*.java src/database/*.java src/model/*.java src/utils/*.java src/view/*.java

run: compile
	$(JAVA) -cp "$(BIN_DIR);$(LIB_DIR)/*" Main

jar: compile
	@echo Manifest-Version: 1.0 > manifest.txt
	@echo Main-Class: Main >> manifest.txt
	@echo Class-Path: lib/mysql-connector-j-8.4.0.jar >> manifest.txt
	@echo. >> manifest.txt
	$(JAR) cfm $(JAR_NAME) manifest.txt -C $(BIN_DIR) .
	@del manifest.txt
	@echo Fichier executable JAR cree avec succes: $(JAR_NAME)

clean:
	@if exist $(BIN_DIR) rmdir /s /q $(BIN_DIR)
	@if exist $(JAR_NAME) del $(JAR_NAME)
