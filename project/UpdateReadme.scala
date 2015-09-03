import sbt._, Keys._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.Git

// copy from https://github.com/scalaprops/sbt-scalaprops
// license: MIT
// author: Kenji Yoshida <https://github.com/xuwei-k>
object UpdateReadme {

  val updateReadmeTask = { state: State =>
    val extracted = Project.extract(state)
    val scalaV = extracted get scalaBinaryVersion
    val sbtV = extracted get sbtBinaryVersion
    val v = extracted get version
    val org =  extracted get organization
    val n = extracted get name
    val snapshotOrRelease = if(extracted get isSnapshot) "snapshots" else "releases"
    val readme = "README.md"
    val readmeFile = file(readme)
    val newReadme = Predef.augmentString(IO.read(readmeFile)).lines.map{ line =>
      val matchReleaseOrSnapshot = line.contains("SNAPSHOT") == v.contains("SNAPSHOT")
      if(line.startsWith("addSbtPlugin") && matchReleaseOrSnapshot){
        s"""addSbtPlugin("${org}" % "${n}" % "$v")"""
      }else line
    }.mkString("", "\n", "\n")
    IO.write(readmeFile, newReadme)
    val git = new Git(extracted get baseDirectory)
    git.add(readme) ! state.log
    git.commit("update " + readme) ! state.log
    "git diff HEAD^" ! state.log
    state
  }

  val updateReadmeProcess: ReleaseStep = updateReadmeTask
}
