package dog
package autodoc

object Markdown {

  val tocTitle = "## Table of Contents\n"

  val tocFileName = "toc.md"

  private[this] def toGithubName(line: String): String =
    line.replaceAll("\\s", "-").replaceAll("""[!"#\$%&'\(\)\*\+,\.\/:;<=>\?@\[\\\]^`{\|}~]""", "")

  def generateToc(filePath: String, contents: String): String = {
    val cs = contents.split("\n").collect { case l if l.startsWith("## ") => {
      val title = l.dropWhile(_ == '#').trim
      val id = toGithubName(title.toLowerCase)
      s"    * [$title]($filePath#$id)"
    }}
      .mkString("\n")
    s"* [$filePath]($filePath)\n$cs\n"
  }
}
