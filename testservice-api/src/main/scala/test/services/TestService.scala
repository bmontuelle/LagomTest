package test.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait TestService extends Service {
  def test(): ServiceCall[LookupQuery, Source[String, NotUsed]]

  override final def descriptor = {
    import Service._
    named("DocsStore")
      .withCalls(
        call(test())
      )
  }
}

case class LookupQuery(value: String)

object LookupQuery {
  implicit val format: Format[LookupQuery] = Json.format[LookupQuery]
}



case class ResultData(uploadId: String, length: Long)

object ResultData {
  implicit val format: Format[ResultData] = Json.format[ResultData]
}

