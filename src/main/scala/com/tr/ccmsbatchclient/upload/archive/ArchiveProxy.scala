package com.tr.ccmsbatchclient.upload.archive

import java.io.File

import com.tr.ccmsbatchclient.domain.{DataStorage, LocalDataStorage}

trait ArchiveProxy {

  def processArchive(processFunction: File => Any): Any
}

object ArchiveProxy {

  def apply(dataStorage: DataStorage): ArchiveProxy = {
    dataStorage match {
      case localDataStorage: LocalDataStorage => new LocalFileSystemArchiveProxy(localDataStorage)
    }
  }
}
