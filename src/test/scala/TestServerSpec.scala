import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.examlpe.JsonModel

import scala.concurrent.duration._
import scala.language.postfixOps

class TestServerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest{

  "The service" should {

    implicit val timeout = RouteTestTimeout(2 seconds)

    implicit val system: ActorSystem[_] = ActorSystem(Behaviors.empty,"my-test")
    val jsonModel = new JsonModel()
    "return a greeting for GET requests to the root path" in {
      Get("/mostSpeech") ~> jsonModel.route ~> check {
        responseAs[String] shouldEqual "{\"name\":\"mostSpeech\",\"value\":\"null\"}"
      }
    }

    "return a 'PONG!' response for GET requests to /ping" in {
      Get("/mostSecurity") ~> jsonModel.route ~> check {
        responseAs[String] shouldEqual "{\"name\":\"mostSecurity\",\"value\":\"Alexander Abel\"}"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/leastWordy") ~> jsonModel.route ~> check {
        responseAs[String] shouldEqual "{\"name\":\"leastWordy\",\"value\":\"Caesare Collins\"}"
      }
    }
  }

}
