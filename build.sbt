import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "andyczerwonka",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT",
      retrieveManaged := true,
      libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "2.0"
    )),
    name := "jira-discord-lambda",
    libraryDependencies += scalaTest % Test
  )
