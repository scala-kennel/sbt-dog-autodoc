package dog
package autodoc

import sbt._
import testing.{ Event => TEvent, Status => TStatus }
import java.nio.file.Files
import scala.collection.JavaConverters._

final class AutodocHtmlListener(var outputDir: File, trimRegex: String) extends AutodocListener {

  val utf8 = java.nio.charset.Charset.forName("UTF-8")

  override def write(testSuite: TestSuite) = {
    if(!outputDir.exists()) IO.createDirectory(outputDir)
    testSuite.events.groupBy(e => e.fullyQualifiedName)
      .foreach { case (name, es) => {
        val fileName = name.split('.').last.replaceAll(trimRegex, "")
        val f = outputDir / s"$fileName.html"
        // XXX
        val doc = es.collect { case e if e.status == TStatus.Success && e.throwable.isDefined =>
          e.throwable.get.getCause.getMessage
        }
          .mkString("\n\n")
        if(!doc.isEmpty) {
          IO.write(f, if(f.exists()) s"\n\n$doc" else doc, utf8, true)
        }
      }}
  }

  override def doComplete(finalResult: TestResult.Value) = {
    val toc = outputDir.listFiles()
      .filter(file => file.isFile && file.absolutePath.endsWith(".html"))
      .map(file => {
        val path = file.absolutePath.replaceFirst(outputDir.absolutePath, "")
        val lines = Files.readAllLines(file.toPath).asScala
        IO.write(file, Html.appendTemplate(lines.mkString("\n")), utf8)
        Html.generateTocBody(path, lines.filter(_.startsWith("<h2>")))
      })
      .mkString("\n")
    IO.write(outputDir / Html.tocFileName,
      Html.appendTemplate(s"<h2>Table of Contents</h2>\n\n<ul>$toc</ul>"), utf8)
  }
}
