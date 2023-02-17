ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scala-zio2-tag-partially-applied-types-workshop",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.9",
      "dev.zio" %% "izumi-reflect" % "2.2.5",
      "io.circe" %% "circe-core" % "0.14.4",
      "io.circe" %% "circe-parser" % "0.14.4",
      "io.circe" %% "circe-generic" % "0.14.4",
      "org.typelevel" %% "cats-core" % "2.9.0",
      "dev.zio" %% "zio-test" % "2.0.9" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
