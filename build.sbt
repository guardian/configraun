import sbtrelease.ReleaseStateTransformations._

name:= "configraun"
organization := "com.gu"
scalaVersion := "2.12.4"
scalacOptions ++= Seq("-feature", "-deprecation", "-unchecked", "-target:jvm-1.8", "-Xfatal-warnings", "-Ypartial-unification")
scalacOptions in doc in Compile := Nil

pomExtra := (
  <url>https://github.com/guardian/configraun</url>
    <developers>
      <developer>
        <id>LATaylor-guardian</id>
        <name>Luke Taylor</name>
        <url>https://github.com/LATaylor-guardian</url>
      </developer>
    </developers>
  )

publishArtifact in Test := false
releasePublishArtifactsAction := PgpKeys.publishSigned.value
organization := "com.gu"
licenses := Seq("Apache v2" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
scmInfo := Some(ScmInfo(
  url("https://github.com/guardian/configraun"),
  "scm:git:git@github.com:guardian/configraun.git"
))

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

releaseProcess := Seq(
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeReleaseAll"),
  pushChanges
)

resolvers += "Guardian GitHub Repository" at "http://guardian.github.io/maven/repo-releases"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.0.1",
  "com.amazonaws" % "aws-java-sdk-ssm" % "1.11.226"
)

initialize := {
  val _ = initialize.value
  assert(sys.props("java.specification.version") == "1.8",
    "Java 8 is required for this project.")
}