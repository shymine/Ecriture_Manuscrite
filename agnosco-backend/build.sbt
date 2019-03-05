name := "agnosco-backend"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.1"
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"

libraryDependencies += "org.glassfish.hk2.external" % "javax.inject" % "2.5.0-b62"
libraryDependencies += "org.glassfish.grizzly" % "grizzly-http-server" % "2.4.4"
libraryDependencies += "org.glassfish.jersey.core" % "jersey-server" % "2.28"
libraryDependencies += "org.glassfish.jersey.containers" % "jersey-container-grizzly2-http" % "2.28"