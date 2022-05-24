name := "nichoshop_backend"

version := "1.0"

scalaVersion := "2.12.8"

val scalatraVersion = "2.6.5"

val scalazVersion = "7.1.0"

val akkaVersion = "2.4.12"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Spy" at "http://files.couchbase.com/maven2/"

resolvers += "sbt-plugin-releases" at "http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases"

avroStringType := "String"
avroFieldVisibility := "private"
avroCreateSetters := false
avroGenerate := Seq(file(baseDirectory.value.absolutePath + "/src/main/generated"))

enablePlugins(JettyPlugin)
containerPort := 9090

javaOptions in Jetty ++= Seq(
  "-Xdebug",
  "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000"
)

//jetty(port=9090)

libraryDependencies ++= Seq(
  "com.zaxxer" % "HikariCP" % "2.2.5",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
//  "com.typesafe" %% "config" % "1.3.0",
  "com.typesafe.slick" %% "slick" % "2.1.0",
//  "com.typesafe.slick" %% "slick-codegen" % "2.1.0-RC3",
  "commons-lang" % "commons-lang" % "2.6",
  "org.apache.commons" % "commons-email" % "1.4",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.apache.httpcomponents" % "httpclient" % "4.3.4",
  "org.scala-lang" % "scala-reflect" % "2.11.7",
  "org.scalatra" %% "scalatra" % scalatraVersion,
  "org.scalatra" %% "scalatra-auth" % scalatraVersion,
  "org.scalatra" %% "scalatra-scalate" % scalatraVersion,
  "org.scalatra" %% "scalatra-specs2" % scalatraVersion % "test",
  "org.scalatra" %% "scalatra-json" % scalatraVersion,

//  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "net.databinder.dispatch" %% "dispatch-core" % "0.12.0",
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,

  "org.eclipse.jetty" % "jetty-webapp" % "9.2.9.v20150224" % "container;compile",
  "com.twilio.sdk" % "twilio-java-sdk" % "4.4.1",
  //  "org.scalaz" %% "scalaz-core" % scalazVersion,
  //  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  //  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.json4s" %% "json4s-jackson" % "3.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "mysql" % "mysql-connector-java" % "5.1.33",
  "javax.servlet" % "javax.servlet-api" % "3.0.1",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.hsqldb" % "hsqldb" % "2.3.2" % "test",
  "org.scalatra" %% "scalatra-swagger" % scalatraVersion,
//  "spy" % "spymemcached" % "2.8.4",
//  "com.bionicspirit" %% "shade" % "1.6.0", //exclude("spy", "spymemcached")
  "io.monix" %% "shade" % "1.10.0", //exclude("spy", "spymemcached")
  "org.apache.avro" % "avro" % "1.10.2",
  "com.duosecurity" % "duo-universal-sdk" % "1.1.3"
/*
,"io.spray" %% "spray-client" % "1.3.2"%"test"
,"io.spray" %% "spray-json" % "1.3.2"%"test"
,"io.spray" %% "spray-testkit" % "1.3.2"%"test"
,"org.specs2" %% "specs2" % "2.3.13"%"test"
*/
)

enablePlugins(FlywayPlugin)
flywayUrl := "jdbc:mysql://localhost:3307/nichoshop"
flywayUser := "root"
flywayPassword := "dev"
flywayLocations += "sql/migration"
//flywayUrl in Test := "jdbc:hsqldb:file:target/flyway_sample;shutdown=true"
//flywayUser in Test := "SA"
//flywayPassword in Test := ""

inThisBuild(
  List(
    scalaVersion := "2.12.8",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision
  )
)

lazy val nichoShopProject = project.settings(
  scalacOptions += " -Ywarn-unused-import -Ywarn-unused -Xlint:unused" // required by `RemoveUnused` rule
)
