import _root_.pl.project13.scala.sbt.JmhPlugin

name := "sbtjmhsample"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "com.lmax" % "disruptor" % "3.3.2"

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.6.0-1"

enablePlugins(JmhPlugin)