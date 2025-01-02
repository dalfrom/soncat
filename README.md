# Soncat - logging interface

To compile, run `sbt` and then `compile`, or `sbt compile` (or `~compile` inside a sbt definition)

Move into the root of the project and run those commands:
```bash
sbt reload
sbt clean compile
sbt
project core
run # To run
test # To test
```

The the socket will start on port 3108

## Custom starting parameters

You can run the core applying custom parameters for your need. Currently 4 parameters are supported and 2 are working in the core:
- Port: By applying `--port=PORT` you can specify a different port number to start your Sonact server on. This will override any configuration;
- Config path: Using `--config-path=CUSTOM_PATH` will allow you to use specific configuration files. The path must be a string, starting with "/", "./", or "~/";
- Verbosity: `--verbose` allows for a very generic logging in console of what is going on;
- Debug: `--debug` allows for an in-depth logging of the processes of Soncat's core.

An example to this could be `run --port=10 --debug --config-path="./configurations/local.json"`. Only json configuration are supported


## Issues and development

[Linear](https://linear.app/) is used for development and issues; it contains a dedicated team for the Soncat repo, so all the commits will reference the Soncat prefix (SNC) followed by the issue incremental number (i.e. SNC-4)