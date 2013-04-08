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
        maven { url 'http://repository-drazzib.forge.cloudbees.com/release/' }
    }
    dependencies {
        classpath 'org.springframework.build.gradle:propdeps-plugin:0.0.1'
    }
}

// ...

apply plugin: 'jcasgen'
```

## Usage
UIMA type system XML descriptor(s) have to been in :
```
src/main/typesystem
```

You can generate UIMA type system using generateTypeSystem directly or using build task:
```
$ gradle generateTypeSystem
$ gradle build
```
