# UCA-Registration

# Overview
This project refactors the initial UCA Course Registration code. 
All original behaviors are preserved.

## Build/Run Instructions
Build the project and package it into an executable JAR:

mvn clean package -DskipTests

Normal run

Loads the CSV files from the current directory (creates them if missing):

java -jar target/course-registration-0.1.0.jar

Demo mode

Seeds the application with sample students and courses:

java -jar target/course-registration-0.1.0.jar --demo
