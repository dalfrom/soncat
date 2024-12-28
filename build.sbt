ThisBuild / scalaVersion := "3.5.1"

ThisBuild / version := "1.0.0"

// ThisBuild / name := "Soncat"

ThisBuild / organization := "com.soncat"

lazy val core = (project in file("core"))
  .settings(
    name := "core",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "3.1.0",
      "com.lihaoyi" %% "os-lib" % "0.9.1",
      "org.scalameta" %% "munit" % "0.7.29" % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val server = (project in file("server"))
  .dependsOn(core)
  .settings(
    name := "server"
  )

lazy val root = (project in file("."))
  .aggregate(core, server)
  .settings(
    name := "root"
  )