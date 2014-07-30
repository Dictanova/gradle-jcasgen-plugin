gradle-jcasgen-plugin
=====================

[Gradle](http://www.gradle.org/) plugin for [UIMA](http://uima.apache.org) JCasGen (JCas Typesystem Generator)

[![Build Status](https://travis-ci.org/Dictanova/gradle-jcasgen-plugin.svg)](https://travis-ci.org/Dictanova/gradle-jcasgen-plugin)
[![Build Status](https://drone.io/github.com/Dictanova/gradle-jcasgen-plugin/status.png)](https://drone.io/github.com/Dictanova/gradle-jcasgen-plugin/latest)

## Overview
Generate [Apache UIMA](http://uima.apache.org) type system from an XML descriptor.
It launch UIMA JCasGen tool (Jg) from gradle build.

## Configuration
Configure the plugin in your project as follows:
```groovy
buildscript {
  repositories {
    jcenter()
  }
  dependencies {
    classpath "com.dictanova.jcasgen.gradle:gradle-jcasgen-plugin:0.2"
  }
}

apply plugin: "com.dictanova.jcasgen"
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

