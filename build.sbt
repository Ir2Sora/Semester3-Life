name := """Semester3-Life"""

version := "1.0"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.6",
  "com.typesafe.akka" %% "akka-remote" % "2.3.6")

lazy val remote = inputKey[Unit]("Start remote akka server")

fullRunInputTask(remote, Compile, "ru.semester3.RemoteApplication")

fullRunInputTask(run, Compile, "ru.semester3.ApplicationMain")
