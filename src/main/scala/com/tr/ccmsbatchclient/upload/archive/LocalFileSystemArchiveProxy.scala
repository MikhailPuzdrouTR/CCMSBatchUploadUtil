package com.tr.ccmsbatchclient.upload.archive

import java.io.File

import com.tr.ccmsbatchclient.domain.LocalDataStorage

class LocalFileSystemArchiveProxy(dataStorage: LocalDataStorage) extends ArchiveProxy {

  val localDataStorage: LocalDataStorage = dataStorage

  override def processArchive(processFunction: File => Any): Any = {
    val archive: File = new File(localDataStorage.path.getPath)
    processFunction(archive)
  }
}
