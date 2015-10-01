package com.example

import httpz._
import argonaut._, Argonaut._
import dog._, autodoc._

final case class FakeInterpreter(
  body: ByteArray,
  status: Int,
  headers: Map[String, List[String]]) extends InterpretersTemplate {

  protected[this] def request2response(req: Request) =
    Response(body, status, headers)
}

object Test1 extends DogAutodoc {

  def str(value: String) = new ByteArray(value.getBytes())

  def interpreter(value: String, status: Int) =
    FakeInterpreter(str(value), status, Map()).sequential.empty

  case class Person(name: String, age: Int) extends JsonToString[Person]

  implicit val personCodec: CodecJson[Person] = casecodec2(Person.apply, Person.unapply)("name", "age")

  val getPerson = Autodoc.json[Person](Request(
      method = "GET",
      url = "http://localhost/person/1"
    )).leftMap(httpz.Error.http).nel

  val `GET /person/:id` = Autodoc[Person](interpreter(Person("Alice", 17).toString, 200), getPerson) { res =>
    Assert.equal(200, res.status)
  }
}

