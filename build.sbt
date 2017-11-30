import Dependencies._

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint", "-deprecation")

scalaVersion := "2.12.4"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.andyczerwonka",
      scalaVersion := "2.12.3",
      version      := "0.1.0-SNAPSHOT",
      retrieveManaged := true,
      libraryDependencies += "com.amazonaws" % "aws-lambda-java-core" % "1.1.0",
      libraryDependencies += "com.amazonaws" % "aws-lambda-java-events" % "2.0",
      libraryDependencies += "io.circe" %% "circe-core" % "0.8.0",
      libraryDependencies += "io.circe" %% "circe-generic" % "0.8.0",
      libraryDependencies += "io.circe" %% "circe-parser" % "0.8.0",
      libraryDependencies += "com.softwaremill.sttp" %% "core" % "0.0.14"
    )),

    name := "jira-discord-lambda",

    libraryDependencies += scalaTest % Test,

    assemblyMergeStrategy in assembly :=
      {
        case PathList("META-INF", xs @ _*) => MergeStrategy.discard
        case x => MergeStrategy.first
      }

  )
