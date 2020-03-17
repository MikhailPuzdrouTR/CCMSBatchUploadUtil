package com.tr.ccmsbatchclient.fs

import java.io.{File, FileInputStream, FileOutputStream}
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}

import com.typesafe.scalalogging.LazyLogging

class ZipPreuploadProcessorImpl extends ZipPreuploadProcessor with LazyLogging {
  override def beforeUpload(pathToZip: String): Unit = {
    logger.info(s"Start to prepare ${pathToZip} before uploading")

    val inputZipFile = new File(pathToZip)
    val outputZipFile = new File(pathToZip + ".temp")

    val zipInputStream = new ZipInputStream(new FileInputStream(inputZipFile))
    val zipOutputStream = new ZipOutputStream(new FileOutputStream(outputZipFile))

    Stream.continually(zipInputStream.getNextEntry)
      .takeWhile(readZipEntry => readZipEntry != null)
      .foreach(readZipEntry => copyZipEntry(readZipEntry, zipInputStream, zipOutputStream))

    zipInputStream.close()
    zipOutputStream.close()

    inputZipFile.delete()
    outputZipFile.renameTo(new File(pathToZip))

    logger.info(s"Finish to prepare ${pathToZip} before uploading")
  }

  def copyZipEntry(zipEntry: ZipEntry, zipInputStream: ZipInputStream, zipOutputStream: ZipOutputStream): Unit = {
    val newZipEntryName = zipEntry.getName.replaceAll("\\\\", "/")
    zipOutputStream.putNextEntry(new ZipEntry(newZipEntryName))

    val bufferSize = 2048 * 8
    val buffer: Array[Byte] = new Array[Byte](bufferSize)

    Stream.continually(zipInputStream.read(buffer, 0, bufferSize))
      .takeWhile(readBytes => readBytes > 0)
      .foreach(readBytes => zipOutputStream.write(buffer, 0, readBytes))
  }
}
