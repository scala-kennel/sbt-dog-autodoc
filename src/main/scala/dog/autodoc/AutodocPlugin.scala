package dog
package autodoc

import sbt._, Keys._

object AutodocPlugin extends AutoPlugin {

  object autoImport {
    val autodocVersion = SettingKey[String]("autodocVersion")
    val autodocOutputDirectory = SettingKey[String]("autodocOutputDirectory")
    val autodocEnable = SettingKey[Boolean]("autodocEnable")
    val autodocTrimNameRegex = SettingKey[String]("autodocTrimNameRegex")
    val autodocToc = SettingKey[Boolean]("autodocToc")
    val autodocInitialize = TaskKey[Unit]("autodocInitialize")
    val autodocMarkdown = SettingKey[Boolean]("autodocMarkdown")
    val autodocHtml = SettingKey[Boolean]("autodocHtml")

    object AutodocDefault {
      val outputDir = "doc"
      val enable = false
      val trim = "(Test|Spec)$"
      val toc = false
      val markdown = true
      val html = false
    }

    val autodocSettings: Seq[Setting[_]] = Seq(
      testFrameworks := testFrameworks.value
        .map(f => {
          val names = f.implClassNames.filter(_ != "dog.DogFramework")
          if(names.isEmpty) None
          else Some(new TestFramework(names: _*))
        })
        .collect { case Some(f) => f }
        ++ Seq(TestFramework("dog.autodoc.AutodocFramework"))
      ,
      autodocOutputDirectory := AutodocDefault.outputDir,
      autodocInitialize := {
        if(autodocEnable.value) Task.deleteFile(baseDirectory.value, autodocOutputDirectory.value)
        if(autodocEnable.value && autodocToc.value) {
          val outputDir = file(autodocOutputDirectory.value)
          if(!outputDir.exists()) IO.createDirectory(outputDir)
          val toc = outputDir / Markdown.tocFileName
          IO.write(toc, Markdown.tocTitle)
        }
      },
      test <<= (test in Test) dependsOn (autodocInitialize),
      autodocEnable := AutodocDefault.enable,
      autodocMarkdown := AutodocDefault.markdown,
      autodocHtml := AutodocDefault.html,
      autodocTrimNameRegex := AutodocDefault.trim,
      autodocToc := AutodocDefault.toc,
      testListeners ++= {
        if(autodocEnable.value) Seq(
          if(autodocMarkdown.value) {
            Seq(new AutodocMarkdownListener(file(autodocOutputDirectory.value), autodocTrimNameRegex.value))
          }
          else Nil,
          if(autodocHtml.value) {
            Seq(new AutodocHtmlListener(file(autodocOutputDirectory.value), autodocTrimNameRegex.value))
          }
          else Nil
        )
        else Nil
      }.flatten,
      libraryDependencies += "com.github.pocketberserker" %% "dog-autodoc" % autodocVersion.value % "test"
    )
  }

  object Task {

    def deleteFile(base: File, filename: String): Unit = IO.delete(base / filename)
  }
}
