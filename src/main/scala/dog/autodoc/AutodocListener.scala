package dog
package autodoc

import sbt._
import scala.collection.mutable.ListBuffer
import scala.util.DynamicVariable
import testing.{ Event => TEvent, Status => TStatus, OptionalThrowable, TestSelector }

class AutodocListener(var outputDir: File, trimRegex: String) extends TestsListener {

  class TestSuite(val name: String) {
    import dog._, autodoc._
    val events: ListBuffer[TEvent] = new ListBuffer()

    def addEvent(e: TEvent) = events += e
  }

  val testSuite = new DynamicVariable(null: TestSuite)

  override def doInit(): Unit = {}

  override def startGroup(name: String): Unit = testSuite.value_=(new TestSuite(name))

  override def testEvent(event: TestEvent): Unit = for (e <- event.detail) { testSuite.value.addEvent(e) }

  override def endGroup(name: String, t: Throwable) = {}

  override def endGroup(name: String, result: TestResult.Value): Unit = {
    write()
  }

  private[this] def write() = {
    if(!outputDir.exists()) IO.createDirectory(outputDir)
    testSuite.value.events.groupBy(e => e.fullyQualifiedName)
      .foreach { case (name, es) => {
        val fileName = name.split('.').last.replaceAll(trimRegex, "")
        val f = outputDir / s"$fileName.md"
        // XXX
        val doc = es.collect { case e if e.status == TStatus.Success && e.throwable.isDefined =>
          e.throwable.get.getMessage
        }
          .mkString("\n\n")
        if(!doc.isEmpty) {
          IO.write(f, if(f.exists()) s"\n\ndoc" else doc, java.nio.charset.Charset.forName("UTF-8"), true)
          val path = f.absolutePath.replaceFirst(outputDir.absolutePath, "")
          val toc = Markdown.generateToc(path, doc)
          IO.write(outputDir / Markdown.tocFileName, toc, java.nio.charset.Charset.forName("UTF-8"), true)
        }
      }}
  }

  override def doComplete(finalResult: TestResult.Value) = {}
}

