sbtEnvConfig
=========

sbtEnvConfig is an sbt plugin that takes a source configuration file, reads and resolves it using Typesafe's `config` library, and writes it (or a subpath of it) to a destination configuration file. It is useful to configure per-project environment variables that can be updated on each compile or as desired.

To use it, start by adding the following lines to your project's `project/plugins.sbt` file:

```scala
lazy val root = project.in( file(".") ).dependsOn( sbtEnvConfigPlugin )
lazy val sbtEnvConfigPlugin = uri("git://github.com/agiledigital/sbt-env-config")
```

Add the following lines to your `build.sbt` file (the defaults aren't very useful):

```scala
import au.com.agiledigital.EnvConfigPlugin._

lazy val root = (project in file(".")).enablePlugins(EnvConfigPlugin)

EnvConfigSettings.envSource := "path/to/source/env.conf"

EnvConfigSettings.envDest   := "path/to/destination/env.conf"

EnvConfigSettings.envPath   := ""

// This is how it hooks into your application.
compile in Compile := {
  EnvConfigSettings.envConfigTask.value
  println("Rewrote .conf files")
  (compile in Compile).value
}
```

Now, on every compile the plugin should process and resolve your source `.conf` file and write it to the location specified as your destination.
