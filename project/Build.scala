import sbt._, Keys._
import sbtrelease._
import sbtrelease.ReleasePlugin.autoImport._
import ReleaseStateTransformations._
import com.typesafe.sbt.pgp.PgpKeys
import xerial.sbt.Sonatype.SonatypeKeys

// port from https://github.com/scalaprops/sbt-scalaprops
// license: MIT
// author: Kenji Yoshida <https://github.com/xuwei-k>
object build extends Build {

  private[this] def gitHash = scala.util.Try(
    sys.process.Process("git rev-parse HEAD").lines_!.head
  ).getOrElse("master")

  lazy val sbtAutodoc = Project("sbt-dog-autodoc", file(".")).settings(
    xerial.sbt.Sonatype.sonatypeRootSettings ++ ScriptedPlugin.scriptedSettings
  ).settings(
    sbtPlugin := true,
    ScriptedPlugin.scriptedBufferLog := false,
    ScriptedPlugin.scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
      a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
    ),
    ScriptedPlugin.scriptedLaunchOpts ++= Seq(
      "-Dplugin.version=" + version.value,
      "-Dautodoc.version=0.2.0"
    ),
    startYear := Some(2015),
    organization := "com.github.pocketberserker",
    name := "sbt-dog-autodoc",
    description := "sbt plugin for dog-autodoc",
    homepage := Some(url("https://github.com/scala-kennel/sbt-dog-autodoc")),
    licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php")),
    pomPostProcess := { node =>
      import scala.xml._
      import scala.xml.transform._
      def stripIf(f: Node => Boolean) = new RewriteRule {
        override def transform(n: Node) =
          if (f(n)) NodeSeq.Empty else n
      }
      val stripTestScope = stripIf { n => n.label == "dependency" && (n \ "scope").text == "test" }
      new RuleTransformer(stripTestScope).transform(node)(0)
    },
    pomExtra := (
      <developers>
        <developer>
          <id>pocketberserker</id>
          <name>Yuki Nakayama</name>
          <url>https://github.com/pocketberserker</url>
        </developer>
      </developers>
      <scm>
        <url>git@github.com:scala-kennel/sbt-dog-autodoc.git</url>
        <connection>scm:git:git@github.com:scala-kennel/sbt-dog-autodoc.git</connection>
        <tag>{if(isSnapshot.value) gitHash else { "v" + version.value }}</tag>
      </scm>
    ),
    scalacOptions ++= (
      "-deprecation" ::
      "-unchecked" ::
      "-Xlint" ::
      "-language:existentials" ::
      "-language:higherKinds" ::
      "-language:implicitConversions" ::
      Nil
    ),
    fullResolvers ~= {_.filterNot(_.name == "jcenter")},
    ReleasePlugin.autoImport.releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      UpdateReadme.updateReadmeProcess,
      tagRelease,
      ReleaseStep(
        action = { state =>
          val extracted = Project extract state
          extracted.runAggregated(PgpKeys.publishSigned in Global in extracted.get(thisProjectRef), state)
        },
        enableCrossBuild = true
      ),
      setNextVersion,
      commitNextVersion,
      UpdateReadme.updateReadmeProcess,
      pushChanges
    ),
    credentials ++= PartialFunction.condOpt(sys.env.get("SONATYPE_USER") -> sys.env.get("SONATYPE_PASSWORD")){
      case (Some(user), Some(password)) =>
        Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", user, password)
    }.toList
  )
}
