# Soncat - logging interface

To compile, run `sbt` and then `compile`, or `sbt compile` (or `~compile` inside a sbt definition)

To run, enter either `core` or `server` and run `runMain com.soncat.Main`

Move into the root of the project and run those commands:
```bash
sbt reload
sbt clean compile
sbt
project core
run
```

The the socket will start on port 3108