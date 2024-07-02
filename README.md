# Overview

This library helps setting up and retaining [ThreadLocal](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html) [MDC](https://www.slf4j.org/api/org/slf4j/MDC.html) of Slf4J across various Java concurrency related classes like Executors, Callables, Runable, etc.

There is common need to add MDC and restore it as we make calls across threads and we have to repeat the code. This library provides easy to use apis for such cases.

# Usage

## Build Dependency 

Below are some of the common ones, more can be found [here](https://central.sonatype.com/artifact/com.rokoder.concurrency/context-preserved)

### Gradle

Kotlin

```kotlin
implementation("com.rokoder.concurrency:context-preserved-slf4j:1.0.0")
```

Groovy

```groovy
implementation group: 'com.rokoder.concurrency', name: 'context-preserved-slf4j', version: '1.0.0'
```

Groovy Short

```groovy
implementation 'com.rokoder.concurrency:context-preserved-slf4j:1.0.0'
```

### Maven

```xml
<dependency>
    <groupId>com.rokoder.concurrency-slf4j</groupId>
    <artifactId>context-preserved</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Basic

Below is basic example but you can read more in java doc of `Slf4jMdcPreservedFactory`

```java
MDC.put("test-key", "test-value");
// It will take snapshot of MDC just before calling submit and restore it post the submit call.
ExecutorService dynamicContextExecutorService = Slf4jMdcPreservedFactory.newDynamicContextExecutorServiceFrom(executorService);
dynamicContextExecutorService.submit(() -> "some code to run on separate thread");

// It will take the snapshot of MDC at the time of Executor creation and will be fixed for all submit calls.
// That means we will use "test-key" and "test-value" for all submit calls
ExecutorService fixedContextExecutor = Slf4jMdcPreservedFactory.newFixedContextExecutorFrom(executorService);
fixedContextExecutor.execute(() -> { // some code to run on separate thread
    });
```