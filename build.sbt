//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("full-clean", ";++clean;++Test/clean")
addCommandAlias("full-test", ";++clean;++Test/clean;++test")

Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala212 = "2.12.17"
val Scala213 = "2.13.8"
val Scala3 = "3.3.1"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "1.0"
ThisBuild / organization := "com.lorandszakacs"
ThisBuild / organizationName := "Loránd Szakács"
ThisBuild / homepage := Option(url("https://github.com/lorandszakacs/enclosure"))
ThisBuild / startYear := Some(2021)
ThisBuild / licenses := Seq(License.Apache2)
ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl = url("https://github.com/lorandszakacs/enclosure"),
    connection = "git@github.com:lorandszakacs/enclosure.git"
  )
)
ThisBuild / developers := List(
  // https://github.com/lorandszakacs
  tlGitHubDev("lorandszakacs", "Loránd Szakács")
)

ThisBuild / scalaVersion := Scala213
ThisBuild / crossScalaVersions := List(Scala3, Scala213, Scala212)

//required for binary compat checks
ThisBuild / tlVersionIntroduced := Map(
  "3" -> "0.1.2"
)

//=============================================================================
//================================= CI details ================================
//=============================================================================

val PrimaryJava = JavaSpec.temurin("8")
val LTSJava = JavaSpec.temurin("17")

ThisBuild / githubWorkflowJavaVersions := Seq(PrimaryJava, LTSJava)

ThisBuild / tlCiHeaderCheck := true
ThisBuild / tlCiScalafmtCheck := true
ThisBuild / tlCiScalafixCheck := false
ThisBuild / tlCiMimaBinaryIssueCheck := true

//=============================================================================
//============================== Project details ==============================
//=============================================================================
// format: off
val munitVersion      = "1.0.0-M6"      // https://github.com/scalameta/munit/releases
// format: on

lazy val root = tlCrossRootProject.aggregate(enclosure)

lazy val enclosure = crossProject(JVMPlatform, JSPlatform)
  .crossType(CrossType.Full)
  .in(file("enclosure"))
  .settings(
    name := "enclosure",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % munitVersion % Test
    )
  )
  .settings(macroSettings)
  .settings(scala3Flags)

lazy val macroSettings = Seq(
  libraryDependencies ++= {
    if (tlIsScala3.value)
      Nil
    else
      Seq("org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided)
  }
)

lazy val scala3Flags = Seq(
  scalacOptions := {
    if (tlIsScala3.value) {
      scalacOptions.value
        .filterNot(_.startsWith("-source:")) ++
        Seq(
          "-source:future",
          "-language:strictEquality",
          "-language:adhocExtensions" // required for extending tests without marking them "open" for Scala 2 compat
        )
    } else scalacOptions.value
  }
)
