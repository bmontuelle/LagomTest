package test.services
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
import com.lightbend.lagom.scaladsl.testkit.ServiceTest
import org.scalatest.{AsyncWordSpec, Matchers}
import testtest.services.TestServiceApplication
/**
  * Created by misher on 6/16/17.
  */
class TestServiceImplSpec extends AsyncWordSpec with Matchers {
  "TestService" should {
    "provide result" in ServiceTest.withServer(ServiceTest.defaultSetup) { ctx =>
      new TestServiceApplication(ctx) with LocalServiceLocator
    }{server =>
      val client = server.serviceClient.implement[TestService]
      val src = Source("A"::"B"::"C"::Nil)
      val resultDataFtr = client.test().invoke(src)
      resultDataFtr.map{resultData=>
        resultData.uploadId should === ("TestResult")
      }
    }
  }

}
