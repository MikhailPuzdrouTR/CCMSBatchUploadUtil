import sbt.Resolver

name := "CCMSBatchClient"
organization := "ThomsonReuters"
version := "0.1"
description := "UnifiedMetadataExtractor"


name := "CCMSBatchClient"

version := "0.1"

scalaVersion := "2.13.1"


lazy val UnifiedMetadataExtractor = Project(
  id = "UnifiedMetadataExtractor",
  base = file(".")
)

val akkaHttpVersion = "10.1.9"
val akkaVersion = "2.6.0-M5"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scala-lang" % "scala-library" % scalaVersion.value,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" %% "scala-xml" % "1.2.0",

  "org.apache.commons" % "commons-lang3" % "3.3.2",
  "commons-io" % "commons-io" % "2.6",

  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",

  "com.jcraft" % "jsch" % "0.1.55",

  "io.spray" %% "spray-json" % "1.3.5",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,

  // Cognito Login Helper for authorization in CCMS
  "com.thomsonreuters.tms.cognito" % "cognito-login-helper" % "1.0-SNAPSHOT",

  // Testing
  "org.scalatest" %% "scalatest" % "3.0.8" % Test,
  "org.scalamock" %% "scalamock" % "4.3.0" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
)

unmanagedJars in Compile := (baseDirectory.value ** "*.jar").classpath

mainClass in assembly := Some("com.tr.metadataextractor.view.ConsoleApp")

credentials += Credentials("Artifactory Realm", "bams-aws.refinitiv.com", "s.tms.bermuda", "Y8mA2vVRqb68y5P")
externalResolvers ++= Seq(
  "Artifactory BAMS AWS" at "https://bams-aws.refinitiv.com/artifactory/release.maven.global/",
  "Artifactory BAMS AWS SNAPSHOT " at "https://bams-aws.refinitiv.com/artifactory/snapshot.maven.global/",
  Resolver.jcenterRepo
)
