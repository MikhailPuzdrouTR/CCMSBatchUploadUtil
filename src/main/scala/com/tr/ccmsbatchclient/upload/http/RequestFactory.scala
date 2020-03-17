package com.tr.ccmsbatchclient.upload.http

import java.io.File
import java.nio.file.Paths

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, MediaTypes, Multipart, RequestEntity}
import akka.stream.scaladsl.{FileIO, Source}
import spray.json.RootJsonFormat
import spray.json._

object RequestFactory {

  def createMutlipartEntity(file: File): RequestEntity = {
    Multipart.FormData(
      Source.single(
        Multipart.FormData.BodyPart(
          "file",
          HttpEntity(
            MediaTypes.`application/octet-stream`,
            file.length(),
            FileIO.fromPath(Paths.get(file.getAbsolutePath), chunkSize = 100000) // the chunk size here is currently critical for performance
          ),
          Map("filename" -> file.getName)
        )
      )
    ).toEntity()
  }

  def createApplicationJsonEntity[T](content: T)(implicit writer: RootJsonFormat[T]): RequestEntity = HttpEntity(ContentTypes.`application/json`, content.toJson.toString)
}
