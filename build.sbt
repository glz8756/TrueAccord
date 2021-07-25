name := "TrueAccordLZ"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies += "io.circe" %% "circe-core" % "0.13.0"
libraryDependencies += "io.circe" %% "circe-generic" % "0.13.0"
libraryDependencies += "io.circe" %% "circe-generic-extras" % "0.13.0"
libraryDependencies += "io.circe" %% "circe-parser" % "0.13.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.2" % Test


scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:_",
  "-unchecked",
  "-Wunused:_",
  "-Xfatal-warnings",
  "-Ymacro-annotations"
)