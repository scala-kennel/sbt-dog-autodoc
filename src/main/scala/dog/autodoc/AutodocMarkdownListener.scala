package dog
package autodoc

import sbt._
import testing.{ Event => TEvent, Status => TStatus }

final class AutodocMarkdownListener(var outputDir: File, trimRegex: String) extends AutodocListener {

  override def write(testSuite: TestSuite) = {
    if(!outputDir.exists()) IO.createDirectory(outputDir)
    testSuite.events.groupBy(e => e.fullyQualifiedName)
      .foreach { case (name, es) => {
        val fileName = name.split('.').last.replaceAll(trimRegex, "")
        val f = outputDir / s"$fileName.md"
        // XXX
        val doc = es.collect { case e if e.status == TStatus.Success && e.throwable.isDefined =>
          e.throwable.get.getMessage
        }
          .mkString("\n\n")
        if(!doc.isEmpty) {
          IO.write(f, if(f.exists()) s"\n\n$doc" else doc, java.nio.charset.Charset.forName("UTF-8"), true)
          val path = f.absolutePath.replaceFirst(outputDir.absolutePath, "")
          val toc = Markdown.generateToc(path, doc)
          IO.write(outputDir / Markdown.tocFileName, toc, java.nio.charset.Charset.forName("UTF-8"), true)
        }
      }}
  }
}
