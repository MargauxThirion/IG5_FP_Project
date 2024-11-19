ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.15"

lazy val root = (project in file("."))
  .settings(
    name := "IG5_FP_Project",
    libraryDependencies ++= Seq(
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.1",
      "com.typesafe.play" %% "play-json" % "2.9.2",
      "org.slf4j" % "slf4j-simple" % "1.7.30"
    )
  )
