gradle-jcasgen-plugin
=====================

Gradle plugin for UIMA JCasGen (JCas Typesystem Generator)

## Overview
Generate UIMA (http://uima.apache.org) type system from an XML descriptor.
It launch UIMA JCasGen tool (Jg) from gradle build.

## Configuration
Configure the plugin in your project as follows:
```groovy
buildscript {
    repositories {
        maven { url 'http://repository-drazzib.forge.cloudbees.com/snapshot/' }
    }
    dependencies {
        classpath 'com.drazzib.gradle.jcasgen.plugin:gradle-jcasgen-plugin:0.1-SNAPSHOT'
    }
}

// ...

apply plugin: 'jcasgen'
```

## Usage
UIMA type system XML descriptor(s) files have to been in :
```
src/main/typesystem
```

You can generate UIMA type system using generateTypeSystem directly or using build task:
```
$ gradle generateTypeSystem
$ gradle build
```

## Build
If you want to build this plugin from a Git checkout, please use Gradle Wrapper :
```
./gradlew clean build install
```

You can check latest Continuous Integration status on CloudBees :
https://drazzib.ci.cloudbees.com/job/gradle-jcasgen-plugin/
