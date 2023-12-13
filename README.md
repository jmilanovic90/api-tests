# api-tests

## Used Technologies

- [Java](https://www.java.com/)
- [JUnit 5](https://junit.org/junit5/)
- [AssertJ](http://joel-costigliola.github.io/assertj/)
- [Cucumber](https://docs.cucumber.io/)
- [Project Lombok](https://projectlombok.org/)
- [REST-Assured](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [Awaitility](https://github.com/awaitility/awaitility/wiki/Usage)
- [Logback](https://logback.qos.ch/)

## To run the tests locally

Install the environment with plugins, clone the project.

Run

```console
mvn clean compile
```

from terminal to generate openapi classes.

Right-click on a feature/Scenario and select Run.

If some problems appear, check Run
Configurations https://www.jetbrains.com/help/idea/running-cucumber-tests.html#cucumber-run-configuration

## Project usage

When pulling a new code from main branch, make sure you first execute

```console
mvn clean compile
```

to generate openApi classess in your target/generated-sources folder

Project is executed by running command in project folder:

```console
mvn clean verify
```

## Local Development Setup

### Download and install

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and installed as
  described [here](https://docs.oracle.com/en/java/javase/13/install/overview-jdk-installation.html)
- [Maven 3.8+](https://maven.apache.org/download.cgi) and installed as
  described [here](https://maven.apache.org/install.html)
- IDE of choice, [IntelliJ IDEA](https://www.jetbrains.com/idea/download)
  or [Eclipse](https://www.eclipse.org/downloads/)
- [Lombok](https://projectlombok.org/download) and configured on chosen
  IDE, [IntelliJ IDEA](https://projectlombok.org/setup/intellij) or [Eclipse](https://projectlombok.org/setup/eclipse)
- Cucumber plug-ins for chosen
  IDE, [IntelliJ IDEA Cucumber for Java plug-in](https://plugins.jetbrains.com/plugin/7212-cucumber-for-java)
  or [Cucumber Eclipse plug-in](https://cucumber.github.io/cucumber-eclipse/)
    - More information about Cucumber Plug-ins usage
        - [IntelliJ IDEA Cucumber for Java plug-in](https://www.jetbrains.com/help/idea/cucumber-support.html)
        - [Cucumber Eclipse plug-in](https://github.com/cucumber/cucumber-eclipse/blob/master/README.md)
- [IntelliJ IDEA Save Actions plug-in](https://plugins.jetbrains.com/plugin/7642-save-actions) to apply code formatting
  on save action (this is not needed for Eclipse as it comes built-in)
- SonarLint plug-in for chosen
  IDE [IntelliJ IDEA SonarLint plug-in](https://plugins.jetbrains.com/plugin/7973-sonarlint)
  or [Eclipse SonarLint plug-in](https://marketplace.eclipse.org/content/sonarlint)

### Maven Settings

		<?xml version="1.0" encoding="UTF-8"?>
		<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

			    <profiles>
                    <profile>
                        <id>alwaysActiveProfile</id>
                        <repositories>
                            <repository>
                                <id>spring-milestones</id>
                                <name>Spring Milestones</name>
                                <url>http://repo.spring.io/milestone</url>
                                <snapshots>
                                    <enabled>false</enabled>
                                </snapshots>
                            </repository>
                        </repositories>
                    </profile>
                </profiles>
		    <activeProfiles>
                <activeProfile>alwaysActiveProfile</activeProfile>
            </activeProfiles>
        </settings>

### REST API Interface

To generate new service openapi classes, create new .yaml file with openapi spec in src/main/resources/clients.

Go to pom.xml, plugins > executions and add new execution by copying an existing one and changing inputSpec (your new
openapi.yaml) and modelPackage (pacakge name in generated-sources)

Do

```console
mvn clean compile
```

and classes are ready to use.

