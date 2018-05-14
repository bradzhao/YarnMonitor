logLevel := Level.Warn

resolvers ++= Seq(
  "DefaultMavenRepository" at "http://mvnrepository.com/artifact/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/",
  "Cloudera Releases" at "https://repository.cloudera.com/artifactory/cloudera-repos/",
  "Pentaho Releases" at "http://repository.pentaho.org/artifactory/repo/",
  "Spring Releases" at "http://repo.spring.io/libs-release/",
  "spray repo" at "http://repo.spray.io",
  "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
)

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0-RC1")