package test.services

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.{Service, ServiceCall}
import play.api.libs.json.{Format, Json}

trait TestService extends Service {
  def test(): ServiceCall[Source[String, NotUsed], Source[String, NotUsed]]

  override final def descriptor = {
    import Service._
    named("DocsStore")
      .withCalls(
        call(test())
      )
  }
}


case class ResultData(uploadId: String, length: Long)

object ResultData {
  implicit val format: Format[ResultData] = Json.format[ResultData]
}
