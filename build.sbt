name := "FileWatcherScala"

version := "0.1"

scalaVersion := "2.13.7"

val typesafeConfigVersion = "1.4.1"
val AkkaVersion = "2.6.17"
val kafkaVersion = "2.8.0"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe" % "config" % typesafeConfigVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion