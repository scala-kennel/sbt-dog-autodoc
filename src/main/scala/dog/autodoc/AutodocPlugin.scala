package dog
package autodoc

import sbt._, Keys._

object AutodocPlugin extends AutoPlugin {

  object autoImport {
    val autodocVersion = SettingKey[String]("autodocVersion")
    val autodocOutputDirectory = SettingKey[String]("autodocOutputDirectory")
    val autodocClean = TaskKey[Unit]("autodocClean")
    val autodocEnable = SettingKey[Boolean]("autodocEnable")
    val autodocTrimNameRegex = SettingKey[String]("autodocTrimNameRegex")

    object Default {
      val outputDir = "doc"
      val enable = false
      val trim = "(Test|Spec)$"
    }

    val autodocSettings: Seq[Setting[_]] = Seq(
      testFrameworks += new TestFramework("dog.autodoc.AutodocFramework"),
      autodocOutputDirectory := Default.outputDir,
      autodocClean := {
        Task.deleteFile(baseDirectory.value, autodocOutputDirectory.value)
      },
      test <<= (test in Test) dependsOn (autodocClean),
      autodocEnable := Default.enable,
      autodocTrimNameRegex := Default.trim,
      testListeners ++= {
        if(autodocEnable.value) Seq(
          new dog.autodoc.AutodocListener(file(autodocOutputDirectory.value), autodocTrimNameRegex.value)
        )
        else Nil
      },
      libraryDependencies += "com.github.pocketberserker" %% "dog-autodoc" % autodocVersion.value % "test"
    )
  }

  object Task {

    def deleteFile(base: File, filename: String): Unit = IO.delete(base / filename)
  }
}
