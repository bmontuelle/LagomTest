package test.services
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
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
      val src = Source("A"::"B"::"C"::Nil)
      val eventualSource = client.test().invoke(src)
      eventualSource.flatMap { src =>
        src.runWith(Sink.head).map { string =>
          string mustEqual "fixed"
        }
      }
    }
  }

}
