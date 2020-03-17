package com.tr.ccmsbatchclient.upload.http

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import spray.json.RootJsonFormat

import scala.concurrent.Future

trait HttpClient {

  def post[C, T](url: String, body: C, accessToken: String)(implicit parser: RootJsonFormat[T], writer: RootJsonFormat[C]): Future[Option[T]]

  def multipartPost[T](url: String, body: File, accessToken: String)(implicit parser: RootJsonFormat[T]): Future[Option[T]]

  def get[T](url: String, accessToken: String)(implicit parser: RootJsonFormat[T]): Future[Option[T]]
}

object HttpClient {
  def apply(implicit actorSystem: ActorSystem): HttpClient = new HttpClientImpl(ActorMaterializer(), actorSystem.dispatcher, Http())
}
