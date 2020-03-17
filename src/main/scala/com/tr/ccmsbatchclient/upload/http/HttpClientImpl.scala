package com.tr.ccmsbatchclient.upload.http

import java.io.File

import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import spray.json.{RootJsonFormat, _}

import scala.concurrent.{ExecutionContext, Future}

class HttpClientImpl(actorMaterializer: ActorMaterializer,
                     exContext: ExecutionContext,
                     httpExecutor: HttpExt) extends HttpClient {

  implicit val materializer: ActorMaterializer = actorMaterializer
  implicit val executionContext: ExecutionContext = exContext

  val http: HttpExt = httpExecutor

  override def post[C, T](url: String, body: C, accessToken: String)(implicit parser: RootJsonFormat[T],
                                                                     writer: RootJsonFormat[C]): Future[Option[T]] = {
    val request: HttpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = url,
      entity = RequestFactory.createApplicationJsonEntity(body)
    )

    executeRequest[T](request, accessToken)
  }

  override def multipartPost[T](url: String, body: File, accessToken: String)(implicit parser: RootJsonFormat[T]): Future[Option[T]] = {
    val request: HttpRequest = HttpRequest(
      method = HttpMethods.POST,
      uri = url,
      entity = RequestFactory.createMutlipartEntity(body)
    )

    executeRequest(request, accessToken)
  }

  override def get[T](url: String, accessToken: String)(implicit parser: RootJsonFormat[T]): Future[Option[T]] = {
    val request: HttpRequest = RequestBuilding.Get(uri = url)

    executeRequest(request, accessToken)
  }

  def executeRequest[T](request: HttpRequest, accessToken: String)(implicit parser: RootJsonFormat[T]): Future[Option[T]] = {
    val requestForExecuting = request
      .withHeaders(Authorization(OAuth2BearerToken(accessToken)))

    http.singleRequest(requestForExecuting).flatMap (response => {
      response.status match {
        case StatusCodes.OK => Unmarshal(response.entity).to[String].map { jsonString => Option(jsonString.parseJson.convertTo[T]) }
        case StatusCodes.Created => Unmarshal(response.entity).to[String].map { jsonString => Option(jsonString.parseJson.convertTo[T]) }
        case StatusCodes.Accepted => Unmarshal(response.entity).to[String].map { jsonString => Option(jsonString.parseJson.convertTo[T]) }
        case StatusCodes.NotFound => Future.successful(Option.empty)
        case _ => Future.failed(new Exception(s"Server return an error. Status Code: ${response.status.intValue()}, ${response.entity}"))
      }
    })
  }
}
