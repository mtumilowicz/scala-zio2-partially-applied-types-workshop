ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "scala-zio2-tag-partially-applied-types",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.0.9",
      "dev.zio" %% "zio-test" % "2.0.9" % Test
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
