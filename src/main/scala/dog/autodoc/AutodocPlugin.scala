package dog
package autodoc

import sbt._, Keys._

object AutodocPlugin extends AutoPlugin {

  object autoImport {
    val autodocVersion = SettingKey[String]("autodocVersion")

    // TODO: implement Task to clean directory

    val autodocSettings: Seq[Setting[_]] = Seq(
      testFrameworks += new TestFramework("dog.autodoc.AutodocFramework"),
      libraryDependencies += "com.github.pocketberserker" %% "dog-autodoc" % autodocVersion.value % "test"
    )
  }
}
