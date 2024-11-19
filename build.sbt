ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.18"

lazy val root = (project in file("."))
  .settings(
    name := "IG5_FP_Project",
    libraryDependencies ++= Seq(
  "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.1",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "neo4j-contrib" % "neo4j-spark-connector" % "4.0.1",
  "org.apache.spark" %% "spark-core" % "3.1.1",
  "org.apache.spark" %% "spark-sql" % "3.1.1",
  "org.slf4j" % "slf4j-simple" % "1.7.30"
)
  )
