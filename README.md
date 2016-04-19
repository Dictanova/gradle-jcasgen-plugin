gradle-jcasgen-plugin
=====================

[Gradle](http://www.gradle.org/) plugin for [UIMA](http://uima.apache.org) JCasGen (JCas Typesystem Generator)

[![Build Status](https://travis-ci.org/Dictanova/gradle-jcasgen-plugin.svg)](https://travis-ci.org/Dictanova/gradle-jcasgen-plugin)
[![Build Status](https://drone.io/github.com/Dictanova/gradle-jcasgen-plugin/status.png)](https://drone.io/github.com/Dictanova/gradle-jcasgen-plugin/latest)

## Overview
Generates a [Apache UIMA](http://uima.apache.org) type system from an XML descriptor.
It launches the UIMA JCasGen tool (Jg) from a gradle build.

## Configuration
You can configure the plugin in your project as follows:
```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.dictanova.jcasgen.gradle:gradle-jcasgen-plugin:0.4"
  }
}

apply plugin: "com.dictanova.jcasgen"
```

## Usage
UIMA type system XML descriptor(s) files have to be located in :
```
src/main/typesystem
```

You can generate a UIMA type system by using the `generateTypeSystem` task directly or by using the `build` task:
```
$ gradle generateTypeSystem
$ gradle build
```

## Build
If you want to build this plugin from a Git checkout, please use Gradle Wrapper :
```
./gradlew clean build install
```

