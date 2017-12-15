# Exception handling for lagom streamed services


### Lagom Version (1.2.x / 1.3.x / etc)
1.4.0-M2 and 1.4.0-M3


### API (Scala / Java / Neither / Both)
Scala


### Operating System (Ubuntu 15.10 / MacOS 10.10 / Windows 10)
`Darwin Benoit-Montuelles-MacBook-Pro.local 17.2.0 Darwin Kernel Version 17.2.0: Fri Sep 29 18:27:05 PDT 2017; root:xnu-4570.20.62~3/RELEASE_X86_64 x86_64`

### JDK (Oracle 1.8.0_112, OpenJDK 1.8.x, Azul Zing)
```
java version "1.8.0_111"
Java(TM) SE Runtime Environment (build 1.8.0_111-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.111-b14, mixed mode)
```

### Library Dependencies
```
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
```

### Expected Behavior

Please describe the expected behavior of the issue, starting from the first action.

1. User perform a request with an invalid parameter
2. Service should fail in such case
3. Client implementation can the exception

### Actual Behavior

Please provide a description of what actually happens, working from the same starting point.

Be descriptive: "it doesn't work" does not describe what the behavior actually is -- instead, say "the page renders a 500 error code with no body content."  Copy and paste logs, and include any URLs.

1. User perform a request with an invalid parameter
2. Service do not fail and return a Source anyway
3. Client fails to handle the stream 

### Reproducible Test Case

An example project shoing this issue is https://github.com/bmontuelle/LagomTest/tree/stream_service_exception 

This project is simplified to narrow down the issue we encountered on isolation

To reproduce the issue run 
```
$ sbt test
```