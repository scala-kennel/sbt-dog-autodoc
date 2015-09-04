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
    testSuite.value.events.groupBy(e => e.fullyQualifiedName)
      .foreach { case (name, es) => {
        if(!outputDir.exists()) IO.createDirectory(outputDir)
        val fileName = name.split('.').last.replaceAll(trimRegex, "")
        val f = outputDir / s"$fileName.md"
        val doc = es.filter(e => e.status == TStatus.Success && e.throwable.isDefined)
          // XXX
          .map(_.throwable.get.getMessage)
          .mkString("\n\n")
        IO.write(f, if(f.exists()) "\n\n" + doc else doc, java.nio.charset.Charset.forName("UTF-8"), true)
      }}
  }

  override def doComplete(finalResult: TestResult.Value) = {}
}

