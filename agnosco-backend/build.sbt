name := "agnosco-backend"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.1.1"
libraryDependencies += "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
libraryDependencies += "org.glassfish.jersey.containers" % "jersey-container-servlet" % "2.28"
libraryDependencies += "org.glassfish.jersey.containers" % "jersey-container-grizzly2-http" % "2.28"
libraryDependencies += "org.glassfish.jersey.inject" % "jersey-hk2" % "2.26-b04"
libraryDependencies += "org.glassfish.jersey.core" % "jersey-client" % "2.26"
libraryDependencies += "javax.servlet" % "javax.servlet-api" % "4.0.1"