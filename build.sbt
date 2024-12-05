ThisBuild / scalaVersion := "3.5.1"

ThisBuild / version := "1.0.0"

ThisBuild / name := "Soncat"

ThisBuild / organization := "com.soncat"

lazy val core = (project in file("core"))
lazy val server = (project in file("server")).dependsOn(core)

lazy val root = (project in file(".")).aggregate(core, server)
