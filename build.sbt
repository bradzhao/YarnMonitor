import _root_.sbt.Keys
import _root_.sbt.Keys._
import com.typesafe.sbt.packager.MappingsHelper._
import sbt._
import Keys._

import com.typesafe.sbt.SbtNativePackager.autoImport._

name := "yarnmonitor"

version := "0.0.1"

scalaVersion := "2.11.7"

resolvers ++= Seq(
  "DefaultMavenRepository" at "http://mvnrepository.com/artifact/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Cloudera Releases" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Pentaho Releases" at "http://repository.pentaho.org/artifactory/repo/",
  "Spring Releases" at "http://repo.spring.io/libs-release/",
  "spray repo" at "http://repo.spray.io",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "javax.mail" % "javax.mail-api" % "1.5.5",
  "com.sun.mail" % "javax.mail" % "1.5.5",
  "io.spray" %% "spray-json" % "1.3.2",
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.slf4j" % "slf4j-nop" % "1.7.14",
  "org.slf4j" % "slf4j-api" % "1.7.14",
  "org.slf4j" % "slf4j-log4j12" % "1.7.14",
  "org.apache.logging.log4j" % "log4j-api" % "2.5",
  "org.apache.logging.log4j" % "log4j-core" % "2.5",
  "com.h2database" % "h2" % "1.4.187",
  "org.quartz-scheduler" % "quartz" % "2.2.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.6" % Test,
  "junit" % "junit" % "4.12" % Test
)

mainClass in Compile := Some("play.core.server.NettyServer")

enablePlugins(JavaServerAppPackaging, PlayScala)

javaOptions in Universal ++= Seq(
  // JVM memory tuning
  "-J-Xmx1024m",
  "-J-Xms512m",
  "-Dhttp.port=9093",

  // Since play uses separate pidfile we have to provide it with a proper path
  s"-Dpidfile.path=/var/run/${packageName.value}/play.pid",

//  // Use separate configuration file for production environment
//  s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf",
//
//  // Use separate logger configuration file for production environment
//  s"-Dlogger.file=/usr/share/${packageName.value}/conf/application-logger.xml",

  // You may also want to include this setting if you use play evolutions
  "-DapplyEvolutions.default=true"
)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

rpmRelease := "0.0.1"

rpmVendor := "analyticservice"

rpmUrl := Some("http://www.zhaoyueblog.com")

rpmLicense := Some("CPL")

rpmChangelogFile := Some("changelog.txt")

routesGenerator := InjectedRoutesGenerator

mappings in Universal <+= (packageBin in Compile, baseDirectory) map { (_, base) =>
  val log = base / "conf" / "application-logger.xml"
  log -> "conf/application-logger.xml"
}

mappings in Universal <+= (packageBin in Compile, baseDirectory) map { (_, base) =>
  val app_conf = base / "conf" / "application.conf"
  app_conf -> "conf/yarnmonitor.conf"
}


mappings in Universal <+= (packageBin in Compile, baseDirectory) map { (_, base) =>
  val conf_d = base / "conf" / "yarnmonitor-default.conf"
  conf_d -> "conf/yarnmonitor-default.conf"
}


mappings in Universal <+= (packageBin in Compile, baseDirectory) map { (_, base) =>
  val routes = base / "conf" / "routes"
  routes -> "conf/routes"
}
mappings in Universal ++= directory("public")