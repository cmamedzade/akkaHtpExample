import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import zamblauskas.csv.parser._

import java.time.LocalDate

final case class Report(name: String, value: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Report)
}

class JsonModel extends Directives with JsonSupport {

  case class Data(speaker: String, topic: String, date: String, words: Int)
  val source = scala.io.Source.fromFile("src/main/resources/politics_en.csv")
  val data = source.getLines.mkString.stripMargin

  val a: String = """Speaker, Topic, Date, Words
             |Alexander Abel, Education Policy, 2012-10-30, 5310
             |Bernhard Belling, Coal Subsidies, 2012-11-05, 1210
             |Caesare Collins, Coal Subsidies, 2012-11-06, 1119
             |Alexander Abel, Internal Security, 2012-12-11, 1911""".stripMargin
  source.close

  val result: Either[Parser.Failure, Seq[Data]] = Parser.parse[Data](data)

  def getMostSpeech(report: Either[Parser.Failure, Seq[Data]]) = {
    report match {
      case Left(_) => Report("mostSpeech", "null")
      case Right(value) =>
        val res = value.filter(p => p.date.take(4) == "2013").maxBy(p => p.words)
        Report("mostSpeech",res.speaker)
    }
  }

  def getMostSecurity(report: Either[Parser.Failure, Seq[Data]]) = {
    report match {
      case Left(_) => Report("mostSecurity", "null")
      case Right(value) =>
        val res = value.filter(p => p.topic == "Internal Security").maxBy(p => p.words)
        Report("mostSecurity",res.speaker)
    }
  }

  def getFewestWord(report: Either[Parser.Failure, Seq[Data]]) = {
    report match {
      case Left(_) => Report("leastWordy", "null")
      case Right(value) =>
        val res = value.minBy(p => p.words)
        Report("leastWordy",res.speaker)
    }
  }

  val route =
    concat(
      get {
        path("mostSpeech") {
          complete(getMostSpeech(result))
        }
      },
      get {
        path("mostSecurity") {
          complete(getMostSecurity(result))
        }
      },
      get {
        path("leastWordy") {
          complete(getFewestWord(result))
        }
      }
    )
}
