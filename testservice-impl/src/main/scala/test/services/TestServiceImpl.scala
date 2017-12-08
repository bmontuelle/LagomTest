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

  override def test(): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]] = ServiceCall { (source: Source[String, NotUsed]) =>
    val outFile = new File(System.getProperty("java.io.tmpdir") + "/test.txt")

    val sink = FileIO.toPath(outFile.toPath)

val result =
  source
    .takeWhile(_ != "EOS")
    .map { s => ByteString(s) }
    .toMat(sink)(Keep.right)
    .mapMaterializedValue { _.map { _ => Source.single("fixed") } }
    .run()

    result.onSuccess{
      case rd: Source[String, _] => logger.info(s"Result data created $rd")
    }
    result.onFailure({
      case t: Throwable => logger.error("Future failed", t)
    })

    result
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