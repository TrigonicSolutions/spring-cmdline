# Spring Command Line

This library provides a Spring mechanism for launching a command-line
application with options and operands.

## Gradle Usage

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath 'com.trigonic:spring-cmdline:0.3'
    }

## Application Usage

The CommandLineAppContext defined in this library allows you to load an application bean within the context of a Spring
ApplicationContext and leverages Spring's rich type conversions and initialization semantics.

See the [sample application](https://github.com/AlanKrueger/spring-cmdline/blob/master/src/test/java/com/trigonic/utils/spring/cmdline/SampleApplication.java) for an example.

