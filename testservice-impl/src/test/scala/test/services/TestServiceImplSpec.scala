package test.services
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{AsyncWordSpec, MustMatchers}
import testtest.services.TestServiceApplication

class TestServiceImplSpec extends AsyncWordSpec with MustMatchers {

  "TestService" should {
    "provide result" in ServiceTest.withServer(ServiceTest.defaultSetup) { ctx =>
      new TestServiceApplication(ctx) with LocalServiceLocator
    }{ server =>

      implicit val system = server.application.actorSystem
      implicit val mat = ActorMaterializer()

      val client = server.serviceClient.implement[TestService]

      //any string passed returns an single element string Source
      val eventualSource = client.test().invoke(LookupQuery("a"))
      val assertion1 = eventualSource.flatMap { src =>
        src.runWith(Sink.head).map { string =>
          string mustEqual "fixed"
        }
      }

      //string 'fail' should throw an exception
      val eventualSource2 = client.test().invoke(LookupQuery("fail"))

      ScalaFutures.whenReady(eventualSource2.failed, Timeout(Span(5, Seconds))) { e =>
        e mustBe a[Throwable]
      }

    }
  }

}
