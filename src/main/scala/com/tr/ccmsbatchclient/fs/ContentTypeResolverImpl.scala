package com.tr.ccmsbatchclient.fs

import java.io.{File, FileInputStream}
import java.util.zip.{ZipEntry, ZipInputStream}

import spray.json._

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import com.tr.ccmsbatchclient.util.MapJsonProtocols._

class ContentTypeResolverImpl extends ContentTypeResolver {

  override def resolveContentTypeOfCorpus(pathToCorpus: String): String = {
    val file = new File(pathToCorpus)

    val zipInputStream = new ZipInputStream(new FileInputStream(file))
    val contentType = Stream
      .continually(zipInputStream.getNextEntry)
      .find(zipEntry => zipEntry.getName.startsWith("meta") && zipEntry.getName.endsWith(".json"))
      .map(_ => readZipEntryToString(zipInputStream))
      .map(metaFileContent => metaFileContent.parseJson.convertTo[Map[String, Any]])
      .map(meta => meta("contentType").toString)
      .getOrElse("")

    zipInputStream.close()
    contentType
  }

  def readZipEntryToString(is: ZipInputStream): String = {
    val os = new ByteOutputStream()
    val bufferSize = 2048
    val buffer: Array[Byte] = new Array[Byte](bufferSize)

    Stream.continually(is.read(buffer, 0, bufferSize))
      .takeWhile(readBytes => readBytes > 0)
      .foreach(readBytes => os.write(buffer, 0, readBytes))

    new String(os.toByteArray)
  }
}