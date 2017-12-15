package testtest.services

import java.io.File

import akka.NotUsed
import akka.event.slf4j.Logger
import akka.stream.{IOResult, KillSwitches, Materializer}
import akka.stream.scaladsl.{FileIO, Flow, Keep, Source}
import akka.util.ByteString
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.api.{ServiceCall, ServiceLocator}
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents
import test.services._

import scala.collection.immutable.Seq
import scala.concurrent.{ExecutionContext, Future}

class TestServiceImpl()(
  implicit val materializer: Materializer,
  implicit val ec: ExecutionContext
) extends TestService {
  val logger = Logger(getClass.getName)

  override def test(): ServiceCall[LookupQuery, Source[String, NotUsed]] = ServiceCall { (q: LookupQuery) =>
    if (q.value == "fail") {
      Future.failed(new IllegalArgumentException("query cannot equals 'fail'"))
    }
    else {
      Future.successful(Source.single("fixed"))
    }
  }
}

class TestServiceServiceApplicationLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new TestServiceApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new TestServiceApplication(context) with LagomDevModeComponents

  override def describeServices = List(
    readDescriptor[TestService]
  )
}

abstract class TestServiceApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {


  private val serviceFactory: TestServiceImpl = wire[TestServiceImpl]
  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[TestService](serviceFactory)

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry = TestServiceSerializerRegistry
}

object TestServiceSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[ResultData]
  )
}