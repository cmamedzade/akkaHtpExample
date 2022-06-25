package org.examlpe

import akka.Done
import akka.actor.typed.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.FileIO
import spray.json._

import java.nio.file.Paths
import java.time.LocalDate
import scala.concurrent.Future

final case class Report(name: String, value: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Report)
}

class JsonModel(implicit val system: ActorSystem[_]) extends Directives with JsonSupport {
  case class Data(speaker: String, topic: String, date: LocalDate, words: Int)


  implicit val ex = system.executionContext
  var buffer = collection.mutable.ListBuffer.empty[Data]

  def container(data: Data) = {
    buffer.append(data)
  }

  def toCaseClass(map: Map[String, String]) = {
    Data(map("Speaker"),map("Topic"),LocalDate.parse(map("Date")), map("Words").toInt)
  }


  def getData() = {
    FileIO.fromPath(Paths.get("src/main/resources/politics_en.csv"))
      .via(CsvParsing.lineScanner())
      .via(CsvToMap.toMap())
      .map(_.view.mapValues(_.utf8String).map(x => (x._1.trim, x._2.trim)))
      .map(p => container(toCaseClass(p.toMap)))
      .run
      .recover({
        case ex: Exception =>
          println(ex)
          Done
      })
  }

  def getMostSpeech(): Future[Report] = {
    getData()
      .map{ _ =>
        val r = buffer.filter(dat => dat.date.getYear == 2013).maxBy(x => x.words)
        buffer.empty
        Report("mostSpeech",r.speaker)
      }
      .recover({
        case ex: Exception =>
          println(ex)
          Report("mostSpeech","null")
      })
  }

  def getMostSecurity() = {
    getData()
      .map{ _ =>
        val r = buffer.filter(dat => dat.topic == "Internal Security").maxBy(x => x.words)
        buffer.empty
        Report("mostSecurity",r.speaker)
      }
      .recover({
        case ex: Exception =>
          println(ex)
          Report("mostSecurity","null")
      })
  }

  def getFewestWord() = {
    getData()
      .map{ _ =>
        val r = buffer.minBy(_.words)
        buffer.empty
        Report("leastWordy",r.speaker)
      }
      .recover({
      case ex: Exception =>
        println(ex)
        Report("leastWordy","null")
    })
  }

  val route =
    concat(
      get {
        path("mostSpeech") {
          complete(getMostSpeech())
        }
      }
      ,
      get {
        path("mostSecurity") {
          complete(getMostSecurity())
        }
      },
      get {
        path("leastWordy") {
          complete(getFewestWord())
        }
      }
    )
}
