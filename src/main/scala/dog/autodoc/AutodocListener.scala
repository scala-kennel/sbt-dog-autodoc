package dog
package autodoc

import sbt._
import scala.collection.mutable.ListBuffer
import scala.util.DynamicVariable
import testing.{ Event => TEvent }

abstract class AutodocListener extends TestsListener {

  class TestSuite(val name: String) {
    val events: ListBuffer[TEvent] = new ListBuffer()
    def addEvent(e: TEvent) = events += e
  }

  def write(suite: TestSuite): Unit

  private[this] val testSuite = new DynamicVariable(null: TestSuite)

  override def doInit(): Unit = {}

  override def startGroup(name: String): Unit = testSuite.value_=(new TestSuite(name))

  override def testEvent(event: TestEvent): Unit = for (e <- event.detail) { testSuite.value.addEvent(e) }

  override def endGroup(name: String, t: Throwable) = {}

  override def endGroup(name: String, result: TestResult.Value) = {
    write(testSuite.value)
  }

  override def doComplete(finalResult: TestResult.Value) = {}
}
