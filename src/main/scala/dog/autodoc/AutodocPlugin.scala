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
    val autodocToc = SettingKey[Boolean]("autodocToc")
    val autodocInitializeToc = TaskKey[Unit]("autodocInitializeToc")

    object Default {
      val outputDir = "doc"
      val enable = false
      val trim = "(Test|Spec)$"
      val toc = false
    }

    val autodocSettings: Seq[Setting[_]] = Seq(
      testFrameworks += new TestFramework("dog.autodoc.AutodocFramework"),
      autodocOutputDirectory := Default.outputDir,
      autodocClean := {
        if(autodocEnable.value) Task.deleteFile(baseDirectory.value, autodocOutputDirectory.value)
      },
      autodocInitializeToc := {
        if(autodocEnable.value && autodocToc.value) {
          val outputDir = file(autodocOutputDirectory.value)
          if(!outputDir.exists()) IO.createDirectory(outputDir)
          val toc = outputDir / Markdown.tocFileName
          IO.write(toc, Markdown.tocTitle)
        }
      },
      test <<= (test in Test) dependsOn (autodocClean, autodocInitializeToc),
      autodocEnable := Default.enable,
      autodocTrimNameRegex := Default.trim,
      autodocToc := Default.toc,
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
