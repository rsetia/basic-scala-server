name := "Service"
version := "1.0"
scalaVersion := "2.12.6"

libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
libraryDependencies += "net.debasishg" %% "redisreact" % "0.9"
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.6.4"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"

libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.3"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3"
libraryDependencies += "com.typesafe.slick" %% "slick-codegen" % "3.2.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.1.3"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.14"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.14" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.14"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test

libraryDependencies += "joda-time" % "joda-time" % "2.10"